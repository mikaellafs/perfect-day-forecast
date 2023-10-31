package com.example.perfectdayforecast.analyzer

class WeatherAnalyzer(private val dataGateway: DataGateway) {
    fun analyzeRequest(request: PerfectDayRequest) {
        // Get best day
        var bestDay: WeatherForecast? = null
        for (day in request.days) {
            if (day.weathercode == request.weatherPreference) {
                bestDay = day
            }
        }

        // Update request
        dataGateway.updateRequest(request.requestId, RequestStatus.DONE, bestDay)
    }
}
