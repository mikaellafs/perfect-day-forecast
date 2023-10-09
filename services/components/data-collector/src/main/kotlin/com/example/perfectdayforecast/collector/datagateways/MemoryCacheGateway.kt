package com.example.perfectdayforecast.collector.datagateways

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MemoryCacheGateway : WeatherDataGateway {
    private val cache = mutableMapOf<String, WeatherForecastRegister>()

    private fun generateKey(location: Location, date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return "${location.latitude}_${location.longitude}_${date.format(formatter)}"
    }

    override fun save(data: WeatherForecastRegister) {
        val key = generateKey(data.location, data.date)
        cache[key] = data
    }

    override fun get(location: Location, date: LocalDate): WeatherForecastRegister? {
        val key = generateKey(location, date)
        return cache[key]
    }
}