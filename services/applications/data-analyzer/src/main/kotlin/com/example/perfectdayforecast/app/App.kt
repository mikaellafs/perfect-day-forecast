package com.example.perfectdayforecast.app

import com.example.perfectdayforecast.analyzer.PerfectDayRequest
import com.example.perfectdayforecast.analyzer.SQLDataGateway
import com.example.perfectdayforecast.analyzer.WeatherAnalyzer
import com.example.perfectdayforecast.rabbitsupport.*
import com.example.perfectdayforecast.analyzer.DataGateway
import java.net.URI

import kotlinx.coroutines.CoroutineScope
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

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
    val weatherAnalyzerQueueName = System.getenv("RABBIT_ANALYZER_QUEUE")
        ?: throw RuntimeException("Please set the RABBIT_ANALYZER_QUEUE environment variable")
    val weatherAnalyzerExchangeName =System.getenv("RABBIT_ANALYZER_EXCHANGE")
        ?: throw RuntimeException("Please set the RABBIT_ANALYZER_EXCHANGE environment variable")
    val databaseUrl = System.getenv("DATABASE_URL")
        ?: throw RuntimeException("Please set the DATABASE_URL environment variable")
        
    val dbConfig = DatabaseConfiguration(databaseUrl)

    val connectionFactory = buildConnectionFactory(rabbitUrl)
    val dataGateway: DataGateway = SQLDataGateway(dbConfig.db)

    val weatherAnalyzerExchange = RabbitExchange(
        name = weatherAnalyzerExchangeName,
        type = "direct",
        routingKeyGenerator = { _: String -> "43" },
        bindingKey = "43",
    )
    val weatherAnalyzerQueue = RabbitQueue(weatherAnalyzerQueueName)
    connectionFactory.declareAndBind(exchange = weatherAnalyzerExchange, queue = weatherAnalyzerQueue)

    listenForWeatherAnalysisRequests(
        dataGateway,
        connectionFactory,
        weatherAnalyzerQueue,
        prometheusRegistry
    )

    val port = System.getenv("PROMETHEUS_METRICS_PORT")?.toIntOrNull() ?: 8080
    embeddedServer(Netty, port = port, module = Application::module).start(wait = false)
}

fun CoroutineScope.listenForWeatherAnalysisRequests(
    dataGateway: DataGateway,
    connectionFactory: ConnectionFactory,
    weatherAnalysisQueue: RabbitQueue,
    prometheusMeterRegistry: PrometheusMeterRegistry
) {
    val weatherAnalyzer = WeatherAnalyzer(dataGateway)
    launch {
        logger.info("listening for analysis requests")
        val channel = connectionFactory.newConnection().createChannel()

        listen(queue = weatherAnalysisQueue, channel = channel) { message ->
            try {
                val request = PerfectDayRequest.fromJson(message)
                prometheusMeterRegistry.counter("analysis_requests").increment()

                logger.info("received weather analysis request id ${request.requestId} for a ${request.weatherPreference.name} weather condition")

                weatherAnalyzer.analyzeRequest(request)
                prometheusMeterRegistry.counter("success_analysis_requests").increment()
            } catch (e: Exception) {
                logger.error("Error occurred when processing message \"$message\"", e)
                prometheusMeterRegistry.counter("fail_analysis_requests").increment()
            }
        }
    }
}
