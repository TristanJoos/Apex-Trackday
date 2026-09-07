package com.example.st_client_mobile_tristanjooshowest.data.local.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.st_client_mobile_tristanjooshowest.data.local.room.dao.TicketDao
import com.example.st_client_mobile_tristanjooshowest.data.local.room.dao.ProductDao
import com.example.st_client_mobile_tristanjooshowest.data.local.room.entity.TicketEntity
import com.example.st_client_mobile_tristanjooshowest.data.local.room.entity.ProductEntity

@Database(
    entities = [TicketEntity::class, ProductEntity::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ticketDao(): TicketDao
    abstract fun productDao(): ProductDao
}
