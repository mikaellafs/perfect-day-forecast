package com.example.perfecdayforecast.collector

data class Units(
    val time: String,
    val weathercode: String,
    val temperature_2m_max: String,
    val temperature_2m_min: String,
    val rain_sum: String,
    val snowfall_sum: String,
    val precipitation_probability_max: String
)
data class WeatherForecastRegister(
    val units: Units,

    val location: Location,
    val date: String,

    val weatherCode: Int,
    val maxTemperature: Double,
    val minTemperature: Double,
    val rainSum: Double,
    val snowfallSum: Double,
    val precipitationSum: Double,
    val precipitationProbabilityMax: Double
)
//{
//    override fun toString(): String {
//        return "Weather Code: $weatherCode\n" +
//                "Max Temperature (°C): $maxTemperatureCelsius\n" +
//                "Min Temperature (°C): $minTemperatureCelsius\n" +
//                "Rainfall Sum (mm): $rainSumMillimeters\n" +
//                "Snowfall Sum (cm): $snowfallSumCentimeters\n" +
//                "Precipitation Hours (h): $precipitationHours\n" +
//                "Precipitation Probability Max (%): $precipitationProbabilityMax"
//    }
//}