package com.example.perfectdayforecast.analyzer

interface DataGateway {
    fun updateRequest(requestId: Int, status: RequestStatus, bestDay: WeatherForecast?)
}