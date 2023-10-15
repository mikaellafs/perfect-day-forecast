package com.example.perfectdayforecast.collector.datagateways

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import com.example.perfectdayforecast.collector.models.Units
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

class MemoryCacheGatewayTest {
    private lateinit var cacheGateway: MemoryCacheGateway

    @BeforeEach
    fun setUp() {
        cacheGateway = MemoryCacheGateway()
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