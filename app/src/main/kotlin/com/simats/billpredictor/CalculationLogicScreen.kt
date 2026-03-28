package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
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
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
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

            item {
                // Header Badge
                Surface(
                    color = Color(0xFFE8F5E9),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Powered by Time-Series AI Forecasting",
                            color = Color(0xFF2E7D32),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Text(
                    text = "How Our AI Predicts Your Spending",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------- STEP 1 ----------------
                CalculationStep(
                    number = 1,
                    title = "Prophet ML Learning",
                    description =
                        "The AI studies your past monthly expenses using a time-series machine learning model (Prophet). Instead of drawing a simple straight line, it learns real spending patterns over time, identifying trends and changes in behaviour across months.",
                    amount = "AI Trend Forecast Generated",
                    amountColor = Color(0xFF2E7D32)
                )

                // ---------------- STEP 2 ----------------
                CalculationStep(
                    number = 2,
                    title = "Event Intelligence Adjustment",
                    description =
                        "The AI analyzes your upcoming planned events such as festivals, travel, or personal occasions. Estimated event costs are automatically added to the prediction to make the forecast realistic and context-aware.",
                    event = "Upcoming Events Included in Prediction",
                    totalEventCost = "Event Cost Added Automatically",
                    amountColor = Color(0xFFFBC02D)
                )

                // ---------------- STEP 3 ----------------
                CalculationStep(
                    number = 3,
                    title = "= Final AI Prediction",
                    description =
                        "The final prediction combines AI-learned spending trends with event-based adjustments. This produces a personalized future expense estimate tailored specifically to your financial behaviour.",
                    isLast = true,
                    amountColor = Color(0xFF34A853)
                )

                Spacer(modifier = Modifier.height(32.dp))

                PredictionSummaryCard()
            }
        }
    }
}

@Composable
fun CalculationStep(
    number: Int,
    title: String,
    description: String,
    amount: String? = null,
    amountColor: Color,
    event: String? = null,
    eventCost: String? = null,
    totalEventCost: String? = null,
    isLast: Boolean = false
) {

    Row(modifier = Modifier.padding(vertical = 8.dp)) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        if (number == 3)
                            Color(0xFF4CAF50)
                        else
                            Color(0xFF4285F4)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (number == 3) "=" else number.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(if (number == 1) 140.dp else 110.dp)
                        .background(Color.LightGray)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {

            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                fontSize = 14.sp,
                color = Color.DarkGray,
                lineHeight = 20.sp
            )

            if (amount != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = amount,
                    color = amountColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            if (event != null || totalEventCost != null) {

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (event != null) {
                        Text(
                            text = event,
                            color = Color(0xFFFBC02D),
                            fontSize = 12.sp,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (totalEventCost != null) {
                        Text(
                            totalEventCost,
                            color = Color(0xFFFBC02D),
                            fontWeight = FontWeight.Medium,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionSummaryCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "AI Prediction Summary",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Trend Forecast", color = Color.Gray)
                Text("Calculated by AI", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Event Adjustments", color = Color.Gray)
                Text("Included", fontWeight = FontWeight.Medium)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Final Predicted Expense", fontWeight = FontWeight.Bold)
                Text(
                    "AI Generated Result",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4285F4)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculationLogicScreenPreview() {
    BillpredictorTheme {
        CalculationLogicScreen(
            onBackClicked = {},
            currentScreen = Screen.Home,
            onNavigate = {}
        )
    }
}
