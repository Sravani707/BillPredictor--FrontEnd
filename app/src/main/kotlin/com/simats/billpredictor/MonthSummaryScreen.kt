package com.simats.billpredictor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.ui.theme.BillpredictorTheme
import kotlin.math.atan2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthSummaryScreen(
    onBackClicked: () -> Unit,
    onAddClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Month Summary") },
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
                .background(Color.White)
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                TotalSpentSummary()
                Spacer(modifier = Modifier.height(24.dp))
                ExpenseDonutChart()
                Spacer(modifier = Modifier.height(24.dp))
                CategoryBreakdown()
            }
        }
    }
}

@Composable
fun TotalSpentSummary() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Total Spent")
        Text("\$1,260", fontSize = 48.sp, fontWeight = FontWeight.Bold)
        Text("Daily avg: \$ 105")
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFFFEBEE))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.ArrowUpward, contentDescription = "Over Prediction", tint = Color(0xFFD32F2F), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("211 over prediction", color = Color(0xFFD32F2F), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun ExpenseDonutChart() {
    val chartData = remember {
        listOf(
            "Education" to 46f,
            "Shopping" to 22f,
            "Entertainment" to 13f,
            "Health" to 10f,
            "Transport" to 8f,
            "Food" to 2f
        )
    }
    val colors = remember {
        listOf(
            Color(0xFF26A69A),
            Color(0xFFEC407A),
            Color(0xFF5C6BC0),
            Color(0xFFEF5350),
            Color(0xFF42A5F5),
            Color(0xFFFFA726)
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
fun CategoryBreakdown() {
    val chartData = remember {
        listOf(
            "Education" to (584f to Color(0xFF26A69A)),
            "Shopping" to (279f to Color(0xFFEC407A)),
            "Entertainment" to (160f to Color(0xFF5C6BC0)),
            "Health" to (121f to Color(0xFFEF5350)),
            "Transport" to (96f to Color(0xFF42A5F5)),
            "Food" to (20f to Color(0xFFFFA726))
        )
    }
    val total = chartData.sumOf { it.second.first.toDouble() }.toFloat()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Category Breakdown", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        chartData.forEach { (category, data) ->
            val (amount, color) = data
            val percentage = (amount / total) * 100
            CategoryItem(color = color, category = category, amount = "\$${amount.toInt()}", percentage = "(${percentage.toInt()}%)", progress = { percentage / 100f })
        }
    }
}

@Composable
fun CategoryItem(color: Color, category: String, amount: String, percentage: String, progress: () -> Float) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).background(color, CircleShape))
            Spacer(modifier = Modifier.width(8.dp))
            Text(category, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.weight(1f))
            Text(amount, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(4.dp))
            Text(percentage, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = progress,
            color = color,
            trackColor = Color.LightGray.copy(alpha = 0.4f),
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp))
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MonthSummaryScreenPreview() {
    BillpredictorTheme {
        MonthSummaryScreen(onBackClicked = {}, onAddClicked = {}, currentScreen = Screen.MonthSummary, onNavigate = {})
    }
}
