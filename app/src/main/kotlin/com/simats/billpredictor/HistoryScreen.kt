package com.simats.billpredictor

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.simats.billpredictor.network.ExpenseApi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    userId: Int,
    api: ExpenseApi,
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {

    var expenses by remember { mutableStateOf<List<ExpenseItemResponse>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(userId) {
        isLoading = true
        try {
            val response = api.getHistory(userId)
            expenses = response.expenses
            Log.d("HistoryScreen", "History loaded: ${response.expenses.size} items")
        } catch (e: Exception) {
            Log.e("HistoryScreen", "History API failed: ${e.message}")
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp)
        ) {

            if (isLoading) {
                item {
                    CircularProgressIndicator()
                }
            } else if (expenses.isEmpty()) {
                item {
                    Text("No expense history found.")
                }
            } else {
                items(expenses) { expense ->

                    val safeCategory = expense.category_name ?: "Other"
                    val safeAmount = expense.amount

                    ExpenseItem(
                        icon = when (safeCategory.lowercase()) {
                            "education" -> Icons.Default.School
                            "transport" -> Icons.Default.DirectionsBus
                            "food" -> Icons.Default.Fastfood
                            else -> Icons.Default.Fastfood
                        },
                        category = safeCategory,
                        date = "", // Date hidden from History Screen
                        amount = "-₹$safeAmount",
                        color = Color(0xFFE3F2FD),
                        iconColor = Color(0xFF4285F4)
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

fun mapToCategorySummary(expenses: List<ExpenseItemResponse>): List<CategorySummary> {
    return expenses.map {
        CategorySummary(
            amount = it.amount?.toDoubleOrNull() ?: 0.0,
            name = it.category_name ?: "Other"
        )
    }
}
