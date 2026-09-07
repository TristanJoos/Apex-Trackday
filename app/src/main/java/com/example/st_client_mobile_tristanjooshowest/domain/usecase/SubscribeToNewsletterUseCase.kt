package com.example.st_client_mobile_tristanjooshowest.domain.usecase


import com.example.st_client_mobile_tristanjooshowest.data.local.datastore.SettingsDataStore
import javax.inject.Inject


class SubscribeToNewsletterUseCase @Inject constructor(
    private val settings: SettingsDataStore
) {
    suspend operator fun invoke() {
        settings.setSubscribed()
    }
}

