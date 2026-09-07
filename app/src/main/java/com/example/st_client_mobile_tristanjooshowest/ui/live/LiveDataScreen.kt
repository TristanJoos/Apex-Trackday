package com.example.st_client_mobile_tristanjooshowest.ui.live

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.st_client_mobile_tristanjooshowest.R
import com.example.st_client_mobile_tristanjooshowest.ui.permissions.AppPermission
import com.example.st_client_mobile_tristanjooshowest.ui.permissions.MainViewModel
import java.util.Locale

@Composable
fun LiveDataScreen(
    viewModel: LiveViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        mainViewModel.requestPermission(AppPermission.LOCATION)
        mainViewModel.requestPermission(AppPermission.CAMERA)
        viewModel.startGpsTelemetry()
    }

    LiveDataScreenContent(
        uiState = uiState,
        onToggleFlashlight = { viewModel.toggleFlashlight() },
        onToggleAutoFeature = { viewModel.toggleAutoFeature(it) }
    )
}

@Composable
fun LiveDataScreenContent(
    uiState: LiveUiState,
    onToggleFlashlight: () -> Unit = {},
    onToggleAutoFeature: (String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF6A1B9A), Color(0xFF2E1047))))
                .padding(24.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Text(uiState.currentTrackName, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text(if (uiState.isAtTrack) "Real-time circuit telemetry active" else "Track verification required", color = Color.White.copy(alpha = 0.7f))
            }
        }

        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

            if (uiState.isAtTrack) {
                GForceCard(
                    totalG = uiState.totalGForce,
                    lateral = uiState.lateralGForce,
                    longitudinal = uiState.longitudinalGForce,
                    maxGToday = uiState.maxGForceToday
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    GaugeCard("Speed", uiState.speedMph.toString(), "mph", uiState.speedProgress, Modifier.weight(1f))
                    GaugeCard("RPM", uiState.rpm.toString(), "rev/min", uiState.rpmProgress, Modifier.weight(1f))
                }

                LightSensorCard(uiState.lightSensorPercentage, uiState.lightSensorProgress, uiState.isFlashlightOn, onToggleFlashlight)

                AutoFlashlightControls(uiState, onToggleAutoFeature)

                SessionSummaryCard(uiState)

            } else {
                Surface(
                    color = Color(0xFF1A1A1A),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(top = 32.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text("🔒 Live Telemetry Locked", color = Color.Red, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "To open telemetry, your device GPS must be located within 5km of an approved track:\n\n" +
                                    "• Laguna Seca Raceway\n" +
                                    "• Nürburgring Nordschleife\n" +
                                    "• Circuit Zolder\n" +
                                    "• Circuit de Spa-Francorchamps",
                            color = Color.Gray,
                            fontSize = 14.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun AutoFlashlightControls(uiState: LiveUiState, onToggle: (String) -> Unit) {
    Surface(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Auto-Flashlight Settings", color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))

            AutoToggleRow("On Paddock Arrival", uiState.autoFlashlightAtTrack) { onToggle("track") }
            AutoToggleRow("In Low Light (<10 lux)", uiState.autoFlashlightLowLight) { onToggle("light") }
            AutoToggleRow("On Movement (>0.3G)", uiState.autoFlashlightMovement) { onToggle("movement") }
        }
    }
}

@Composable
fun AutoToggleRow(label: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.Red, checkedTrackColor = Color.Red.copy(alpha = 0.5f))
        )
    }
}

@Composable
fun GForceCard(totalG: Float, lateral: Float, longitudinal: Float, maxGToday: Float) {
    Surface(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("G-Force Meter", color = Color.White, fontWeight = FontWeight.Bold)
                Column(horizontalAlignment = Alignment.End) {
                    Text(String.format(Locale.US, "%.2fG", totalG), color = Color(0xFFBB86FC), fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text("Current", color = Color.Gray, fontSize = 10.sp)
                }
            }

            Box(modifier = Modifier.fillMaxWidth().height(250.dp), contentAlignment = Alignment.Center) {
                GForceVisualizer(lateral, longitudinal)
            }

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SmallStatBox("Lateral", String.format(Locale.US, "%.2fG", lateral), Modifier.weight(1f))
                SmallStatBox("Longitudinal", String.format(Locale.US, "%.2fG", longitudinal), Modifier.weight(1f))
                SmallStatBox("Max Today", String.format(Locale.US, "%.2fG", maxGToday), Modifier.weight(1f), labelColor = Color(0xFFEF5350))
            }
        }
    }
}

