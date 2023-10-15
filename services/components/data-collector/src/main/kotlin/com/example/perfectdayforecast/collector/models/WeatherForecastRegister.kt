package com.example.perfectdayforecast.collector.models

import com.google.gson.*
import com.google.gson.annotations.SerializedName
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

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

    val weathercode: Int,
    @SerializedName("max_temperature") val maxTemperature: Double,
    @SerializedName("min_temperature") val minTemperature: Double,
    @SerializedName("rain_sum") val rainSum: Double,
    @SerializedName("snowfall_sum") val snowfallSum: Double,
    @SerializedName("precipitation_sum") val precipitationSum: Double,
    @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: Double
) {
    fun toJson(): String {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateSerializer())
            .create()
        return gson.toJson(this, WeatherForecastRegister::class.java)
    }

    companion object {
        fun fromJson(json: String): WeatherForecastRegister {
            val gson = GsonBuilder()
                .registerTypeAdapter(LocalDate::class.java, LocalDateDeserializer())
                .create()

            return gson.fromJson(json, WeatherForecastRegister::class.java)
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

    class LocalDateSerializer : JsonSerializer<LocalDate> {
        override fun serialize(
            date: LocalDate,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            val formatter = DateTimeFormatter.ISO_LOCAL_DATE
            val dateString = date.format(formatter)
            return JsonPrimitive(dateString)
        }
    }
}