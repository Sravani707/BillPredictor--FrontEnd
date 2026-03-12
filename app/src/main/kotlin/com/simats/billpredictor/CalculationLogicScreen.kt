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

                Text(
                    text = "How Our AI Predicts Your Spending",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ---------------- STEP 1 ----------------
                CalculationStep(
                    number = 1,
                    title = "AI Pattern Learning",
                    description =
                        "The AI analyzes your historical expenses and learns your " +
                                "spending behaviour over time. Instead of using a simple " +
                                "average, it detects trends such as increasing or decreasing " +
                                "spending using machine learning regression analysis.",
                    amount = "Trend Prediction Generated",
                    amountColor = Color.Black
                )

                // ---------------- STEP 2 ----------------
                CalculationStep(
                    number = 2,
                    title = "Event Intelligence Adjustment",
                    description =
                        "The AI examines your planned special events and estimates " +
                                "their financial impact. These upcoming commitments are " +
                                "incorporated into the prediction to ensure realistic and " +
                                "context-aware forecasting.",
                    event = "Upcoming Events",
                    eventCost = "Added to prediction",
                    totalEventCost = "Event Cost Included",
                    amountColor = Color(0xFFFBC02D)
                )

                // ---------------- STEP 3 ----------------
                CalculationStep(
                    number = 3,
                    title = "Final AI Prediction",
                    description =
                        "The final forecast is produced by combining learned spending " +
                                "trends with event-based adjustments. This creates a personalized " +
                                "future expense prediction tailored to your behaviour.",
                    amount = "AI Predicted Expense",
                    amountColor = Color(0xFF34A853),
                    isLast = true
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
                            Color(0xFF34A853)
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
                        .height(110.dp)
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
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (amount != null) {
                Text(
                    text = amount,
                    color = amountColor,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            if (event != null && eventCost != null && totalEventCost != null) {

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(event, color = Color(0xFFFBC02D))
                        Text(eventCost, color = Color(0xFFFBC02D))
                    }

                    Text(
                        totalEventCost,
                        color = Color(0xFFFBC02D),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PredictionSummaryCard() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White)
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
                Text("Trend-Based Prediction")
                Text("Calculated by AI", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Event Adjustments")
                Text("Included", fontWeight = FontWeight.Medium)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
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