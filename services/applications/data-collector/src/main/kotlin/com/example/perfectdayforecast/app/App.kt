
package com.example.perfectdayforecast.app

import com.example.perfectdayforecast.collector.handlers.WeatherForecastApiHandler
import com.example.perfectdayforecast.collector.handlers.WeatherRequestContext
import com.example.perfectdayforecast.collector.models.Location
import java.time.LocalDate

fun main() {
    println("Hello from Data Collector")
    val baseUrl = "https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&daily=weathercode,temperature_2m_max,temperature_2m_min,rain_sum,snowfall_sum,precipitation_sum,precipitation_probability_max&timezone=auto&start_date={start_date}&end_date={end_date}"
    val handler = WeatherForecastApiHandler(baseUrl)
    val context = WeatherRequestContext(Location.TOKYO, LocalDate.of(2023, 10, 8), LocalDate.of(2023, 10, 10))
    handler.getData(context)

    print(context.response.toString())
}
