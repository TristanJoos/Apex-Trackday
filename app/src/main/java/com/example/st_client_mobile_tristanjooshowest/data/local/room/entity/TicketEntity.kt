package com.example.st_client_mobile_tristanjooshowest.data.local.room.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.st_client_mobile_tristanjooshowest.domain.model.Ticket
import com.squareup.moshi.JsonClass
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "tickets")
@JsonClass(generateAdapter = true)
data class TicketEntity(
    @PrimaryKey val id: String,
    val eventName: String,
    val location: String,
    val latitude: Double,
    val longitude: Double,
    val date: String,
    val startTime: String,
    val heat: Int,
    val status: String,
    val expectedGForces: String,
    val qrCodeData: String
)

fun TicketEntity.toDomain(): Ticket = Ticket(
    id = id,
    eventName = eventName,
    location = location,
    latitude = latitude,
    longitude = longitude,
    date = LocalDate.parse(date),
    startTime = LocalTime.parse(startTime),
    heat = heat,
    status = status,
    expectedGForces = expectedGForces,
    qrCodeData = qrCodeData
)