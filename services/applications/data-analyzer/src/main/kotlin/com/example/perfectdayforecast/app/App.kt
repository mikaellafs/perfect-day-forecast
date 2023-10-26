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

class App

private val logger = LoggerFactory.getLogger(App::class.java)

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
        weatherAnalyzerQueue
    )
}

fun CoroutineScope.listenForWeatherAnalysisRequests(
    dataGateway: DataGateway,
    connectionFactory: ConnectionFactory,
    weatherAnalysisQueue: RabbitQueue,
) {
    val weatherAnalyzer = WeatherAnalyzer(dataGateway)
    launch {
        logger.info("listening for analysis requests")
        val channel = connectionFactory.newConnection().createChannel()

        listen(queue = weatherAnalysisQueue, channel = channel) { message ->
            try {
                val request = PerfectDayRequest.fromJson(message)
                logger.info("received weather analysis request id ${request.requestId} for a ${request.weatherPreference.name} weather condition")

                weatherAnalyzer.analyzeRequest(request)
            } catch (e: Exception) {
                logger.error("Error occurred when processing message \"$message\"", e)
            }
        }
    }
}
