package com.example.st_client_mobile_tristanjooshowest.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Ticket(
    val id: String,
    val eventName: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val date: LocalDate,
    val startTime: LocalTime,
    val heat: Int,
    val status: String,
    val expectedGForces: String,
    val qrCodeData: String
)