package com.example.st_client_mobile_tristanjooshowest.ui.tickets

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.st_client_mobile_tristanjooshowest.R
import com.example.st_client_mobile_tristanjooshowest.domain.model.Ticket
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@Composable
fun TicketListScreen(
    uiState: TicketListUiState,
    viewModel: TicketViewModel = viewModel(),
    modifier: Modifier = Modifier,
    onTicketClick: (String) -> Unit = {}
) {
    var showUpcoming by remember { mutableStateOf(true) }
    var activeQrData by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            android.util.Log.d("TicketScreen", "Notificaties toegestaan! Wekkers zullen werken.")
        }
    }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(uiState.reminderSetMessage) {
        uiState.reminderSetMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            viewModel.clearMessage()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .verticalScroll(rememberScrollState())
    ) {
        CalendarHeader()

        TabSwitcher(
            isUpcomingSelected = showUpcoming,
            upcomingCount = uiState.upcomingTickets.size,
            pastCount = uiState.pastResults.size,
            onTabSelected = { isUpcoming -> showUpcoming = isUpcoming }
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Red)
                }
            } else {
                val ticketsToShow = if (showUpcoming) uiState.upcomingTickets else uiState.pastResults

                if (ticketsToShow.isEmpty()) {
                    Text(
                        text = "No registrations found.",
                        color = Color.Gray,
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                } else {
                    ticketsToShow.forEach { ticket ->
                        if (showUpcoming) {
                            SessionCard(
                                ticket = ticket,
                                uiState = uiState,
                                viewModel = viewModel,
                                onShowQrClick = { activeQrData = ticket.qrCodeData },
                                onGetDirectionsClick = {
                                    val mapIntent = Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse("geo:${ticket.latitude},${ticket.longitude}?q=${Uri.encode(ticket.location)}")
                                    )
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (e: Exception) {
                                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/?q=${Uri.encode(ticket.location)}"))
                                        context.startActivity(browserIntent)
                                    }
                                },
                                onClick = { onTicketClick(ticket.id) }
                            )
                        } else {
                            ResultCard(ticket)
                        }
                    }
                }
            }
        }

        if (activeQrData != null) {
            var isQrLoading by remember { mutableStateOf(true) }
            var qrBitMatrix by remember { mutableStateOf<com.google.zxing.common.BitMatrix?>(null) }

            LaunchedEffect(activeQrData) {
                isQrLoading = true
                withContext(kotlinx.coroutines.Dispatchers.Default) {
                    try {
                        val hints = HashMap<com.google.zxing.EncodeHintType, Any>()
                        hints[com.google.zxing.EncodeHintType.MARGIN] = 0

                        qrBitMatrix = QRCodeWriter().encode(
                            activeQrData ?: "",
                            BarcodeFormat.QR_CODE,
                            512,
                            512,
                            hints
                        )
                    } catch (e: Exception) {
                        android.util.Log.e("ZXING", "Fout bij genereren QR", e)
                    } finally {
                        kotlinx.coroutines.delay(300)
                        isQrLoading = false
                    }
                }
            }

            AlertDialog(
                onDismissRequest = { activeQrData = null },
                title = {
                    Text(
                        text = "Scrutineering Check-in",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                text = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Present this QR code at the tech bay:",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )

                        Spacer(Modifier.height(24.dp))

                        Box(
                            modifier = Modifier
                                .size(200.dp)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            AnimatedContent(
                                targetState = isQrLoading,
                                transitionSpec = {
                                    fadeIn(animationSpec = androidx.compose.animation.core.tween(200)) togetherWith
                                            fadeOut(animationSpec = androidx.compose.animation.core.tween(200))
                                },
                                label = "QR_Transition"
                            ) { loading ->
                                if (loading) {
                                    CircularProgressIndicator(
                                        color = Color.Red,
                                        strokeWidth = 3.dp,
                                        modifier = Modifier.size(40.dp)
                                    )
                                } else {
                                    val matrix = qrBitMatrix
                                    if (matrix != null) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val width = matrix.width
                                            val height = matrix.height
                                            val sizeX = size.width / width
                                            val sizeY = size.height / height

                                            for (x in 0 until width) {
                                                for (y in 0 until height) {
                                                    if (matrix.get(x, y)) {
                                                        drawRect(
                                                            color = Color.Black,
                                                            topLeft = androidx.compose.ui.geometry.Offset(x * sizeX, y * sizeY),
                                                            size = androidx.compose.ui.geometry.Size(sizeX + 0.5f, sizeY + 0.5f)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        Text("Failed to load QR", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "Token: $activeQrData",
                            style = MaterialTheme.typography.labelMedium,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { activeQrData = null },
                        colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Close", fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF1E1E1E)
            )
        }
    }
}

@Composable
fun CalendarHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Red, Color(0xFF1A0000))
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.BottomStart
    ) {
        Column {
            Text(
                text = "Race Calendar",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "Your track day registrations",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
fun TabSwitcher(
    isUpcomingSelected: Boolean,
    upcomingCount: Int,
    pastCount: Int,
    onTabSelected: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .background(Color(0xFF121212), RoundedCornerShape(50))
            .padding(4.dp)
    ) {
        TabItem(
            text = "Upcoming ($upcomingCount)",
            isSelected = isUpcomingSelected,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(true) }
        )
        TabItem(
            text = "Results ($pastCount)",
            isSelected = !isUpcomingSelected,
            modifier = Modifier.weight(1f),
            onClick = { onTabSelected(false) }
        )
    }
}

@Composable
fun TabItem(text: String, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.clickable { onClick() },
        color = if (isSelected) Color(0xFF2C2C2C) else Color.Transparent,
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(8.dp),
            textAlign = TextAlign.Center,
            color = if (isSelected) Color.White else Color.Gray,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun SessionCard(
    ticket: Ticket,
    uiState: TicketListUiState,
    viewModel: TicketViewModel,
    onShowQrClick: () -> Unit,
    onGetDirectionsClick: () -> Unit,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(ticket.eventName, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Surface(color = Color(0xFF301515), shape = RoundedCornerShape(4.dp)) {
                    Text("Upcoming", color = Color.Red, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                }
            }

            Text(
                text = "${ticket.location} • Heat ${ticket.heat} • Target Gs: ${ticket.expectedGForces}",
                color = Color.Gray,
                modifier = Modifier.padding(start = 26.dp)
            )

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.padding(start = 4.dp)) {
                Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(" ${ticket.date}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(16.dp))
                Text(" ${ticket.startTime}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = onShowQrClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Show Registration Token", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onGetDirectionsClick,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00A651)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
                Text("Get Directions", fontWeight = FontWeight.Bold)
            }

            Box(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Button(
                    onClick = { viewModel.toggleReminderMenu(ticket.id) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6A1B9A)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("⏰ Set Race Reminder", fontWeight = FontWeight.Bold)
                }

                DropdownMenu(
                    expanded = uiState.expandedTicketId == ticket.id,
                    onDismissRequest = { viewModel.toggleReminderMenu(null) }
                ) {
                    val intervals = (30..360 step 15).toList()
                    intervals.forEach { totalMinutes ->
                        val hours = totalMinutes / 60
                        val mins = totalMinutes % 60
                        val label = if (hours > 0) {
                            if (mins > 0) "$hours uur en $mins min" else "$hours uur"
                        } else {
                            "$mins minuten"
                        }

                        DropdownMenuItem(
                            text = { Text("Starts in $label", fontSize = 14.sp) },
                            onClick = {
                                val localDateTime = ticket.date.atTime(ticket.startTime)
                                val zoneId = ZoneId.systemDefault()
                                val eventTimeMillis = localDateTime.atZone(zoneId).toInstant().toEpochMilli()

                                viewModel.setRaceReminder(
                                    ticketName = ticket.eventName,
                                    eventTimeMillis = eventTimeMillis,
                                    minutesBefore = totalMinutes
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultCard(ticket: Ticket) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Red, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(ticket.eventName, color = Color.White, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(Modifier.weight(1f))
                Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF22C55E), modifier = Modifier.size(20.dp))
            }

            Text(text = "${ticket.location} - Session Complete", color = Color.Gray, modifier = Modifier.padding(start = 26.dp))

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.padding(start = 4.dp)) {
                Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Text(" ${ticket.date}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                Spacer(Modifier.width(16.dp))
                Text(" ${ticket.startTime}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 16.dp),
                thickness = 0.5.dp,
                color = Color.DarkGray
            )

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Expected Limit", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    Text(ticket.expectedGForces, color = Color(0xFF22C55E), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Paddock Token", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    Text(ticket.qrCodeData.take(4), color = Color(0xFF3B82F6), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@SuppressLint("NewApi")
@Preview(showSystemUi = true)
@Composable
fun CalendarPreview() {
    val mockUpcomingTickets = listOf(
        Ticket("1", "Track Day Nürburgring", "Nürburgring Nordschleife", 50.3341, 6.9427, LocalDate.of(2026, 5, 15), LocalTime.of(10, 0), 1, "Aankomende Heats", "1.8G", "NURB-2026-H1-XYZ"),
        Ticket("2", "Laguna Seca Open Pit", "Laguna Seca", 36.5841, -121.7529, LocalDate.of(2026, 6, 20), LocalTime.of(14, 0), 2, "Aankomende Heats", "1.5G", "LAG-2026-H2-ABC")
    )

    val mockPastResults = listOf(
        Ticket("3", "Zandvoort Time Attack", "Circuit Zandvoort", 52.3888, 4.5409, LocalDate.of(2026, 3, 1), LocalTime.of(9, 0), 1, "Resultaten", "1.4G", "ZAND-2026-H1-RES")
    )

    val previewUiState = TicketListUiState(
        isLoading = false,
        upcomingTickets = mockUpcomingTickets,
        pastResults = mockPastResults
    )

    MaterialTheme {
        TicketListScreen(uiState = previewUiState)
    }
}