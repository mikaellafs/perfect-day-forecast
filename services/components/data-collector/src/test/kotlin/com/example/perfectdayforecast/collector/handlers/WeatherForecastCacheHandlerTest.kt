package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.datagateways.WeatherDataGateway
import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.Units
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.LocalDate
import org.junit.jupiter.api.Assertions.assertEquals

@ExtendWith(MockKExtension::class)
class WeatherForecastCacheHandlerTest {

    @MockK
    private lateinit var dataGateway: WeatherDataGateway

    @MockK
    private lateinit var nextHandler: WeatherForecastHandler

    @Test
    fun `WeatherForecastCacheHandler should retrieve data from cache`() {
        // When
        val context = WeatherRequestContext(
            location = Location.LOS_ANGELES,
            startDate = LocalDate.of(2023, 10, 1),
            endDate = LocalDate.of(2023, 10, 3)
        )
        val register = WeatherForecastRegister(
            Units("iso8601", "wmo code", "mm", "C", "C", "mm", "cm", "%"),
            context.location,
            context.startDate,
            2,
            10.0,
            3.0,
            80.0,
            0.0,
            80.0,
            70.0
        )
        val cacheHandler = WeatherForecastCacheHandler(dataGateway, nextHandler)

        every { dataGateway.get(any(), any()) } returns register
        every { nextHandler.getData(any()) } returns Unit

        // Do
        cacheHandler.getData(context)

        // Assert
        val expectedResponse = listOf(register, register, register)
        assertEquals(expectedResponse, context.response)
    }

    @Test
    fun `WeatherForecastCacheHandler should skip cache when data is not available`() {
        // When
        val context = WeatherRequestContext(
            location = Location.LOS_ANGELES,
            startDate = LocalDate.of(2023, 10, 1),
            endDate = LocalDate.of(2023, 10, 3)
        )
        val cacheHandler = WeatherForecastCacheHandler(dataGateway, nextHandler)

        every { dataGateway.get(any(), any()) } returns null
        every { nextHandler.getData(any()) } returns Unit

        // Do
        cacheHandler.getData(context)

        // Assert
        assertEquals(emptyList<WeatherForecastRegister>(), context.response)
    }
}

