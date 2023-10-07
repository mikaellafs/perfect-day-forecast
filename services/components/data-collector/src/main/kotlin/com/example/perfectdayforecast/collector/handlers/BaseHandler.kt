package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate

class BaseHandler: WeatherForecastHandler {
    private var first: WeatherForecastHandler? = null

    constructor(vararg handlers : WeatherForecastHandler) {
        var last: WeatherForecastHandler? = null
        for (h in handlers) {
            last?.setNext(h)
            last = h
        }

        first = handlers[0]
    }
    override fun getData(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister>? {
        return first?.next(location, startDate, endDate)
    }

    override fun next(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister>? {
        return null
    }

    override fun setNext(handler: WeatherForecastHandler) {}

}