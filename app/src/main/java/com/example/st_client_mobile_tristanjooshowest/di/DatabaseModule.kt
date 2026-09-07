package com.example.st_client_mobile_tristanjooshowest.di

import android.content.Context
import androidx.room.Room
import com.example.st_client_mobile_tristanjooshowest.data.local.room.AppDatabase
import com.example.st_client_mobile_tristanjooshowest.data.local.room.dao.TicketDao
import com.example.st_client_mobile_tristanjooshowest.data.local.room.dao.ProductDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): AppDatabase =
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "apex_trackday.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideTicketDao(db: AppDatabase): TicketDao =
        db.ticketDao()

    @Provides
    fun provideProductDao(db: AppDatabase): ProductDao =
        db.productDao()
}