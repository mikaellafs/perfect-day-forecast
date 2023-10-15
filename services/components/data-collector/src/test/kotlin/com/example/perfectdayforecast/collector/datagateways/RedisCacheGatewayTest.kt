package com.example.perfectdayforecast.collector.datagateways

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import com.example.perfectdayforecast.collector.models.Units
import io.lettuce.core.RedisClient

import io.kotest.core.spec.style.AnnotationSpec
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers

import org.junit.jupiter.api.Assertions.assertEquals

import java.time.LocalDate

@Testcontainers
class RedisCacheGatewayTest : AnnotationSpec() {

    @Container
    var redisContainer = GenericContainer<Nothing>("redis:latest").apply {
        withExposedPorts(6379)
    }

    private lateinit var cacheGateway: RedisCacheGateway

    @BeforeEach
    fun setUp() {
        val redisHost = redisContainer.host
        val redisPort = redisContainer.getMappedPort(6379)

        val redisCommands = RedisClient.create("redis://$redisHost:$redisPort").connect().sync()
        cacheGateway = RedisCacheGateway(redisCommands)
    }

    @Test
    fun testSaveAndRetrieveData() {
        val location = Location.LONDON
        val date = LocalDate.of(2023, 10, 10)

        val data = WeatherForecastRegister(
            Units("iso8601", "wmo code", "mm", "C", "C", "mm", "cm", "%"),
            location,
            date,
            2,
            10.0,
            3.0,
            80.0,
            0.0,
            80.0,
            70.0
        )

        cacheGateway.save(data)
        val retrievedData = cacheGateway.get(location, date)

        assertEquals(data, retrievedData)
    }
}