package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.datagateways.WeatherDataGateway
import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import io.kotest.assertions.any
import io.mockk.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.jupiter.api.Test
import java.time.LocalDate

class WeatherQueryOrchestratorTest {

    @Test
    fun `getData saves data when shouldUpdateData is true`() {
        val dataGateway = mockk<WeatherDataGateway>() {
            every { save(any()) } returns Unit
        }

        val mockHandler = mockk<WeatherForecastHandler>()
        every { mockHandler.getData(any<WeatherRequestContext>()) } answers {
            val context = firstArg<WeatherRequestContext>()
            context.response = listOf(mockkClass(WeatherForecastRegister::class))
            context.shouldUpdateData = true
        }

        val orchestrator = WeatherQueryOrchestrator(dataGateway, mockHandler)

        val location = Location.SYDNEY
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)
        val result = orchestrator.getData(location, startDate, endDate)

        verify { mockHandler.getData(any<WeatherRequestContext>()) }
        verify { dataGateway.save(any()) }
        assertEquals(result.size, 1)
    }

    @Test
    fun `getData does not update data when shouldUpdateData is false`() {
        val dataGateway = mockk<WeatherDataGateway>()
        val mockHandler = mockk<WeatherForecastHandler>()

        every { mockHandler.getData(any<WeatherRequestContext>()) } answers {
            val context = firstArg<WeatherRequestContext>()
            context.response = emptyList()
            context.shouldUpdateData = false
        }

        val orchestrator = WeatherQueryOrchestrator(dataGateway, mockHandler)

        val location = Location.NEW_YORK
        val startDate = LocalDate.now()
        val endDate = LocalDate.now().plusDays(1)
        val result = orchestrator.getData(location, startDate, endDate)

        verify { mockHandler.getData(any()) }
        verify(exactly = 0) { dataGateway.save(any()) }
        assertTrue(result.isEmpty())
    }
}
