package com.example.st_client_mobile_tristanjooshowest.ui.permissions

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor() : ViewModel() {

    private val _permissionRequest =
        MutableStateFlow<AppPermission?>(null)

    val permissionRequest: StateFlow<AppPermission?> =
        _permissionRequest

    fun requestPermission(permission: AppPermission) {
        _permissionRequest.value = permission
        Log.d("Permission", "$permission asked")
    }

    fun onPermissionResult(permission: AppPermission, granted: Boolean) {
        Log.d("Permission", "$permission granted: $granted")
        _permissionRequest.value = null
    }
}