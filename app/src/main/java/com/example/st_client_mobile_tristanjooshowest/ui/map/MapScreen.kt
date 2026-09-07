package com.example.st_client_mobile_tristanjooshowest.ui.map

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.example.st_client_mobile_tristanjooshowest.R
import com.example.st_client_mobile_tristanjooshowest.domain.model.Ticket
import com.mapbox.geojson.Point
import com.mapbox.maps.MapboxExperimental
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.MapEffect
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.PointAnnotation
import com.mapbox.maps.extension.compose.annotation.generated.PolylineAnnotation
import com.mapbox.maps.extension.compose.annotation.rememberIconImage
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener

@OptIn(MapboxExperimental::class)
@Composable
fun MapScreen(
    tickets: List<Ticket>,
    onRequestLocationPermission: () -> Unit,
    onNavigateClick: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(tickets) {
        android.util.Log.d("MAP_DEBUG", "Aantal binnengekomen tickets op MapScreen: ${tickets.size}")
    }

    LaunchedEffect(Unit) {
        onRequestLocationPermission()
    }

    val startPoint = if (tickets.isNotEmpty()) {
        Point.fromLngLat(tickets[0].longitude, tickets[0].latitude)
    } else {
        Point.fromLngLat(-121.7529, 36.5841)
    }

    val mapViewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(10.0)
            center(startPoint)
        }
    }

    val trackLayouts = remember {
        mapOf(
            "Laguna Seca" to listOf(
                Point.fromLngLat(-121.7545, 36.5840),
                Point.fromLngLat(-121.7538, 36.5855),
                Point.fromLngLat(-121.7515, 36.5858),
                Point.fromLngLat(-121.7495, 36.5842),
                Point.fromLngLat(-121.7505, 36.5822),
                Point.fromLngLat(-121.7530, 36.5828),
                Point.fromLngLat(-121.7545, 36.5840)
            ),
            "Nürburgring" to listOf(
                Point.fromLngLat(6.9427, 50.3341),
                Point.fromLngLat(6.9450, 50.3360),
                Point.fromLngLat(6.9480, 50.3350),
                Point.fromLngLat(6.9460, 50.3320),
                Point.fromLngLat(6.9427, 50.3341)
            ),
            "Zandvoort" to listOf(
                Point.fromLngLat(4.5409, 52.3888),
                Point.fromLngLat(4.5430, 52.3905),
                Point.fromLngLat(4.5460, 52.3895),
                Point.fromLngLat(4.5440, 52.3870),
                Point.fromLngLat(4.5409, 52.3888)
            ),
            "Zolder" to listOf(
                Point.fromLngLat(5.2581, 50.9889),
                Point.fromLngLat(5.2610, 50.9905),
                Point.fromLngLat(5.2640, 50.9890),
                Point.fromLngLat(5.2600, 50.9875),
                Point.fromLngLat(5.2581, 50.9889)
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(R.color.background))
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(Brush.verticalGradient(listOf(Color(0xFF006400), Color(0xFF001A00))))
                .padding(24.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            Column {
                Text("Track Map", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Live Event Telemetry", color = Color.White.copy(alpha = 0.8f))
            }
        }
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.LightGray
        ) {
            val lucideVector = Lucide.MapPin
            val markerPainter = androidx.compose.ui.graphics.vector.rememberVectorPainter(image = lucideVector)
            val markerIcon = rememberIconImage(key = "lucide_track_pin", painter = markerPainter)

            MapboxMap(
                modifier = Modifier.fillMaxSize(),
                mapViewportState = mapViewportState
            ) {
                MapEffect(Unit) { mapView ->
                    mapView.location.apply {
                        enabled = true
                        pulsingEnabled = true

                        var listener: OnIndicatorPositionChangedListener? = null
                        listener = OnIndicatorPositionChangedListener { point ->
                            mapViewportState.setCameraOptions {
                                center(point)
                                zoom(14.0)
                            }
                            listener?.let { removeOnIndicatorPositionChangedListener(it) }
                        }
                        addOnIndicatorPositionChangedListener(listener)
                    }
                }

                val currentZoom = mapViewportState.cameraState?.zoom ?: 0.0
                if (currentZoom >= 13.0) {
                    trackLayouts.forEach { (name, points) ->
                        key("layout_$name") {
                            PolylineAnnotation(
                                points = points
                            ) {
                                lineColor = Color.Red
                                lineWidth = 4.0
                            }
                        }
                    }
                }

                tickets.forEach { ticket ->
                    key("marker_${ticket.id}") {
                        val ticketPoint = Point.fromLngLat(ticket.longitude, ticket.latitude)

                        PointAnnotation(
                            point = ticketPoint,
                            onClick = {
                                Toast.makeText(
                                    context,
                                    "Track: ${ticket.eventName}\nLocation: ${ticket.location}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                true
                            }
                        ) {
                            iconImage = markerIcon
                            iconSize = 1.2
                        }
                    }
                }
            }
        }
        Column(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DistanceCard()
            GetDirectionsCard(onNavigateClick)

            Text("Pre-Arrival Checklist", color = Color.White, style = MaterialTheme.typography.titleMedium)
            ChecklistSection()

            Text("Track Information", color = Color.White, style = MaterialTheme.typography.titleMedium)
            TrackInfoTable()
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun ChecklistSection() {
    var ticketReady by remember { mutableStateOf(true) }
    var vehiclePrepared by remember { mutableStateOf(true) }
    var techInspection by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ChecklistItem("Ticket Ready", "QR code downloaded", ticketReady) { ticketReady = it }
        ChecklistItem("Vehicle Prepared", "Safety check completed", vehiclePrepared) { vehiclePrepared = it }
        ChecklistItem("Tech Inspection", "At track upon arrival", techInspection) { techInspection = it }
    }
}

@Composable
fun ChecklistItem(title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Surface(
        color = Color(0xFF1A1A1A),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = Color(0xFF22C55E), uncheckedColor = Color.Gray)
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            Surface(
                color = if (checked) Color(0xFF142E1F) else Color(0xFF2C2C2C),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (checked) "Complete" else "Pending",
                    color = if (checked) Color(0xFF22C55E) else Color.Gray,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun TrackInfoTable() {
    Surface(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TrackInfoRow("Check-in Opens", "7:00 AM")
            TrackInfoRow("First Session", "9:00 AM")
            TrackInfoRow("Parking", "Paddock Area A")
            TrackInfoRow("Weather", "☀️ 72°F, Clear")
        }
    }
}

@Composable
fun TrackInfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
        Text(value, color = Color.White, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun DistanceCard() {
    Surface(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color(0xFF22C55E), modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Distance to Track", color = Color.White, modifier = Modifier.weight(1f))
                Surface(color = Color(0xFF2C2C2C), shape = RoundedCornerShape(12.dp)) {
                    Text("12.4 mi", color = Color.White, modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall)
                }
            }
            Text("Estimated arrival: 20 minutes · Check-in opens at 7:00 AM", color = Color.Gray, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
fun GetDirectionsCard(onNavigateClick: () -> Unit) {
    Surface(color = Color(0xFF1A1A1A), shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(color = Color(0xFF262E3D), shape = RoundedCornerShape(8.dp), modifier = Modifier.size(48.dp)) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.LocationOn, null, tint = Color.White)
                }
            }
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Get Directions", color = Color.White, fontWeight = FontWeight.Bold)
                Text("1021 Monterey Salinas Hwy\nSalinas, CA 93908", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
            }
            Button(onClick = onNavigateClick, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)), shape = RoundedCornerShape(8.dp)) {
                Text("Open Maps")
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MapScreenPreview() {
    val mockTickets = listOf(
        Ticket(
            id = "1",
            eventName = "Track Day Experience",
            location = "Paddock Area A",
            latitude = 36.5830,
            longitude = -121.7510,
            date = java.time.LocalDate.now(),
            startTime = java.time.LocalTime.of(9, 0),
            heat = 1,
            status = "Confirmed",
            expectedGForces = "2.4 G",
            qrCodeData = "MOCK_QR_CODE"
        )
    )

    MaterialTheme {
        MapScreen(
            tickets = mockTickets,
            onRequestLocationPermission = { },
            onNavigateClick = { }
        )
    }
}