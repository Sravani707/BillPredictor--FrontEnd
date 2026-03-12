package com.simats.billpredictor

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.network.ExpenseApi
import com.simats.billpredictor.network.PredictRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictionScreen(
    userId: Int,
    api: ExpenseApi,
    viewModel: ExpenseViewModel,
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    var predictedAmount by remember { mutableStateOf<Double?>(null) }
    var nextMonthPrediction by remember { mutableStateOf<Double?>(null) }
    var eventImpact by remember { mutableStateOf<Double?>(null) }
    var eventsCount by remember { mutableStateOf(0) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    LaunchedEffect(userId) {
        viewModel.fetchTrends(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Prediction", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
                .background(Color(0xFFF8F9FA))
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // MAIN AI CARD
            Card(
                shape = RoundedCornerShape(28.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color(0xFFF5F5FF))
                        .padding(28.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE6E0FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🧠", fontSize = 36.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Predicted Next Month",
                        fontSize = 16.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = predictedAmount?.let { "₹$it" } ?: "---",
                        fontSize = 42.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF4A90E2)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 1. ADD EVENT BLOCK
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.NewEvent) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color(0xFF4A90E2))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Add event for any month", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // EVENT IMPACT SECTION
            Text(
                text = "Upcoming Events Impact",
                modifier = Modifier.align(Alignment.Start),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF4E5))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.CalendarToday,
                            contentDescription = "Event",
                            tint = Color(0xFFF2994A),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (eventsCount > 0) "$eventsCount upcoming event(s)" else "No upcoming events",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Expected increase: ${eventImpact?.let { "₹$it" } ?: "₹0.00"}",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NEXT MONTH CARD
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Next Month Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(
                        text = nextMonthPrediction?.let { "₹$it" } ?: "---",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = Color(0xFF27AE60)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. HOW IS THIS CALCULATED BLOCK
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(Screen.CalculationLogic) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("How is this calculated?", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(
                            "Tap to see details.",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    }
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color.LightGray)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. TRENDING BLOCKS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(Screen.TrendingUp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = Color(0xFFD32F2F), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Trending Up", fontWeight = FontWeight.Bold, color = Color(0xFFD32F2F), fontSize = 15.sp)
                        Text("View categories", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onNavigate(Screen.TrendingDown) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Icon(Icons.AutoMirrored.Filled.TrendingDown, contentDescription = null, tint = Color(0xFF388E3C), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Trending Down", fontWeight = FontWeight.Bold, color = Color(0xFF388E3C), fontSize = 15.sp)
                        Text("View categories", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // FETCH BUTTON
            Button(
                onClick = {
                    isLoading = true
                    errorMessage = ""
                    scope.launch {
                        try {
                            val response = api.predict(PredictRequest(userId))
                            predictedAmount = response.expense_prediction
                            eventImpact = response.event_added_amount
                            nextMonthPrediction = response.ai_predicted_next_month_expense
                            eventsCount = response.events_count
                        } catch (e: Exception) {
                            errorMessage = "Error: ${e.message}"
                            Log.e("PREDICT", e.message ?: "Unknown error")
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2))
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(
                        text = if (predictedAmount == null) "Get AI Prediction" else "Recalculate",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(errorMessage, color = Color.Red, fontSize = 14.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
