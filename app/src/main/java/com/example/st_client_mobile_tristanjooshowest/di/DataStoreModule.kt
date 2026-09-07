package com.example.st_client_mobile_tristanjooshowest.di

import android.content.Context
import com.example.st_client_mobile_tristanjooshowest.data.local.datastore.SettingsDataStore
import com.example.st_client_mobile_tristanjooshowest.data.local.datastore.dataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    @Provides
    @Singleton
    fun provideSettingsDataStore(@ApplicationContext context: Context): SettingsDataStore {
        return SettingsDataStore(context.dataStore)
    }
}