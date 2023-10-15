package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate

class WeatherRequestContext(
    val location: Location,
    var startDate: LocalDate,
    var endDate: LocalDate,
    var response: List<WeatherForecastRegister> = mutableListOf(),
    var shouldUpdateData: Boolean = false
)