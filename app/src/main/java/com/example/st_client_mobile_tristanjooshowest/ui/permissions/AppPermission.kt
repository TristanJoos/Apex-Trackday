package com.example.st_client_mobile_tristanjooshowest.ui.permissions

import android.Manifest

enum class AppPermission {
    NOTIFICATIONS,
    CAMERA,
    LOCATION
}

fun AppPermission.toAndroidPermission(): String {
    return when (this) {
        AppPermission.NOTIFICATIONS -> Manifest.permission.POST_NOTIFICATIONS
        AppPermission.CAMERA -> Manifest.permission.CAMERA
        AppPermission.LOCATION -> Manifest.permission.ACCESS_FINE_LOCATION
    }
}