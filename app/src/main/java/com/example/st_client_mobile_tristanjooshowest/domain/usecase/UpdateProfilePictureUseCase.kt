package com.example.st_client_mobile_tristanjooshowest.domain.usecase

import com.example.st_client_mobile_tristanjooshowest.data.local.datastore.SettingsDataStore
import javax.inject.Inject

class UpdateProfilePictureUseCase @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    suspend operator fun invoke(uri: String) {
        settingsDataStore.setProfilePictureUri(uri)
    }
}
