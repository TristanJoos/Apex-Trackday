package com.example.st_client_mobile_tristanjooshowest.domain.repository

import android.location.Location
import kotlinx.coroutines.flow.Flow

interface LocationRepository {
    fun getGpsLocationFlow(): Flow<Location>
}