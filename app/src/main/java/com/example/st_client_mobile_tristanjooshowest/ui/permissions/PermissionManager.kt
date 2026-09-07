package com.example.st_client_mobile_tristanjooshowest.ui.permissions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState

@Composable
fun PermissionManager(
    permissionRequest: AppPermission?,
    onPermissionResult: (AppPermission, Boolean) -> Unit
) {
    val currentRequest by rememberUpdatedState(permissionRequest)

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            currentRequest?.let {
                onPermissionResult(it, granted)
            }
        }

    LaunchedEffect(permissionRequest) {
        permissionRequest ?: return@LaunchedEffect
        val permission = permissionRequest.toAndroidPermission()
        permissionLauncher.launch(permission)
    }
}