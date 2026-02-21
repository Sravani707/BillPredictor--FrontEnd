package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewEventScreen(
    onBackClicked: () -> Unit,
    onSaveEvent: () -> Unit,
    onCategoryClicked: () -> Unit,
    selectedCategory: String?,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    var eventName by remember { mutableStateOf("Valentine") }
    var eventDate by remember { mutableStateOf("2026-02-14") }
    var estimatedCost by remember { mutableStateOf("1000") }

    val category = remember(selectedCategory) {
        getCategory(selectedCategory ?: "Food")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Event") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                content = {
                    BottomNavigationBar(currentScreen, onNavigate)
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = eventDate,
                onValueChange = { eventDate = it },
                label = { Text("Event Date") },
                trailingIcon = { Icon(Icons.Default.CalendarToday, contentDescription = "Select Date") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = estimatedCost,
                onValueChange = { estimatedCost = it },
                label = { Text("Estimated Cost ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            ExpenseDetailRow(icon = category.icon, text = category.name, actionText = "Change", onClick = onCategoryClicked)
            Spacer(modifier = Modifier.height(24.dp))
            PredictionImpactCard(eventName, estimatedCost)
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = onSaveEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Event")
            }
        }
    }
}

@Composable
fun PredictionImpactCard(eventName: String, cost: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Icon(Icons.Default.BarChart, contentDescription = "Prediction Impact", tint = Color(0xFFFBC02D))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("Prediction Impact", fontWeight = FontWeight.Bold)
                Text("Adding \"$eventName\" will increase your February 2026 prediction by \$$cost.")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NewEventScreenPreview() {
    NewEventScreen(onBackClicked = {}, onSaveEvent = {}, onCategoryClicked = {}, selectedCategory = "Food", currentScreen = Screen.Home, onNavigate = {})
}
