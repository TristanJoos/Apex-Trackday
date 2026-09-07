package com.example.st_client_mobile_tristanjooshowest.domain.repository

import com.example.st_client_mobile_tristanjooshowest.domain.model.Ticket
import kotlinx.coroutines.flow.Flow

interface TicketRepository {
    val upcomingTickets: Flow<List<Ticket>>
    val pastResults: Flow<List<Ticket>>
    fun getTicketById(id: String): Flow<Ticket?>
}