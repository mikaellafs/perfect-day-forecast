package com.example.perfectdayforecast.collector.models

import com.google.gson.*
import java.lang.reflect.Type
import java.time.LocalDate
import java.time.format.DateTimeFormatter

enum class Location(val city: String, val latitude: Double, val longitude: Double) {
    NEW_YORK("New York", 40.7128, -74.0060),
    LOS_ANGELES("Los Angeles", 34.0522, -118.2437),
    LONDON("London", 51.5074, -0.1278),
    PARIS("Paris", 48.8566, 2.3522),
    TOKYO("Tokyo", 35.682839, 139.759455),
    SYDNEY("Sydney", -33.8675, 151.2094),
    RIO_DE_JANEIRO("Rio de Janeiro", -22.9068, -43.1729);

    override fun toString(): String {
        return city
    }

    class LocationDeserializer : JsonDeserializer<Location> {
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type?,
            context: JsonDeserializationContext?
        ): Location {
            return when (json.asString) {
                "New York" -> Location.NEW_YORK
                "Los Angeles" -> Location.LOS_ANGELES
                "London" -> Location.LONDON
                "Paris" -> Location.PARIS
                "Tokyo" -> Location.TOKYO
                "Sydney" -> Location.SYDNEY
                "Rio de Janeiro" -> Location.RIO_DE_JANEIRO
                else -> throw IllegalArgumentException("Unknown location: ${json.asString}")
            }
        }
    }

    class LocationSerializer : JsonSerializer<Location> {
        override fun serialize(
            location: Location,
            typeOfSrc: Type?,
            context: JsonSerializationContext?
        ): JsonElement {
            return JsonPrimitive(location.name)
        }
    }
}