package com.example.perfectdayforecast.collector.handlers

open class BaseHandler(private var next: WeatherForecastHandler? = null): WeatherForecastHandler {

    override fun getData(context: WeatherRequestContext) {}
    override fun next(context: WeatherRequestContext) {
        next?.getData(context)
    }

    override fun setNext(handler: WeatherForecastHandler) {
        next = handler
    }
}