package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.ui.theme.BillpredictorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseTrendsScreen(
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Expense Trends") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
            item { BarChart() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { MonthlyHistory() }
        }
    }
}

@Composable
fun BarChart() {
    val data = listOf(
        "Oct" to 1200f,
        "Nov" to 1900f,
        "Dec" to 1500f,
        "Jan" to 1100f,
        "Feb" to 1400f,
        "Mar" to 900f
    )
    val maxVal = data.maxOf { it.second }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.Bottom
        ) {
            data.forEach { (month, value) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .height((value / maxVal * 200).dp) // Scale height
                            .width(30.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF4285F4))
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(month, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun MonthlyHistory() {
    Column {
        Text("Monthly History", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        MonthlyHistoryItem("Mar 2025/26", "\$900")
        MonthlyHistoryItem("Feb 2025/26", "\$1400")
        MonthlyHistoryItem("Jan 2025/26", "\$1100")
        MonthlyHistoryItem("Dec 2025/26", "\$1500")
        MonthlyHistoryItem("Nov 2025/26", "\$1900")
        MonthlyHistoryItem("Oct 2025/26", "\$1200")
    }
}

@Composable
fun MonthlyHistoryItem(month: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(month, color = Color.Gray)
        Text(amount, fontWeight = FontWeight.Bold, fontSize = 18.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun ExpenseTrendsScreenPreview() {
    BillpredictorTheme {
        ExpenseTrendsScreen(onBackClicked = {}, currentScreen = Screen.Home, onNavigate = {})
    }
}
