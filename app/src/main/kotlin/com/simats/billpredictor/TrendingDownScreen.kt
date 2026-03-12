package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingDownScreen(
    userId: Int,
    viewModel: ExpenseViewModel,
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val expenses by viewModel.expenses.collectAsState()

    LaunchedEffect(userId) {
        viewModel.fetchExpenses(userId)
    }

    val trends = remember(expenses) {
        viewModel.calculateTrends().second
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trending Down", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(currentScreen, onNavigate)
        }
    ) { padding ->
        if (trends.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No decreasing expenses this month", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(padding)
                    .padding(16.dp)
            ) {
                item {
                    TrendingDownSummary()
                    Spacer(modifier = Modifier.height(24.dp))
                }
                
                items(trends) { trend ->
                    TrendingDownCategoryItem(
                        category = trend.category,
                        percentage = "${kotlin.math.abs(trend.percentageChange).toInt()}%",
                        monthlyDecrease = "-₹${(trend.previousAmount - trend.currentAmount).toInt()} /mo"
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun TrendingDownSummary() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.TrendingDown,
                contentDescription = "Trending Down",
                tint = Color(0xFF34A853),
                modifier = Modifier.size(40.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Spending Trending Down",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Great job! These categories are decreasing compared to previous months",
            textAlign = TextAlign.Center,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun TrendingDownCategoryItem(category: String, percentage: String, monthlyDecrease: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFE8F5E9).copy(alpha = 0.3f))
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                category,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A)
            )
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.AutoMirrored.Filled.TrendingDown,
                        contentDescription = null,
                        tint = Color(0xFF34A853),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        percentage,
                        color = Color(0xFF34A853),
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    monthlyDecrease,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }
    }
}
