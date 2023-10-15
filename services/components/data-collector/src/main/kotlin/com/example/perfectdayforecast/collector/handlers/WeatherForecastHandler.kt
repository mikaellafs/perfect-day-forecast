package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate

interface WeatherForecastHandler {
    fun getData(context: WeatherRequestContext)
    fun next(context: WeatherRequestContext)
    fun setNext(handler: WeatherForecastHandler)
}
