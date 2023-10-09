package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate

class BaseHandler {
    private var dataRetriever: WeatherForecastHandler? = null

    constructor(vararg handlers : WeatherForecastHandler) {
        var last: WeatherForecastHandler? = null
        for (h in handlers) {
            last?.setNext(h)
            last = h
        }

        dataRetriever = handlers[0]
    }
    fun getData(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister> {
        val context = WeatherRequestContext(location, startDate, endDate)
        dataRetriever?.next(context)

        return context.response
    }

}