package com.example.st_client_mobile_tristanjooshowest.data.local.room.dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.st_client_mobile_tristanjooshowest.data.local.room.AppDatabase
import com.example.st_client_mobile_tristanjooshowest.data.local.room.entity.TicketEntity
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TicketDaoTest {

    private lateinit var database: AppDatabase
    private lateinit var ticketDao: TicketDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()
        ticketDao = database.ticketDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertTicket_canBeRetrievedInUpcoming() = runTest {
        val ticket = TicketEntity(
            id = "1",
            eventName = "Track Day Laguna Seca",
            location = "California",
            latitude = 36.5841,
            longitude = -121.7529,
            date = "2026-06-15",
            startTime = "09:00",
            heat = 1,
            status = "Aankomende Heats",
            expectedGForces = "2.5G",
            qrCodeData = "some-qr-data"
        )

        ticketDao.insertTicket(ticket)
        val upcoming = ticketDao.getUpcomingTickets().first()

        assertEquals(1, upcoming.size)
        assertEquals("Track Day Laguna Seca", upcoming.first().eventName)
    }

    @Test
    fun getPastResults_onlyReturnsResultatenStatus() = runTest {
        val upcomingTicket = TicketEntity(
            id = "1",
            eventName = "Future Event",
            location = "Track A",
            latitude = 0.0,
            longitude = 0.0,
            date = "2026-07-01",
            startTime = "10:00",
            heat = 1,
            status = "Aankomende Heats",
            expectedGForces = "2.0G",
            qrCodeData = "qr1"
        )
        val pastTicket = TicketEntity(
            id = "2",
            eventName = "Past Event",
            location = "Track B",
            latitude = 0.0,
            longitude = 0.0,
            date = "2026-05-01",
            startTime = "08:00",
            heat = 2,
            status = "Resultaten",
            expectedGForces = "1.8G",
            qrCodeData = "qr2"
        )

        ticketDao.insertTicket(upcomingTicket)
        ticketDao.insertTicket(pastTicket)

        val results = ticketDao.getPastResults().first()

        assertEquals(1, results.size)
        assertEquals("Past Event", results.first().eventName)
    }

    @Test
    fun insertTicket_replacesOnConflict() = runTest {
        val ticket1 = TicketEntity(
            id = "1",
            eventName = "Original Event",
            location = "Track A",
            latitude = 0.0,
            longitude = 0.0,
            date = "2026-06-01",
            startTime = "09:00",
            heat = 1,
            status = "Aankomende Heats",
            expectedGForces = "1.0G",
            qrCodeData = "qr1"
        )
        val ticket2 = TicketEntity(
            id = "1", // Same ID
            eventName = "Updated Event",
            location = "Track A",
            latitude = 0.0,
            longitude = 0.0,
            date = "2026-06-01",
            startTime = "09:00",
            heat = 1,
            status = "Aankomende Heats",
            expectedGForces = "1.0G",
            qrCodeData = "qr1"
        )

        ticketDao.insertTicket(ticket1)
        ticketDao.insertTicket(ticket2)

        val upcoming = ticketDao.getUpcomingTickets().first()

        assertEquals(1, upcoming.size)
        assertEquals("Updated Event", upcoming.first().eventName)
    }
}
