package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
fun CalculationLogicScreen(
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculation Logic") },
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
            item {
                Text("How We Predict Your Spending", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
                CalculationStep(number = 1, title = "Historical Average", description = "Average of your last 3 months of spending.", amount = "\$049", amountColor = Color.Black)
                CalculationStep(number = 2, title = "Special Events ( February )", description = "1 event planned this month.", event = "valantine", eventCost = "+ 1000", totalEventCost = "1000 S", amountColor = Color(0xFFFBC02D))
                CalculationStep(number = 3, title = "Final Prediction ( February )", description = "Average + Events = Predicted Total", amount = "\$049", amountColor = Color(0xFF34A853), isLast = true)
                Spacer(modifier = Modifier.height(32.dp))
                NextMonthForecastCard()
            }
        }
    }
}

@Composable
fun CalculationStep(number: Int, title: String, description: String, amount: String? = null, amountColor: Color, event: String? = null, eventCost: String? = null, totalEventCost: String? = null, isLast: Boolean = false) {
    Row(modifier = Modifier.padding(vertical = 8.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (number == 3) Color(0xFF34A853) else Color(0xFF4285F4)),
                contentAlignment = Alignment.Center
            ) {
                Text(if (number == 3) "=" else number.toString(), color = Color.White, fontWeight = FontWeight.Bold)
            }
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(100.dp)
                        .background(Color.LightGray)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(description)
            if (amount != null) {
                Text(amount, color = amountColor, fontWeight = FontWeight.Bold, fontSize = 24.sp)
            }
            if (event != null && eventCost != null && totalEventCost != null) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(event, color = Color(0xFFFBC02D))
                        Text(eventCost, color = Color(0xFFFBC02D))
                    }
                    Text(totalEventCost, color = Color(0xFFFBC02D), fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }
    }
}

@Composable
fun NextMonthForecastCard() {
    Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Next Month: March", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("3-Month Average")
                Text("\$569", fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Events (0)")
                Text("+ \$0", fontWeight = FontWeight.Medium)
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Predicted Total", fontWeight = FontWeight.Bold)
                Text("\$569", fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculationLogicScreenPreview() {
    BillpredictorTheme {
        CalculationLogicScreen(onBackClicked = {}, currentScreen = Screen.Home, onNavigate = {})
    }
}
