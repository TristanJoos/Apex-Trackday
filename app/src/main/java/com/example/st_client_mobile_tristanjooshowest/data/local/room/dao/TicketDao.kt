package com.example.st_client_mobile_tristanjooshowest.data.local.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.st_client_mobile_tristanjooshowest.data.local.room.entity.TicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketDao {
    @Query("SELECT * FROM tickets WHERE status = 'Aankomende Heats' ORDER BY date ASC, startTime ASC")
    fun getUpcomingTickets(): Flow<List<TicketEntity>>

    @Query("SELECT * FROM tickets WHERE status = 'Resultaten' ORDER BY date DESC, startTime DESC")
    fun getPastResults(): Flow<List<TicketEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTicket(ticket: TicketEntity)
}