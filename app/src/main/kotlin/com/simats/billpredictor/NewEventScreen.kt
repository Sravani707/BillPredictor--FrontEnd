package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.network.ExpenseApi
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventScreen(
    userId: Int,
    api: ExpenseApi,
    viewModel: ExpenseViewModel,
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {

    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableStateOf("") }
    var estimatedCost by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    val scrollState = rememberScrollState()
    
    val todayMillis = System.currentTimeMillis()
    val datePickerState = rememberDatePickerState(
        selectableDates = object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                // allow only today & future
                // subtracting 24 hours in millis to ensure today is definitely selectable regardless of timezone offsets at start of day
                return utcTimeMillis >= todayMillis - (24 * 60 * 60 * 1000)
            }

            override fun isSelectableYear(year: Int): Boolean {
                return year >= Calendar.getInstance().get(Calendar.YEAR)
            }
        }
    )

    // -------- DATE PICKER --------
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        eventDate = sdf.format(Date(millis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // -------- CONFIRMATION DIALOG --------
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Confirm Save") },
            text = { Text("Are you sure you want to save the event?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showConfirmDialog = false
                        val request = AddEventRequest(
                            user_id = userId,
                            event_name = eventName,
                            event_date = eventDate,
                            estimated_cost = estimatedCost.toDoubleOrNull() ?: 0.0
                        )
                        viewModel.addEvent(request) {
                            onNavigate(Screen.EventSaved)
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Add New Event", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .background(Color.White)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Event Name
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth()
            )

            // Event Date
            OutlinedTextField(
                value = eventDate,
                onValueChange = {},
                readOnly = true,
                label = { Text("Event Date (YYYY-MM-DD)") },
                trailingIcon = {
                    Icon(
                        Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.clickable { showDatePicker = true }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true }
            )

            // Estimated Cost
            OutlinedTextField(
                value = estimatedCost,
                onValueChange = { estimatedCost = it },
                label = { Text("Estimated Cost (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            // Prediction Impact Card
            if (eventName.isNotBlank() && eventDate.isNotBlank() && estimatedCost.isNotBlank()) {
                val monthName = try {
                    val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(eventDate)
                    SimpleDateFormat("MMMM", Locale.getDefault()).format(date!!)
                } catch (e: Exception) {
                    "this month"
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE3F2FD))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            buildAnnotatedString {
                                append("Adding \"")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(eventName)
                                }
                                append("\" will increase your ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append(monthName)
                                }
                                append(" prediction by ")
                                withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("₹$estimatedCost")
                                }
                                append(".")
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // SAVE BUTTON
            Button(
                onClick = {
                    if (eventName.isNotBlank() && eventDate.isNotBlank()) {
                        showConfirmDialog = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                enabled = !isLoading && eventName.isNotBlank() && eventDate.isNotBlank() && estimatedCost.toDoubleOrNull() != null
            ) {

                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text("Save Event")
                }
            }
        }
    }
}
