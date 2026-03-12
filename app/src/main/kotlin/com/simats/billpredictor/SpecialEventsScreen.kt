package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.simats.billpredictor.network.ExpenseApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Robust date parser that handles various common formats and removes time/GMT suffixes.
 */
fun parseEventDate(dateString: String?): Date? {
    if (dateString.isNullOrBlank()) return null
    
    val cleanString = dateString.trim()
    
    // Try standard formats
    val formats = listOf(
        "yyyy-MM-dd",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss",
        "EEE, dd MMM yyyy HH:mm:ss 'GMT'", // For "Wed, 01 Jan 2025 00:00:00 GMT"
        "EEE, dd MMM yyyy"
    )
    
    for (format in formats) {
        try {
            // Use Locale.US for parsing standard server formats (GMT, Month names)
            val sdf = SimpleDateFormat(format, Locale.US)
            val date = sdf.parse(cleanString)
            if (date != null) return date
        } catch (e: Exception) {
            // Try next format
        }
    }
    
    // Fallback: If it contains yyyy-MM-dd at the start, try to extract it
    try {
        if (cleanString.length >= 10) {
            val datePart = cleanString.substring(0, 10)
            if (datePart.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
                return SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(datePart)
            }
        }
    } catch (e: Exception) {}

    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecialEventsScreen(
    navController: NavController,
    userId: Int,
    api: ExpenseApi,
    viewModel: ExpenseViewModel,
    onBackClicked: () -> Unit,
    onAddEventClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val events by viewModel.events.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<EventResponse?>(null) }

    val scope = rememberCoroutineScope()

    // Load events using ViewModel
    LaunchedEffect(userId) {
        if (userId > 0) {
            viewModel.fetchEvents(userId)
        }
    }

    // Group events by MONTH NAME (UPPERCASE)
    val groupedEvents = events.groupBy { event ->
        val date = parseEventDate(event.event_date)
        if (date != null) {
            SimpleDateFormat("MMMM", Locale.getDefault()).format(date).uppercase()
        } else {
            "UPCOMING"
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Special Events", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(currentScreen, onNavigate)
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                if (isLoading) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Color(0xFF4285F4))
                        }
                    }
                } else if (events.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No special events found.", color = Color.Gray)
                        }
                    }
                } else {

                    groupedEvents.forEach { (monthName, monthEvents) ->

                        item {
                            Text(
                                text = "($monthName)",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.sp,
                                color = Color(0xFF4285F4),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        }

                        items(monthEvents) { event ->
                            val date = parseEventDate(event.event_date)
                            val formattedDate = if (date != null) {
                                val formatter = SimpleDateFormat("EEEE, dd MMMM", Locale.getDefault())
                                formatter.format(date)
                            } else {
                                // Fallback: Strip anything that looks like time or GMT
                                event.event_date
                                    .split(" ", "T")[0] // Take only date part if possible
                                    .replace(Regex("\\d{2}:\\d{2}:\\d{2}.*"), "")
                                    .trim()
                            }

                            SpecialEventItem(
                                icon = Icons.Default.Event,
                                eventName = event.event_name,
                                eventDate = formattedDate,
                                cost = "₹${event.estimated_cost}",
                                onClick = {
                                    navController.navigate(Screen.EventSavingsPlanner.createRoute(userId, event.id))
                                },
                                onDeleteClick = {
                                    selectedEvent = event
                                    showDialog = true
                                }
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }

            // Add Event Button
            Button(
                onClick = onAddEventClicked,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Add New Event", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // Delete Confirmation Dialog
    if (showDialog && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Event?") },
            text = { Text("Are you sure you want to delete this event?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            try {
                                api.deleteEvent(selectedEvent!!.id)
                                viewModel.fetchEvents(userId) // Refresh list
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            showDialog = false
                        }
                    }
                ) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun SpecialEventItem(
    icon: ImageVector,
    eventName: String,
    eventDate: String,
    cost: String,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFE3F2FD)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF4285F4))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(eventName, fontWeight = FontWeight.Bold)
                    Text(text = eventDate, color = Color.Gray, fontSize = 12.sp)
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(cost, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.LightGray)
                }
            }
        }
    }
}
