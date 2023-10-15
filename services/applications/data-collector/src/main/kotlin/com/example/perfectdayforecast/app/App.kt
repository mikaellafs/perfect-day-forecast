
package com.example.perfectdayforecast.app

import com.example.perfectdayforecast.collector.datagateways.RedisCacheGateway
import com.example.perfectdayforecast.collector.datagateways.WeatherDataGateway
import com.example.perfectdayforecast.collector.handlers.WeatherForecastApiHandler
import com.example.perfectdayforecast.collector.handlers.WeatherForecastCacheHandler
import com.example.perfectdayforecast.collector.handlers.WeatherQueryOrchestrator
import com.example.perfectdayforecast.collector.models.ApiUrl
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import com.example.perfectdayforecast.rabbitsupport.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rabbitmq.client.ConnectionFactory
import io.lettuce.core.RedisClient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import java.net.URI

class App

private val logger = LoggerFactory.getLogger(App::class.java)

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
        routingKeyGenerator = { _: String -> "42" },
        bindingKey = "42",
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
        weatherRequestsQueue
    )
}

fun CoroutineScope.listenForWeatherForecastRequests(
    apiUrl: String,
    dataGateway: WeatherDataGateway,
    connectionFactory: ConnectionFactory,
    weatherAnalyzerExchange: RabbitExchange,
    weatherRequestQueue: RabbitQueue,
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
            val request = MessageWeatherRequest.fromJson(message)
            logger.debug("received weather forecast request for days from {} to {}", request.startDate, request.endDate)
            val forecasts = dataRetrieverOrchestrator.getData(request.location, request.startDate, request.endDate)

            logger.debug("publishing weather forecast results for days from {} to {}", request.startDate, request.endDate)
            publishWeatherForecast(forecastsToJson(forecasts))
        }
    }
}

fun forecastsToJson(forecasts: List<WeatherForecastRegister>): String {
    return Gson().toJson(forecasts, object : TypeToken<List<WeatherForecastRegister>>(){}.type)
}
