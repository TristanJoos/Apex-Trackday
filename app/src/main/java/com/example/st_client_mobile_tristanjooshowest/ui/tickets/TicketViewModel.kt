package com.example.st_client_mobile_tristanjooshowest.ui.tickets

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.st_client_mobile_tristanjooshowest.reciever.ReminderBroadcastReceiver
import com.example.st_client_mobile_tristanjooshowest.domain.model.Ticket
import com.example.st_client_mobile_tristanjooshowest.domain.repository.TicketRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val ticketRepository: TicketRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TicketListUiState(isLoading = true))
    val uiState: StateFlow<TicketListUiState> = _uiState.asStateFlow()

    init {
        observeTickets()
    }

    private fun observeTickets() {
        viewModelScope.launch {
            combine(
                ticketRepository.upcomingTickets,
                ticketRepository.pastResults
            ) { upcoming, past ->
                Pair(upcoming, past)
            }.collect { (upcoming, past) ->
                _uiState.update { currentState ->
                    currentState.copy(
                        isLoading = false,
                        upcomingTickets = upcoming,
                        pastResults = past
                    )
                }
            }
        }
    }

    fun getTicketById(id: String): Flow<Ticket?> {
        return ticketRepository.getTicketById(id)
    }

    fun toggleReminderMenu(ticketId: String?) {
        _uiState.update { it.copy(expandedTicketId = ticketId) }
    }

    fun setRaceReminder(ticketName: String, eventTimeMillis: Long, minutesBefore: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val reminderTimeMillis = eventTimeMillis - (minutesBefore * 60 * 1000)

        if (reminderTimeMillis <= System.currentTimeMillis()) {
            _uiState.update {
                it.copy(
                    reminderSetMessage = "Gekozen tijd ligt al in het verleden!",
                    expandedTicketId = null
                )
            }
            return
        }

        val intent = Intent(context, ReminderBroadcastReceiver::class.java).apply {
            putExtra("TRACK_NAME", ticketName)
            putExtra("MINUTES_BEFORE", minutesBefore)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ticketName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTimeMillis, pendingIntent)
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTimeMillis, pendingIntent)
            }
        } catch (e: SecurityException) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, reminderTimeMillis, pendingIntent)
            android.util.Log.w("TicketViewModel", "Exact alarm geweigerd door Android, fallback naar inexact ingezet.")
        }

        val hours = minutesBefore / 60
        val mins = minutesBefore % 60
        val timeString = if (hours > 0) "$hours uur en $mins min" else "$mins minuten"

        _uiState.update {
            it.copy(
                reminderSetMessage = "Herinnering ingesteld voor $timeString voor de start!",
                expandedTicketId = null
            )
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(reminderSetMessage = null) }
    }
}