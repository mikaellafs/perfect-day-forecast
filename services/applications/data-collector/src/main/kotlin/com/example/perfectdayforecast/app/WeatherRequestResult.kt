package com.example.perfectdayforecast.app

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.Units
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import java.time.LocalDate

class WeatherRequestResult(
    val date: LocalDate,

    val weathercode: Int,
    @SerializedName("max_temperature") val maxTemperature: Double,
    @SerializedName("min_temperature") val minTemperature: Double,
    @SerializedName("rain_sum") val rainSum: Double,
    @SerializedName("snowfall_sum") val snowfallSum: Double,
    @SerializedName("precipitation_sum") val precipitationSum: Double,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: Double
)
class WeatherRequestResultMessage(
    @SerializedName("request_id") val requestId: Int,
    @SerializedName("weather_preference") val weatherPreference: String,
    val units: Units,
    val location: Location,
    val days: List<WeatherRequestResult>
) {

    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, WeatherForecastRegister.LocalDateSerializer())
            .registerTypeAdapter(Location::class.java, Location.LocationSerializer())
            .create()
    }
    fun toJson(): String {
        return gson.toJson(this, WeatherRequestResultMessage::class.java)
    }
}