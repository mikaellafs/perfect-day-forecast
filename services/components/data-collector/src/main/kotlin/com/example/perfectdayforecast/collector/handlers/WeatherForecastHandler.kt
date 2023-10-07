package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate

interface WeatherForecastHandler {
    fun getData(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister>?
    fun next(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister>?
    fun setNext(handler: WeatherForecastHandler)
}
