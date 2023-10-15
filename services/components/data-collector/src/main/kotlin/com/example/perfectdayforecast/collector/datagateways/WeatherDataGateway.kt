package com.example.perfectdayforecast.collector.datagateways

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate

interface WeatherDataGateway {
    fun save(data: WeatherForecastRegister)
    fun get(location: Location, date: LocalDate): WeatherForecastRegister?
}