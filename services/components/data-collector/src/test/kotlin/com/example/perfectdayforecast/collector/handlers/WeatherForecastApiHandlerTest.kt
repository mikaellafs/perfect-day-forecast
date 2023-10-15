package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.models.ApiUrl
import com.example.perfectdayforecast.collector.models.Location
import com.github.kittinunf.fuel.core.*
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import io.kotest.assertions.any
import io.mockk.*
import org.junit.Assert.*
import org.junit.jupiter.api.Test
import java.net.URL
import java.time.LocalDate

class WeatherForecastApiHandlerTest {
    @Test
    fun `WeatherForecastApiHandler should fetch data from API and parse it`() {
        val baseUrl = "https://example.com"

        // Mock the response from the API
        val apiResponse = """
            {
                "daily_units": {
                    "time": "iso8601",
                    "weathercode": "wmo code",
                    "temperature_2m_max": "°C",
                    "temperature_2m_min": "°C",
                    "rain_sum": "mm",
                    "snowfall_sum": "cm",
                    "precipitation_sum": "mm",
                    "precipitation_probability_max": "%"
                },
                "daily": {
                    "time": ["2023-10-10", "2023-10-11", "2023-10-12"],
                    "weathercode": [3, 3, 80],
                    "temperature_2m_max": [17.7, 21.8, 18.9],
                    "temperature_2m_min": [11.4, 13.2, 12.5],
                    "rain_sum": [0.00, 0.00, 4.20],
                    "snowfall_sum": [0.00, 0.00, 0.00],
                    "precipitation_sum": [0.00, 0.00, 0.00],
                    "precipitation_probability_max": [32, 32, 26]
                }
            }
        """

        val mHeaders = Headers()
        mHeaders["Content-Type"] = listOf("application/json")

        val mockRequest = mockk<Request>() {
            every { url } returns URL(baseUrl)
            every { headers } returns mHeaders
            every { responseString() } returns ResponseResultOf(this, mockk<Response>(), Result.success(apiResponse))

        }

        val apiUrlMock = mockkClass(ApiUrl::class) {
            every { replace(any(), any()) } returns this
            every { httpGet() } returns mockRequest
        }

        val handler = WeatherForecastApiHandler(apiUrlMock)

        // Set up the context
        val context = WeatherRequestContext(
            location = Location.RIO_DE_JANEIRO,
            startDate = LocalDate.of(2023, 10, 1),
            endDate = LocalDate.of(2023, 10, 3)
        )

        // Call the getData method
        handler.getData(context)

        assertFalse(context.response.isEmpty())
        assertEquals(context.response.size, 3)
    }
}