package com.example.perfectdayforecast.analyzer

import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class PerfectDayRequest(
    val requestId: Int,
    val days: List<WeatherForecast>,
    @SerializedName("weather_preference") val weatherPreference: WeatherCondition
) {
    companion object {
        private val gson = GsonBuilder()
            .registerTypeAdapter(WeatherCondition::class.java,WeatherCondition.WeatherConditionDeserializer())
            .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
            .create()

        fun fromJson(json: String) : PerfectDayRequest {
            return gson.fromJson(json, PerfectDayRequest::class.java)
        }
    }

    class LocalDateDeserializer : JsonDeserializer<LocalDate> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDate {
            val dateString = json?.asString
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            return LocalDate.parse(dateString, formatter)
        }
    }
}