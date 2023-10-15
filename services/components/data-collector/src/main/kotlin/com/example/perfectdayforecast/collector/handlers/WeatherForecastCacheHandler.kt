package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.datagateways.MemoryCacheGateway
import com.example.perfectdayforecast.collector.datagateways.WeatherDataGateway
import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate

class WeatherForecastCacheHandler(private val dataGateway: WeatherDataGateway, private var next: WeatherForecastHandler? = null) :
    BaseHandler() {

    override fun getData(context: WeatherRequestContext) {
        val result: MutableList<WeatherForecastRegister> = mutableListOf()

        var currentDate = context.startDate
        while (currentDate <= context.endDate) {
            var register: WeatherForecastRegister =
                dataGateway.get(context.location, currentDate) ?: return next(context)

            result.add(register)
            currentDate = currentDate.plusDays(1)
        }

        context.shouldUpdateData = false
        context.response = result
        println("cache: " + context.response)
    }
}