package com.example.st_client_mobile_tristanjooshowest.ui.tickets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.st_client_mobile_tristanjooshowest.R
import com.example.st_client_mobile_tristanjooshowest.domain.model.Ticket

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(
    ticketId: String,
    viewModel: TicketViewModel,
    onBackClick: () -> Unit
) {
    val ticket by viewModel.getTicketById(ticketId).collectAsState(initial = null)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Session Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = colorResource(R.color.background)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ticket?.let { t ->
                DetailCard(t)
                
                InfoSection(
                    title = "Event Information",
                    content = "This session at ${t.location} is scheduled for ${t.date} starting at ${t.startTime}. Please arrive at least 45 minutes early for the safety briefing."
                )

                InfoSection(
                    title = "Technical Requirements",
                    content = "Expected G-Forces for this heat: ${t.expectedGForces}. Ensure your tire pressures are adjusted accordingly and your harness is secure."
                )

                InfoSection(
                    title = "Check-in Instructions",
                    content = "Present your registration token (found on the main calendar page) at the Scrutineering bay. Your heat number is ${t.heat}."
                )
            } ?: Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.Red)
            }
        }
    }
}

@Composable
fun DetailCard(ticket: Ticket) {
    Surface(
        color = Color(0xFF1E1E1E),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, null, tint = Color.Red, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(12.dp))
                Text(
                    text = ticket.eventName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = ticket.location,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(start = 36.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp), color = Color.DarkGray)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DetailItem("Date", ticket.date.toString())
                DetailItem("Time", ticket.startTime.toString())
            }
            
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                DetailItem("Heat", "#${ticket.heat}")
                DetailItem("Target Gs", ticket.expectedGForces)
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String) {
    Column {
        Text(text = label, color = Color.Gray, style = MaterialTheme.typography.labelMedium)
        Text(text = value, color = Color.White, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun InfoSection(title: String, content: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF121212), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Info, null, tint = Color(0xFF3B82F6), modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(text = title, color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(8.dp))
        Text(text = content, color = Color.Gray, style = MaterialTheme.typography.bodyMedium)
    }
}