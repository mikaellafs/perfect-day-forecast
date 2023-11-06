package com.example.perfectdayforecast.analyzer

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.AfterAll
import org.jetbrains.exposed.sql.Database
import org.junit.Ignore
import java.time.LocalDate

class SQLDataGatewayTest {
//    companion object {
//        private val container = PostgreSQLContainer<Nothing>("postgres:15-bullseye")
//        private lateinit var gateway: SQLDataGateway
//
//        @BeforeAll
//        @JvmStatic
//        fun startContainer() {
//            container.start()
//            System.setProperty("DB_URL", container.jdbcUrl)
//            System.setProperty("DB_USER", container.username)
//            System.setProperty("DB_PASSWORD", container.password)
//
//            val db = Database.connect(
//                url = container.jdbcUrl,
//                driver = "org.postgresql.Driver",
//                user = container.username,
//                password = container.password
//            )
//
//            gateway = SQLDataGateway(db)
//        }
//
//        @AfterAll
//        @JvmStatic
//        fun stopContainer() {
//            container.stop()
//        }
//    }
//
//
//    @Ignore
//    @Test
//    fun testUpdateRequest() {
//        // Save request to system
//
//        // Call gateway update
////        gateway.updateRequest(
////            1,
////            RequestStatus.DONE,
////            mockk<WeatherForecast>() {
////                every {date} returns LocalDate.of(2023, 10, 26)
////            }
////        )
//
//        // Query data and check fields have been updated
//
//    }
}