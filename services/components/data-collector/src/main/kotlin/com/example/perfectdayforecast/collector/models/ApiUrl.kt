package com.example.perfectdayforecast.collector.models

import com.github.kittinunf.fuel.core.Request
import com.github.kittinunf.fuel.httpGet

class ApiUrl(private var url: String) {

    fun replace(old: String, new: String) : ApiUrl {
        return ApiUrl(url.replace(old, new))
    }
    fun httpGet(): Request {
        return url.httpGet()
    }
}