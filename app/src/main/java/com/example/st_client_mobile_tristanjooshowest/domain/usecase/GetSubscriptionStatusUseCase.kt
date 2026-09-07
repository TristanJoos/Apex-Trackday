package com.example.st_client_mobile_tristanjooshowest.domain.usecase

import com.example.st_client_mobile_tristanjooshowest.data.local.datastore.SettingsDataStore

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSubscriptionStatusUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    operator fun invoke(): Flow<Boolean> {
        return settings.isSubscribed
    }
}