package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.ui.theme.BillpredictorTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingUpScreen(
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Trending Up") },
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
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item { TrendingUpSummary() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { TrendingCategoryItem("Food", "15%", "+\$45 /mo") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { TrendingCategoryItem("Entertainment", "22%", "+\$30 /mo") }
            item { Spacer(modifier = Modifier.height(16.dp)) }
            item { TrendingCategoryItem("Shopping", "8%", "+\$25 /mo") }
        }
    }
}

@Composable
fun TrendingUpSummary() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFFFFEBEE)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Trending Up", tint = Color(0xFFD32F2F), modifier = Modifier.size(40.dp))
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Spending Trending Up", fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text("These categories are increasing compared to previous months", textAlign = TextAlign.Center)
    }
}

@Composable
fun TrendingCategoryItem(category: String, percentage: String, monthlyIncrease: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(category, fontSize = 18.sp, fontWeight = FontWeight.Medium)
            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = "Trending Up", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(percentage, color = Color(0xFFD32F2F), fontWeight = FontWeight.Bold)
                }
                Text(monthlyIncrease)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun TrendingUpScreenPreview() {
    BillpredictorTheme {
        TrendingUpScreen(onBackClicked = {}, currentScreen = Screen.Home, onNavigate = {})
    }
}
