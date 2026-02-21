package com.simats.billpredictor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.atan2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MarchForecastScreen(
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("March Forecast") },
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
            item { MarchForecastSummary() }
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item { PredictedByCategory() }
        }
    }
}

@Composable
fun MarchForecastSummary() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Predicted for March", color = Color.Gray)
        Text("\$1569", fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(24.dp))
        MarchDonutChart()
    }
}

@Composable
fun MarchDonutChart() {
    val chartData = remember {
        listOf(
            "Education" to 323f,
            "Health" to 307f,
            "Transport" to 244f,
            "Shopping" to 244f,
            "Entertainment" to 186f,
            "Food" to 123f,
            "Other" to 107f,
            "Bills" to 34f
        )
    }
    val colors = remember {
        listOf(
            Color(0xFF26A69A),
            Color(0xFFEF5350),
            Color(0xFF42A5F5),
            Color(0xFFEC407A),
            Color(0xFF5C6BC0),
            Color(0xFFFFA726),
            Color(0xFFBDBDBD),
            Color(0xFF8E24AA)
        )
    }

    var selectedCategory by remember { mutableStateOf<String?>(null) }
    val animationProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animationProgress.animateTo(1f, animationSpec = tween(durationMillis = 1500))
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(250.dp)
            .pointerInput(Unit) {
                detectTapGestures {
                    val angle = (atan2(it.y - size.height / 2, it.x - size.width / 2) * (180 / Math.PI)).toFloat()
                    val normalizedAngle = (angle + 360) % 360

                    var startAngle = -90f
                    val total = chartData.sumOf { it.second.toDouble() }.toFloat()

                    chartData.forEachIndexed { index, (category, value) ->
                        val sweepAngle = (value / total) * 360f
                        if (normalizedAngle in startAngle..startAngle + sweepAngle) {
                            selectedCategory = if (selectedCategory == category) null else category
                        }
                        startAngle += sweepAngle
                    }
                }
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) { 
            val total = chartData.sumOf { it.second.toDouble() }.toFloat()
            var startAngle = -90f

            chartData.forEachIndexed { index, (category, value) ->
                val sweepAngle = (value / total) * 360f * animationProgress.value
                val isSelected = selectedCategory == category
                val strokeWidth = if (isSelected) 50f else 40f

                drawArc(
                    color = colors[index],
                    startAngle = startAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
                startAngle += (value / total) * 360f
            }
        }

        Text(
            text = selectedCategory ?: "",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PredictedByCategory() {
    val chartData = remember {
        listOf(
            "Education" to (323f to Color(0xFF26A69A)),
            "Health" to (307f to Color(0xFFEF5350)),
            "Transport" to (244f to Color(0xFF42A5F5)),
            "Shopping" to (244f to Color(0xFFEC407A)),
            "Entertainment" to (186f to Color(0xFF5C6BC0)),
            "Food" to (123f to Color(0xFFFFA726)),
            "Other" to (107f to Color(0xFFBDBDBD)),
            "Bills" to (34f to Color(0xFF8E24AA))
        )
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Predicted by Category", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        chartData.forEach { (category, data) ->
            val (amount, color) = data
            PredictedCategoryItem(color = color, category = category, amount = "\$${amount.toInt()}")
        }
    }
}

@Composable
fun PredictedCategoryItem(color: Color, category: String, amount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(16.dp))
            Text(category, fontWeight = FontWeight.Medium)
        }
        Text(amount, fontWeight = FontWeight.Bold)
    }
}

@Preview(showBackground = true)
@Composable
fun MarchForecastScreenPreview() {
    MarchForecastScreen(onBackClicked = {}, currentScreen = Screen.Home, onNavigate = {})
}
