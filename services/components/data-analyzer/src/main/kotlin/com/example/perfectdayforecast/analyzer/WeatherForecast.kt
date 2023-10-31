package com.example.perfectdayforecast.analyzer

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class WeatherForecast(
    val date: LocalDate,
    val weathercode: WeatherCondition,
    @SerializedName("max_temperature") val maxTemperature: Double,
    @SerializedName("min_temperature") val minTemperature: Double,
    @SerializedName("rain_sum") val rainSum: Double,
    @SerializedName("snowfall_sum") val snowfallSum: Double,
    @SerializedName("precipitation_sum") val precipitationSum: Double,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: Double
) {
}