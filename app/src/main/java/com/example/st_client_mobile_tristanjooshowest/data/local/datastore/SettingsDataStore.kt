package com.example.st_client_mobile_tristanjooshowest.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class SettingsDataStore(private val dataStore: DataStore<Preferences>) {

    companion object {
        val IS_SUBSCRIBED = booleanPreferencesKey("is_subscribed")
        val PROFILE_PICTURE_URI = stringPreferencesKey("profile_picture_uri")
    }

    val isSubscribed: Flow<Boolean> =
        dataStore.data.map { prefs ->
            prefs[IS_SUBSCRIBED] ?: false
        }

    val profilePictureUri: Flow<String?> =
        dataStore.data.map { prefs ->
            prefs[PROFILE_PICTURE_URI]
        }

    suspend fun setSubscribed() {
        dataStore.edit { prefs ->
            prefs[IS_SUBSCRIBED] = true
        }
    }

    suspend fun setProfilePictureUri(uri: String) {
        dataStore.edit { prefs ->
            prefs[PROFILE_PICTURE_URI] = uri
        }
    }
}