package com.example.perfectdayforecast.app

import com.example.perfectdayforecast.collector.models.Location
import java.lang.reflect.Type
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class MessageWeatherRequest(
    @SerializedName("request_id") val requestId: Int,
    val location: Location,
    @SerializedName("start_date") val startDate: LocalDate,
    @SerializedName("end_date") val endDate: LocalDate,
    @SerializedName("weather_preference") val weatherPreference: String
) {
    companion object {
        fun fromJson(json: String): MessageWeatherRequest {
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                .registerTypeAdapter(Location::class.java, Location.LocationDeserializer())
                .create()

            return gson.fromJson(json, MessageWeatherRequest::class.java)
        }
    }

    class LocalDateDeserializer : JsonDeserializer<LocalDate> {
        override fun deserialize(
            json: JsonElement?,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): LocalDate {
            val dateString = json?.asString
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return LocalDate.parse(dateString, formatter)
        }
    }
}
