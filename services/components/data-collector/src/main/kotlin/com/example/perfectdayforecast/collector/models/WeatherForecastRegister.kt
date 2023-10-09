package com.example.perfectdayforecast.collector.models

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class Units(
    val time: String,
    val weathercode: String,
    @SerializedName("temperature_2m_max") val temperatureMax: String,
    @SerializedName("temperature_2m_min") val temperatureMin: String,
    @SerializedName("rain_sum") val rainSum: String,
    @SerializedName("snowfall_sum") val snowfallSum: String,
    @SerializedName("precipitation_sum") val precipitationSum: String,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: String
)
data class WeatherForecastRegister(
    val units: Units,

    val location: Location,
    val date: LocalDate,

    val weatherCode: Int,
    val maxTemperature: Double,
    val minTemperature: Double,
    val rainSum: Double,
    val snowfallSum: Double,
    val precipitationSum: Double,
    val precipitationProbabilityMax: Double
)