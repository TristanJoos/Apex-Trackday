package com.example.st_client_mobile_tristanjooshowest.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.st_client_mobile_tristanjooshowest.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SweetDroidAppBar(
    modifier: Modifier = Modifier.Companion,
    @StringRes appName: Int,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    shareWithFriends: () -> Unit
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(stringResource(appName))
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        ),
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        actions = {
            IconButton(
                onClick = shareWithFriends
            ) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    tint = Color.White,
                    contentDescription = "Share"
                )
            }
        }
    )
}