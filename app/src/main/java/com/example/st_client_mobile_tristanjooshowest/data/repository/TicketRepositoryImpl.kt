package com.example.st_client_mobile_tristanjooshowest.data.repository

import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.st_client_mobile_tristanjooshowest.data.local.room.dao.TicketDao
import com.example.st_client_mobile_tristanjooshowest.data.local.room.entity.TicketEntity
import com.example.st_client_mobile_tristanjooshowest.data.local.room.entity.toDomain
import com.example.st_client_mobile_tristanjooshowest.data.messaging.ConfirmationConsumer
import com.example.st_client_mobile_tristanjooshowest.data.worker.NotificationWorker
import com.example.st_client_mobile_tristanjooshowest.domain.model.Ticket
import com.example.st_client_mobile_tristanjooshowest.domain.repository.TicketRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TicketRepositoryImpl @Inject constructor(
    private val ticketDao: TicketDao,
    private val consumer: ConfirmationConsumer,
    private val workManager: WorkManager,
    private val moshi: Moshi
) : TicketRepository {
    override val upcomingTickets: Flow<List<Ticket>> = ticketDao.getUpcomingTickets().map { entities ->
        entities.map { it.toDomain() }
    }

    override val pastResults: Flow<List<Ticket>> = ticketDao.getPastResults().map { entities ->
        entities.map { it.toDomain() }
    }

    override fun getTicketById(id: String): Flow<Ticket?> {
        return upcomingTickets.map { list -> list.find { it.id == id } }
    }

    init {
        consumer.onMessageReceived = { json ->
            Log.d("ApexBroker", "Bericht ontvangen: $json")
            try {
                val adapter = moshi.adapter(TicketEntity::class.java)
                val ticketEntity = adapter.fromJson(json)

                ticketEntity?.let {
                    CoroutineScope(Dispatchers.IO).launch {
                        ticketDao.insertTicket(it)
                        scheduleNotification(it.eventName)
                    }
                }
            } catch (e: Exception) {
                Log.e("ApexBroker", "Error parsing JSON", e)
            }
        }
        consumer.startConsuming()
    }

    private fun scheduleNotification(eventName: String) {
        val data = workDataOf("EVENT_NAME" to eventName)
        val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data)
            .build()
        workManager.enqueue(workRequest)
    }
}