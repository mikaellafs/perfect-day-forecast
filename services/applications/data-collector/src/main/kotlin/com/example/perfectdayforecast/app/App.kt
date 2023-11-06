
package com.example.perfectdayforecast.app

import com.example.perfectdayforecast.collector.datagateways.RedisCacheGateway
import com.example.perfectdayforecast.collector.datagateways.WeatherDataGateway
import com.example.perfectdayforecast.collector.handlers.WeatherForecastApiHandler
import com.example.perfectdayforecast.collector.handlers.WeatherForecastCacheHandler
import com.example.perfectdayforecast.collector.handlers.WeatherQueryOrchestrator
import com.example.perfectdayforecast.collector.models.ApiUrl
import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import com.example.perfectdayforecast.rabbitsupport.*
import com.rabbitmq.client.ConnectionFactory
import io.lettuce.core.RedisClient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.URI

import io.ktor.metrics.micrometer.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

class App

private val logger = LoggerFactory.getLogger(App::class.java)
private val prometheusRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)

fun Application.module() {
    install(MicrometerMetrics) {
        registry = prometheusRegistry
    }

    routing {
        get("/metrics") {
            call.respond(prometheusRegistry.scrape())
        }
    }
}

fun main(): Unit = runBlocking {
    val rabbitUrl = System.getenv("RABBIT_URL")?.let(::URI)
        ?: throw RuntimeException("Please set the RABBIT_URL environment variable")
    val apiUrl = System.getenv("WEATHER_FORECAST_API_URL")
        ?: throw RuntimeException("Please set the WEATHER_FORECAST_API_URL environment variable")
    val redisUrl = System.getenv("REDIS_URL")
        ?: throw RuntimeException("Please set the REDIS_URL environment variable")

    val weatherAnalyzerExchangeName =System.getenv("RABBIT_ANALYZER_EXCHANGE")
        ?: throw RuntimeException("Please set the RABBIT_ANALYZER_EXCHANGE environment variable")
    val weatherAnalyzerQueueName = System.getenv("RABBIT_ANALYZER_QUEUE")
        ?: throw RuntimeException("Please set the RABBIT_ANALYZER_QUEUE environment variable")

    val weatherRequestsExchangeName =System.getenv("RABBIT_REQUESTS_EXCHANGE")
        ?: throw RuntimeException("Please set the RABBIT_REQUESTS_EXCHANGE environment variable")
    val weatherRequestsQueueName = System.getenv("RABBIT_REQUESTS_QUEUE")
        ?: throw RuntimeException("Please set the RABBIT_REQUESTS_QUEUE environment variable")

    val redisClient = RedisClient.create(redisUrl)
    val connection = redisClient.connect()
    val syncCommands = connection.sync()

    val connectionFactory = buildConnectionFactory(rabbitUrl)
    val dataGateway: WeatherDataGateway = RedisCacheGateway(syncCommands)

    val weatherAnalyzerExchange = RabbitExchange(
        name = weatherAnalyzerExchangeName,
        type = "direct",
        routingKeyGenerator = { _: String -> "43" },
        bindingKey = "43",
    )
    val weatherAnalyzerQueue = RabbitQueue(weatherAnalyzerQueueName)

    val weatherRequestsExchange = RabbitExchange(
        name = weatherRequestsExchangeName,
        type = "direct",
        routingKeyGenerator = { _: String -> "42" },
        bindingKey = "42",
    )
    val weatherRequestsQueue = RabbitQueue(weatherRequestsQueueName)

    connectionFactory.declareAndBind(weatherRequestsExchange, weatherRequestsQueue)
    connectionFactory.declareAndBind(weatherAnalyzerExchange, weatherAnalyzerQueue)

    listenForWeatherForecastRequests(
        apiUrl,
        dataGateway,
        connectionFactory,
        weatherAnalyzerExchange,
        weatherRequestsQueue,
        prometheusRegistry
    )

    val port = System.getenv("PROMETHEUS_METRICS_PORT")?.toIntOrNull() ?: 8082
    embeddedServer(Netty, port = port, module = Application::module).start(wait = false)
}

fun CoroutineScope.listenForWeatherForecastRequests(
    apiUrl: String,
    dataGateway: WeatherDataGateway,
    connectionFactory: ConnectionFactory,
    weatherAnalyzerExchange: RabbitExchange,
    weatherRequestQueue: RabbitQueue,
    prometheusMeterRegistry: PrometheusMeterRegistry
) {
    val publishWeatherForecast = publish(connectionFactory, weatherAnalyzerExchange)

    val dataRetrieverOrchestrator = WeatherQueryOrchestrator(
        dataGateway,
        WeatherForecastCacheHandler(dataGateway),
        WeatherForecastApiHandler(ApiUrl(apiUrl))
    )

    launch {
        logger.info("listening for perfect requests")
        val channel = connectionFactory.newConnection().createChannel()

        listen(queue = weatherRequestQueue, channel = channel) { message ->
            try {
                val request = MessageWeatherRequest.fromJson(message)
                prometheusMeterRegistry.counter("weather_forecast_requests").increment()

                println("received weather forecast request for days from ${request.startDate} to ${request.endDate}")
                val forecasts = dataRetrieverOrchestrator.getData(request.location, request.startDate, request.endDate)

                if (forecasts.isNotEmpty()) {
                    println("publishing weather forecast results for days from ${request.startDate} to ${request.endDate}")
                    publishWeatherForecast(generateResultMessage(request.requestId, request.location, request.weatherPreference, forecasts))
                }

                prometheusMeterRegistry.counter("success_weather_forecast_requests").increment()
            } catch (e: Exception) {
                prometheusMeterRegistry.counter("failed_weather_forecast_requests").increment()
                logger.error("Error occurred when processing message \"$message\"", e)
            }

        }
    }
}

fun generateResultMessage(id: Int, location: Location, preference: String, forecasts: List<WeatherForecastRegister>): String {
    val requestResults = forecasts.map { w ->
        WeatherRequestResult(
            w.date,
            w.weathercode,
            w.maxTemperature,
            w.minTemperature,
            w.rainSum,
            w.snowfallSum,
            w.precipitationSum,
            w.precipitationProbabilityMax
        )
    }

    val resultMessage = WeatherRequestResultMessage(
        id, preference, forecasts[0].units, location, requestResults
    )
    val r = resultMessage.toJson()
    return r
}
