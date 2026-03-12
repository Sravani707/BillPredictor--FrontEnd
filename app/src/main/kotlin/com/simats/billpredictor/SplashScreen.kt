package com.simats.billpredictor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onTimeout: () -> Unit) {

    LaunchedEffect(Unit) {
        delay(3000) // Slightly longer to show the progress bar
        onTimeout()
    }

    // A cream/off-white background color similar to the design
    val backgroundColor = Color(0xFFF7F7F2)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = backgroundColor),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Main content (Logo)
        Image(
            painter = painterResource(id = R.drawable.expenseai_logo),
            contentDescription = "ExpenseAI Logo",
            modifier = Modifier
                .size(300.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Tagline from the design
        Text(
            text = "Intelligent Expense Management & Growth",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif,
            color = Color(0xFF4A4A4A)
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Progress bar similar to the design
        LinearProgressIndicator(
            modifier = Modifier
                .width(200.dp)
                .height(8.dp),
            color = Color(0xFF81C784), // A light green color matching the design
            trackColor = Color(0xFFE0E0E0)
        )
    }
}
