package com.example.perfectdayforecast.analyzer

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

enum class WeatherCondition {
    CLEAR_SKY,
    CLOUDY,
    FOGGY,
    RAINY,
    SNOWY,
    UNKNOWN;

    companion object {
        fun fromWeatherCode(code: Int): WeatherCondition {
            return when (code) {
                0 -> WeatherCondition.CLEAR_SKY
                in 1..3 -> WeatherCondition.CLOUDY
                in 45..48 -> WeatherCondition.FOGGY
                in 51..55, in 56..57 -> WeatherCondition.RAINY // Combine rainy and freezing drizzle
                in 61..67 -> WeatherCondition.RAINY
                in 71..75, 77 -> WeatherCondition.SNOWY // Combine snowy and snow grains
                in 80..82 -> WeatherCondition.RAINY
                in 85..86 -> WeatherCondition.SNOWY
                95, 96 -> WeatherCondition.RAINY // Combine rainy and thunderstorm
                99 -> WeatherCondition.SNOWY // Consider as snowy due to thunderstorm with heavy hail
                else -> WeatherCondition.UNKNOWN
            }
        }
    }

    class WeatherConditionDeserializer : JsonDeserializer<WeatherCondition> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): WeatherCondition {
            return when (json.asString.lowercase()) {
                "clear sky" -> WeatherCondition.CLEAR_SKY
                "cloudy" -> WeatherCondition.CLOUDY
                "foggy" -> WeatherCondition.FOGGY
                "rainy" -> WeatherCondition.RAINY
                "snowy" -> WeatherCondition.SNOWY
                else -> WeatherCondition.fromWeatherCode(json.asInt)
            }
        }
    }
}