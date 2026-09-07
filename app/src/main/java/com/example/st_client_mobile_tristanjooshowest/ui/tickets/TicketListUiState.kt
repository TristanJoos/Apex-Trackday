package com.example.st_client_mobile_tristanjooshowest.ui.tickets

import com.example.st_client_mobile_tristanjooshowest.domain.model.Ticket

data class TicketListUiState(
    val isLoading: Boolean = true,
    val upcomingTickets: List<Ticket> = emptyList(),
    val pastResults: List<Ticket> = emptyList(),
    val errorMessage: String? = null,
    val expandedTicketId: String? = null,
    val reminderSetMessage: String? = null
)