package com.example.st_client_mobile_tristanjooshowest.di

import com.example.st_client_mobile_tristanjooshowest.data.repository.HardwareSensorRepositoryImpl
import com.example.st_client_mobile_tristanjooshowest.data.repository.LocationRepositoryImpl
import com.example.st_client_mobile_tristanjooshowest.data.repository.TicketRepositoryImpl
import com.example.st_client_mobile_tristanjooshowest.data.repository.WeatherRepositoryImpl
import com.example.st_client_mobile_tristanjooshowest.domain.repository.HardwareSensorRepository
import com.example.st_client_mobile_tristanjooshowest.domain.repository.LocationRepository
import com.example.st_client_mobile_tristanjooshowest.domain.repository.TicketRepository
import com.example.st_client_mobile_tristanjooshowest.domain.repository.WeatherRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTicketRepository(
        ticketRepositoryImpl: TicketRepositoryImpl
    ): TicketRepository

    @Binds
    @Singleton
    abstract fun bindHardwareSensorRepository(
        hardwareSensorRepositoryImpl: HardwareSensorRepositoryImpl
    ): HardwareSensorRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    @Binds
    @Singleton
    abstract fun bindWeatherRepository(
        weatherRepositoryImpl: WeatherRepositoryImpl
    ): WeatherRepository
}