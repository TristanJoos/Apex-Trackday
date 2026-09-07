package com.example.st_client_mobile_tristanjooshowest.domain.usecase

import com.example.st_client_mobile_tristanjooshowest.data.local.datastore.SettingsDataStore
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfilePictureUriUseCase @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) {
    operator fun invoke(): Flow<String?> {
        return settingsDataStore.profilePictureUri
    }
}
