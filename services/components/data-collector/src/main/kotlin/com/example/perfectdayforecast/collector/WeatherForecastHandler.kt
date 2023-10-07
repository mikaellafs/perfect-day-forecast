package com.example.perfecdayforecast.collector

import java.time.LocalDate
interface WeatherForecastHandler {
    fun getData(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister>?
    fun next(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister>?
    fun setNext(handler: WeatherForecastHandler)
}
