package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.ui.theme.BillpredictorTheme

@Composable
fun HomeScreen(
    onSummaryClicked: () -> Unit, 
    onPredictClicked: () -> Unit,
    onAddClicked: () -> Unit,
    onTrendsClicked: () -> Unit,
    onViewAllClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomAppBar(
                containerColor = Color.White,
                content = {
                    BottomNavigationBar(currentScreen, onNavigate)
                }
            )
        },
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                FloatingActionButton(
                    onClick = onAddClicked,
                    shape = CircleShape,
                    containerColor = Color(0xFF4285F4),
                    modifier = Modifier.offset(y = 60.dp) // Adjust this value to move the button down
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense", tint = Color.White)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Add", fontWeight = FontWeight.Medium, modifier = Modifier.offset(y = 60.dp))
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                WelcomeHeader("Sravs5!")
                Spacer(modifier = Modifier.height(24.dp))
                TotalSpentCard(onClick = onSummaryClicked)
                Spacer(modifier = Modifier.height(24.dp))
                QuickActions(onPredictClicked = onPredictClicked, onAddClicked = onAddClicked, onTrendsClicked = onTrendsClicked)
                Spacer(modifier = Modifier.height(24.dp))
                RecentExpenses(onViewAllClicked = onViewAllClicked)
            }
        }
    }
}

@Composable
fun WelcomeHeader(name: String) {
    Column {
        Text(text = "Welcome,", fontSize = 18.sp)
        Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TotalSpentCard(onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF4285F4))
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Total Spent this Month", color = Color.White.copy(alpha = 0.8f))
            Text("\$1,260", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Psychology, contentDescription = "Predicted", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Predicted: \$ 1049", color = Color.White)
                }
                Icon(Icons.Default.ChevronRight, contentDescription = "View Prediction", tint = Color.White)
            }
        }
    }
}

@Composable
fun QuickActions(onPredictClicked: () -> Unit, onAddClicked: () -> Unit, onTrendsClicked: () -> Unit) {
    Column {
        Text("Quick Actions", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            QuickActionItem(icon = Icons.Default.Add, text = "Add", color = Color(0xFFE3F2FD), iconColor = Color(0xFF4285F4), onClick = onAddClicked)
            QuickActionItem(icon = Icons.Default.Psychology, text = "Predict", color = Color(0xFFF3E5F5), iconColor = Color(0xFF8E24AA), onClick = onPredictClicked)
            QuickActionItem(icon = Icons.AutoMirrored.Filled.TrendingUp, text = "Trends", color = Color(0xFFE8F5E9), iconColor = Color(0xFF34A853), onClick = onTrendsClicked)
        }
    }
}

@Composable
fun QuickActionItem(icon: ImageVector, text: String, color: Color, iconColor: Color, onClick: (() -> Unit)? = null) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(color),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = text, tint = iconColor, modifier = Modifier.size(32.dp))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RecentExpenses(onViewAllClicked: () -> Unit) {
    Column {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Recent Expenses", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            TextButton(onClick = onViewAllClicked) {
                Text("View All", color = Color(0xFF4285F4))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            ExpenseItem(icon = Icons.Default.School, category = "Education", date = "2026-02-19", amount = "-\$80", color = Color(0xFFE8F5E9), iconColor = Color(0xFF34A853))
            ExpenseItem(icon = Icons.Default.DirectionsBus, category = "Transport", date = "2026-02-02", amount = "-\$8", color = Color(0xFFE3F2FD), iconColor = Color(0xFF4285F4))
            ExpenseItem(icon = Icons.Default.School, category = "Education", date = "2026-02-15", amount = "-\$30", color = Color(0xFFE8F5E9), iconColor = Color(0xFF34A853))
            ExpenseItem(icon = Icons.Default.Fastfood, category = "Food", date = "2026-02-21", amount = "-\$20", color = Color(0xFFFFF9C4), iconColor = Color(0xFFFBC02D))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    BillpredictorTheme {
        HomeScreen(onSummaryClicked = {}, onPredictClicked = {}, onAddClicked = {}, onTrendsClicked = {}, onViewAllClicked = {}, currentScreen = Screen.Home, onNavigate = {})
    }
}