@Composable
fun GForceVisualizer(lat: Float, long: Float) {
    val animLat by animateFloatAsState(targetValue = lat, label = "lat")
    val animLong by animateFloatAsState(targetValue = long, label = "long")

    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2

        drawCircle(Color.Gray, radius, center, style = Stroke(1f), alpha = 0.3f)
        drawCircle(Color.Gray, radius * 0.6f, center, style = Stroke(1f), alpha = 0.3f)
        drawCircle(Color.Gray, radius * 0.3f, center, style = Stroke(1f), alpha = 0.3f)

        drawLine(Color.Gray, Offset(center.x, 0f), Offset(center.x, size.height), strokeWidth = 1f, alpha = 0.3f)
        drawLine(Color.Gray, Offset(0f, center.y), Offset(size.width, center.y), strokeWidth = 1f, alpha = 0.3f)

        val scaleFactor = radius * 0.8f
        val dotX = (center.x + (animLat * scaleFactor)).coerceIn(0f, size.width)
        val dotY = (center.y - (animLong * scaleFactor)).coerceIn(0f, size.height)

        drawCircle(
            color = Color(0xFFBB86FC),
            radius = 12.dp.toPx(),
            center = Offset(dotX, dotY)
        )
        drawCircle(
            color = Color(0xFFBB86FC).copy(alpha = 0.3f),
            radius = 18.dp.toPx(),
            center = Offset(dotX, dotY)
        )
    }
}

@Composable
fun GaugeCard(label: String, value: String, unit: String, progress: Float, modifier: Modifier) {
    Surface(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(16.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, color = if (label == "Speed") Color(0xFF2196F3) else Color(0xFF22C55E), fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Text(unit, color = Color.Gray, fontSize = 12.sp)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                color = if (label == "Speed") Color(0xFF2196F3) else Color(0xFF22C55E),
                trackColor = Color.DarkGray
            )
        }
    }
}

@Composable
fun LightSensorCard(percentage: Int, progress: Float, isFlashlightOn: Boolean, onToggleFlashlight: () -> Unit) {
    Surface(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Light Sensor", color = Color.White, fontWeight = FontWeight.Bold)

                IconButton(onClick = onToggleFlashlight) {
                    Icon(
                        imageVector = if (isFlashlightOn) Icons.Default.FlashlightOn else Icons.Default.FlashlightOff,
                        contentDescription = "Toggle Flashlight",
                        tint = if (isFlashlightOn) Color.Yellow else Color.Gray
                    )
                }

                Text("$percentage%", color = Color.Yellow, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).height(8.dp),
                color = Color.Yellow,
                trackColor = Color.DarkGray
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Dark", color = Color.Gray, fontSize = 10.sp)
                Text("Bright", color = Color.Gray, fontSize = 10.sp)
            }

            Spacer(Modifier.height(16.dp))
            Text("Shift Light", color = Color.White, fontSize = 14.sp)

            val lowLightActive = percentage < 30
            Button(
                onClick = {},
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (lowLightActive) Color.Red else Color(0xFF2C2C2C),
                    contentColor = if (lowLightActive) Color.White else Color.Gray
                )
            ) {
                Text(if (lowLightActive) "⚠️ SHIFT NOW (LOW LIGHT)" else "Auto-monitoring active")
            }
        }
    }
}

@Composable
fun SessionSummaryCard(uiState: LiveUiState) {
    Surface(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Session Summary", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(16.dp))
            SummaryRow("Laps Completed", uiState.lapsCompleted.toString(), Color.White)
            SummaryRow("Best Lap Time", uiState.bestLapTime, Color(0xFF22C55E))
            SummaryRow("Avg Speed", "${uiState.avgSpeedMph} mph", Color.White)
            SummaryRow("Max G-Force", String.format(Locale.US, "%.2fG", uiState.maxGForceToday), Color(0xFFEF5350))
            SummaryRow("Track Temp", uiState.trackTempFahrenheit?.let { "$it°F" } ?: "N/A", Color.White)
        }
    }
}

@Composable
fun SummaryRow(label: String, value: String, valueColor: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray)
        Text(value, color = valueColor, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SmallStatBox(label: String, value: String, modifier: Modifier, labelColor: Color = Color(0xFFBB86FC)) {
    Surface(color = Color(0xFF2C2C2C), shape = RoundedCornerShape(8.dp), modifier = modifier) {
        Column(modifier = Modifier.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.Gray, fontSize = 10.sp)
            Text(value, color = labelColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LiveDataPreview() {
    val mockUiState = LiveUiState(
        totalGForce = 0.79f,
        lateralGForce = 0.78f,
        longitudinalGForce = 0.13f,
        maxGForceToday = 1.40f,
        lightSensorPercentage = 86,
        lightSensorProgress = 0.86f
    )

    MaterialTheme {
        LiveDataScreenContent(uiState = mockUiState)
    }
}