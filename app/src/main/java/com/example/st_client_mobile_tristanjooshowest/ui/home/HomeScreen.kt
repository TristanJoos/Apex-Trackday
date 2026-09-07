package com.example.st_client_mobile_tristanjooshowest.ui.home


import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.st_client_mobile_tristanjooshowest.R
import java.io.File
import java.io.FileOutputStream


@Composable
fun HomeScreen(
    uiState: HomeUiState,
    upcomingCount: Int,
    onEmailChanged: (String) -> Unit,
    onSubscribeClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onShopClick: () -> Unit,
    onProfilePictureChanged: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .verticalScroll(rememberScrollState())
            .testTag("home_screen")
    ) {
        TopPart(
            userName = uiState.username,
            profilePictureUri = uiState.profilePictureUri,
            onProfilePictureChanged = onProfilePictureChanged
        )

        QuickAccessSection(
            upcomingCount = upcomingCount,
            onCalendarClick = onCalendarClick,
            onShopClick = onShopClick
        )

        DriverStatsGrid()

        if(!uiState.isSubscribed) {
            SubscribeToNewsletter(
                emailAddress = uiState.emailAddress,
                onEmailChanged = onEmailChanged,
                onSubscribeClick = onSubscribeClick,
                modifier = Modifier.testTag("subscribe")
            )
        }
    }
}

@Composable
fun TopPart(
    userName: String,
    profilePictureUri: String?,
    onProfilePictureChanged: (String) -> Unit
){
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }

    // Gallery Picker
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let { onProfilePictureChanged(it.toString()) }
    }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val file = File(context.cacheDir, "profile_pic_${System.currentTimeMillis()}.jpg")
            val out = FileOutputStream(file)
            it.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
            onProfilePictureChanged(file.toURI().toString())
        }
    }

    val startColor = colorResource(R.color.racing_red)
    val endColor = colorResource(R.color.paddock_gradiant_end)

    Box(
        modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(startColor, endColor),
                        start = Offset(0f, 0f),
                        end = Offset.Infinite
                    )
                )
                .padding(24.dp)
    )
    {
        Column(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalArrangement = Arrangement.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.the_padock),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Black,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${ stringResource(R.string.welcome_back)}, $userName",
                color = Color.White,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = stringResource(R.string.next_sesion),
                color = Color.White.copy(alpha = 0.7f),
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Profile Picture Circle
        Surface(
            modifier = Modifier
                .size(64.dp)
                .align(Alignment.TopEnd)
                .clickable { showDialog = true },
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.2f),
            border = BorderStroke(2.dp, Color.White.copy(alpha = 0.5f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (profilePictureUri != null) {
                    AsyncImage(
                        model = profilePictureUri,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Select Profile Picture") },
            text = { Text("Choose a source for your profile picture") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    cameraLauncher.launch()
                }) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    galleryLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                }) {
                    Text("Gallery")
                }
            }
        )
    }
}

@Composable
fun QuickAccessSection(
    upcomingCount: Int,
    onCalendarClick: () -> Unit,
    onShopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.quick_acces),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        QuickAccessCard(
            title = stringResource(R.string.titel_calander),
            subtitle = stringResource(R.string.subtitel_calander),
            statusText = if (upcomingCount > 0) "$upcomingCount Heats Gepland" else "Geen Heats Gepland",
            statusColor = colorResource(R.color.racing_red),
            icon = Icons.Default.DateRange,
            iconBgColor = colorResource(R.color.calander_icon_background),
            onClick = onCalendarClick
        )

        QuickAccessCard(
            title = stringResource(R.string.title_performance_shop),
            subtitle = stringResource(R.string.subtitel_performance_shop),
            statusText = stringResource(R.string.status_performance_shop),
            statusColor = colorResource(R.color.performance_shop_status),
            icon = Icons.Default.ShoppingCart,
            iconBgColor = colorResource(R.color.performance_shop_icon_background),
            onClick = onShopClick
        )
    }
}

@Composable
fun QuickAccessCard(
    title: String,
    subtitle: String,
    statusText: String,
    statusColor: Color,
    icon: ImageVector,
    iconBgColor: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = colorResource(R.color.card_color),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(iconBgColor, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = statusColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
                Text(statusText, color = statusColor, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun DriverStatsGrid(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = "Track Days",
                value = "12",
                valueColor = colorResource(R.color.racing_red),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Sessions",
                value = "47",
                valueColor = Color(0xFF3B82F6),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                label = "Best Lap",
                value = "2:14.8",
                valueColor = Color(0xFF22C55E),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Max G-Force",
                value = "1.4G",
                valueColor = Color(0xFFA855F7),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.height(110.dp),
        shape = RoundedCornerShape(16.dp),
        color = colorResource(R.color.card_color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                color = valueColor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun SubscribeToNewsletter(
    emailAddress: String,
    onEmailChanged: (String) -> Unit,
    onSubscribeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = colorResource(R.color.card_color)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(R.string.recieve_updates),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                stringResource(R.string.recieve_updates_subtext),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )

            androidx.compose.material3.OutlinedTextField(
                value = emailAddress,
                onValueChange = onEmailChanged,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(stringResource(R.string.enter_email), color = Color.DarkGray) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = colorResource(R.color.racing_red),
                    unfocusedBorderColor = Color.DarkGray,
                    cursorColor = colorResource(R.color.racing_red)
                )
            )

            androidx.compose.material3.Button(
                onClick = onSubscribeClick,
                modifier = Modifier.fillMaxWidth(),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.racing_red)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.subscribe), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    val mockUiState = HomeUiState(
        emailAddress = "",
        isSubscribed = false
    )
    MaterialTheme {
        HomeScreen(
            uiState = mockUiState,
            onEmailChanged = {},
            onSubscribeClick = {},
            onCalendarClick = {},
            onShopClick = {},
            onProfilePictureChanged = {},
            upcomingCount = 2
        )
    }
}
