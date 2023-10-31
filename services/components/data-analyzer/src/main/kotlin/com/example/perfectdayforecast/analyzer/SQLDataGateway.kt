package com.example.perfectdayforecast.analyzer

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import org.jetbrains.exposed.sql.javatime.datetime

class SQLDataGateway(private val db: Database): DataGateway {
    object RequestTable : IntIdTable("requests") {
        val status = varchar("status", length = 255)
        val best_day_result = datetime("best_day_result").nullable()
    }
    override fun updateRequest(requestId: Int, status: RequestStatus, bestDay: WeatherForecast?): Unit = transaction(db) {
        RequestTable.update({RequestTable.id eq requestId}) {
            it[RequestTable.status] = status.value
            it[best_day_result] = bestDay?.date?.atStartOfDay()
        }
    }
}