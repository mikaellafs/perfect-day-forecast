package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.datagateways.WeatherDataGateway
import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate

class WeatherQueryOrchestrator {
    private var dataRetriever: WeatherForecastHandler? = null
    private val dataGateway: WeatherDataGateway?

    constructor(dataGateway: WeatherDataGateway? = null, vararg handlers : WeatherForecastHandler) {
        this.dataGateway = dataGateway

        var last: WeatherForecastHandler? = null
        for (h in handlers) {
            last?.setNext(h)
            last = h
        }

        dataRetriever = handlers[0]
    }
    fun getData(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister> {
        val context = WeatherRequestContext(location, startDate, endDate)
        dataRetriever?.getData(context)

        if (context.shouldUpdateData) {
            context.response.forEach { register -> dataGateway?.save(register)}
        }

        return context.response
    }
}