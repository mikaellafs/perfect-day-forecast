package com.example.perfectdayforecast.app

import com.example.perfectdayforecast.analyzer.PerfectDayRequest
import com.example.perfectdayforecast.analyzer.WeatherAnalyzer
import com.example.perfectdayforecast.rabbitsupport.*
import java.net.URI

import kotlinx.coroutines.CoroutineScope
import com.rabbitmq.client.ConnectionFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val rabbitUrl = System.getenv("RABBIT_URL")?.let(::URI)
        ?: throw RuntimeException("Please set the RABBIT_URL environment variable")
    val weatherAnalyzerQueueName = System.getenv("RABBIT_ANALYZER_QUEUE")
        ?: throw RuntimeException("Please set the RABBIT_ANALYZER_QUEUE environment variable")
    val weatherAnalyzerExchangeName =System.getenv("RABBIT_ANALYZER_EXCHANGE")
        ?: throw RuntimeException("Please set the RABBIT_ANALYZER_EXCHANGE environment variable")

    val connectionFactory = buildConnectionFactory(rabbitUrl)
//    val dataGateway: DataGateway =

    val weatherAnalyzerExchange = RabbitExchange(
        name = weatherAnalyzerExchangeName,
        type = "direct",
        routingKeyGenerator = { _: String -> "43" },
        bindingKey = "43",
    )
    val weatherAnalyzerQueue = RabbitQueue(weatherAnalyzerQueueName)
    connectionFactory.declareAndBind(exchange = weatherAnalyzerExchange, queue = weatherAnalyzerQueue)

    listenForWeatherAnalysisRequests(
//        dataGateway,
        connectionFactory,
        weatherAnalyzerQueue
    )
}

fun CoroutineScope.listenForWeatherAnalysisRequests(
//    dataGateway: WeatherDataGateway,
    connectionFactory: ConnectionFactory,
    weatherAnalysisQueue: RabbitQueue,
) {
//    val weatherAnalyzer = WeatherAnalyzer(dataGateway)

    launch {
        println("listening for analysis requests")
        val channel = connectionFactory.newConnection().createChannel()

        listen(queue = weatherAnalysisQueue, channel = channel) { message ->
            val request = PerfectDayRequest.fromJson(message)
//            logger.debug("received weather forecast request for days from {} to {}", request.startDate, request.endDate)

//            weatherAnalyzer.analyzeRequest(request)
        }
    }
}
