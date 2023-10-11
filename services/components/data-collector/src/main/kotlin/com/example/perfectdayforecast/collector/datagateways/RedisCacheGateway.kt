package com.example.perfectdayforecast.collector.datagateways

import com.example.perfectdayforecast.collector.models.Location
import com.example.perfectdayforecast.collector.models.WeatherForecastRegister
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import io.lettuce.core.api.sync.RedisCommands
import java.time.Duration

class RedisCacheGateway(private val commands: RedisCommands<String, String>): WeatherDataGateway {
    private fun generateKey(location: Location, date: LocalDate): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        return "${location.latitude}_${location.longitude}_${date.format(formatter)}"
    }

    override fun save(data: WeatherForecastRegister) {
        val key = generateKey(data.location, data.date)
        commands.setex(key, Duration.ofDays(1).toSeconds(), data.toJson())
    }

    override fun get(location: Location, date: LocalDate): WeatherForecastRegister? {
        val key = generateKey(location, date)
        return commands.get(key).let { WeatherForecastRegister.fromJson(it) }
    }
}