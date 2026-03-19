package com.simats.billpredictor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.simats.billpredictor.model.EventSavingsItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventSavingsPlannerScreen(
    navController: NavController,
    userId: Int,
    eventId: Int,
    viewModel: ExpenseViewModel
) {
    val savings by viewModel.eventSavingsPlan.collectAsState()
    val savedMap by viewModel.savedStatusMap.collectAsState()
    val loading by viewModel.isLoading.collectAsState()

    LaunchedEffect(eventId) {
        if (eventId != 0) {
            viewModel.fetchEventSavings(userId, eventId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Event Savings Planner")
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            if (loading) {
                item {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            if (!loading && savings.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No savings plan available",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            }

            items(savings) { item ->
                SavingsRow(item, savedMap, viewModel)
            }
        }
    }
}

@Composable
fun SavingsRow(
    item: EventSavingsItem,
    savedMap: Map<Int, Boolean>,
    viewModel: ExpenseViewModel
) {
    // Use ViewModel's map as source of truth
    val isSaved by remember(item.id, savedMap) {
        derivedStateOf { savedMap[item.id] ?: item.saved }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                // Clicking anywhere on card toggles checkbox
                viewModel.updateSavingStatus(item.id, !isSaved)
            },
        colors = CardDefaults.cardColors(
            containerColor = if (isSaved) MaterialTheme.colorScheme.secondaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.month_year, style = MaterialTheme.typography.titleMedium)
                Text("Save ₹${item.required_amount}", color = MaterialTheme.colorScheme.primary)
            }

            Checkbox(
                checked = isSaved,
                onCheckedChange = { checked ->
                    // Only update via ViewModel
                    viewModel.updateSavingStatus(item.id, checked)
                }
            )
        }
    }
}
