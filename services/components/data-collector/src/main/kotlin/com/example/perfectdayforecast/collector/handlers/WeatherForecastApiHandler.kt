package com.example.perfectdayforecast.collector.handlers

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.Units
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.google.gson.Gson
import com.github.kittinunf.fuel.core.ResponseDeserializable
import com.google.gson.annotations.SerializedName

//val baseUrl = "https://api.open-meteo.com/v1/forecast?latitude={latitude}&longitude={longitude}&daily=weathercode,temperature_2m_max,temperature_2m_min,rain_sum,snowfall_sum,precipitation_sum,precipitation_probability_max&timezone=auto&start_date={start_date}&end_date={end_date}"

class WeatherForecastApiHandler(private val baseUrl: String, private var next: WeatherForecastHandler? = null) :
    WeatherForecastHandler {
    override fun next(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister>? {
        return next?.getData(location, startDate, endDate)
    }

    override fun setNext(handler: WeatherForecastHandler) {
        next = handler
    }
    override fun getData(location: Location, startDate: LocalDate, endDate: LocalDate): List<WeatherForecastRegister>? {
        val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH)

        // Format dates as strings
        val startDateStr = dateFormat.format(startDate)
        val endDateStr = dateFormat.format(endDate)

        // Replace placeholders with params
        val apiUrl = baseUrl
            .replace("{latitude}", location.latitude.toString())
            .replace("{longitude}", location.longitude.toString())
            .replace("{start_date}", startDateStr)
            .replace("{end_date}", endDateStr)

        val (_, _, result) = apiUrl
            .httpGet()
            .responseObject(WeatherApiResultDeserializer())


        return when (result) {
            is Result.Success -> {
                parseResult(result.get(), location)
            }
           else -> {
                print("Erro ao buscar dados da API")
                next(location, startDate, endDate)
            }
        }
    }

    fun parseResult(result: WeatherApiResult, location: Location): List<WeatherForecastRegister> {
        val weatherForecastList: MutableList<WeatherForecastRegister> = mutableListOf()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        for (i in 0 until result.daily.weathercode.size) {
            weatherForecastList.add(
                WeatherForecastRegister(
                    result.dailyUnits,
                    location,
                    LocalDate.parse(result.daily.time[i], formatter),
                    result.daily.weathercode[i],
                    result.daily.temperatureMax[i],
                    result.daily.temperatureMin[i],
                    result.daily.rainSum[i],
                    result.daily.snowfallSum[i],
                    result.daily.precipitationSum[i],
                    result.daily.precipitationProbabilityMax[i]
                )
            )
        }
        return weatherForecastList
    }

    data class WeatherApiResult(
        @SerializedName("daily_units") val dailyUnits : Units,
        val daily : DailyData
    )

    data class DailyData(
        val time: List<String>,
        val weathercode: List<Int>,
        @SerializedName("temperature_2m_max") val temperatureMax: List<Double>,
        @SerializedName("temperature_2m_min") val temperatureMin: List<Double>,
        @SerializedName("rain_sum") val rainSum: List<Double>,
        @SerializedName("snowfall_sum") val snowfallSum: List<Double>,
        @SerializedName("precipitation_sum") val precipitationSum: List<Double>,
        @SerializedName("precipitation_probability_max") val precipitationProbabilityMax: List<Double>
    )

    class WeatherApiResultDeserializer : ResponseDeserializable<WeatherApiResult> {
        override fun deserialize(content: String): WeatherApiResult {
            return Gson().fromJson(content, WeatherApiResult::class.java)
        }
    }
}