package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Fastfood
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.ui.theme.BillpredictorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp)
        ) {
            item { Text("Today", fontWeight = FontWeight.Bold, fontSize = 20.sp) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                ExpenseItem(icon = Icons.Default.School, category = "Education", date = "", amount = "-\$80", color = Color(0xFFE8F5E9), iconColor = Color(0xFF34A853))
            }

            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { Text("Yesterday", fontWeight = FontWeight.Bold, fontSize = 20.sp) }
            item { Spacer(modifier = Modifier.height(8.dp)) }
            item {
                ExpenseItem(icon = Icons.Default.DirectionsBus, category = "Transport", date = "", amount = "-\$8", color = Color(0xFFE3F2FD), iconColor = Color(0xFF4285F4))
            }
            item {
                ExpenseItem(icon = Icons.Default.Fastfood, category = "Food", date = "", amount = "-\$20", color = Color(0xFFFFF9C4), iconColor = Color(0xFFFBC02D))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HistoryScreenPreview() {
    BillpredictorTheme {
        HistoryScreen(onBackClicked = {}, currentScreen = Screen.History, onNavigate = {})
    }
}
