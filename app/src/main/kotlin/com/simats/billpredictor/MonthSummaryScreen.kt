package com.simats.billpredictor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.network.ExpenseApi
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

data class CategoryUiSummary(
    val name: String,
    val amount: Float,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlySummaryScreen(
    userId: Int,
    api: ExpenseApi,
    onBackClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {

    val scope = rememberCoroutineScope()

    var totalSpent by remember { mutableFloatStateOf(0f) }
    var dailyAvg by remember { mutableFloatStateOf(0f) }
    var categorySummary by remember { mutableStateOf(listOf<CategoryUiSummary>()) }
    var isLoading by remember { mutableStateOf(true) }

    val colors = listOf(
        Color(0xFF00C49F),
        Color(0xFFE91E63),
        Color(0xFF3F51B5),
        Color(0xFFF44336),
        Color(0xFF2196F3),
        Color(0xFFFF9800)
    )

    val animatedSweeps = remember { mutableStateListOf<Float>() }

    LaunchedEffect(userId) {
        isLoading = true
        try {
            val response = api.getHistory(userId)

            /* ---------------- FIX START ---------------- */
            // FILTER ONLY CURRENT MONTH EXPENSES
            val expenses = response.expenses.filter {
                println("DATE FROM API = ${it.expense_date}")
                isCurrentMonth(it.expense_date)
            }
            /* ---------------- FIX END ---------------- */

            val grouped = expenses.groupBy { it.category_name ?: "Other" }

            val summary = grouped.entries.mapIndexed { index, entry ->

                val sum = entry.value.sumOf {
                    it.amount?.toDoubleOrNull() ?: 0.0
                }.toFloat()

                CategoryUiSummary(
                    name = entry.key,
                    amount = sum,
                    color = colors[index % colors.size]
                )
            }

            categorySummary = summary
            totalSpent = summary.sumOf { it.amount.toDouble() }.toFloat()

            // REAL DAYS IN CURRENT MONTH
            val calendar = Calendar.getInstance()
            val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
            dailyAvg = if (totalSpent > 0f) totalSpent / daysInMonth else 0f

            animatedSweeps.clear()
            summary.forEach { animatedSweeps.add(0f) }

            summary.forEachIndexed { index, cat ->

                val targetSweep =
                    if (totalSpent > 0f)
                        360f * (cat.amount / totalSpent)
                    else 0f

                scope.launch {
                    val anim = Animatable(0f)
                    anim.animateTo(
                        targetValue = targetSweep,
                        animationSpec = tween(900)
                    )
                    animatedSweeps[index] = anim.value
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Month Summary") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
        }
    ) { padding ->

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    "Total Spent this Month",
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Text(
                    "₹${totalSpent.toInt()}",
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Surface(
                    color = Color(0xFFF0F7FF),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Daily Average: ₹${dailyAvg.toInt()}",
                        modifier = Modifier.padding(16.dp),
                        color = Color(0xFF007AFF),
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                // DONUT CHART
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.size(220.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {

                        val strokeWidth = 36.dp.toPx()
                        val diameter = size.minDimension - strokeWidth
                        var startAngle = -90f

                        categorySummary.forEachIndexed { index, cat ->

                            val sweep = animatedSweeps.getOrNull(index) ?: 0f

                            drawArc(
                                color = cat.color,
                                startAngle = startAngle,
                                sweepAngle = sweep,
                                useCenter = false,
                                topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                                size = Size(diameter, diameter),
                                style = Stroke(
                                    width = strokeWidth,
                                    cap = StrokeCap.Round
                                )
                            )

                            startAngle += sweep
                        }
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Summary", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            "₹${totalSpent.toInt()}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    "Category Breakdown",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                categorySummary.forEach { cat ->

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {

                        Column(modifier = Modifier.padding(16.dp)) {

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {

                                Row(verticalAlignment = Alignment.CenterVertically) {

                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(cat.color)
                                    )

                                    Spacer(modifier = Modifier.width(8.dp))

                                    Text(cat.name, fontWeight = FontWeight.Bold)
                                }

                                Text(
                                    "₹${cat.amount.toInt()} (${if (totalSpent > 0) (cat.amount / totalSpent * 100).toInt() else 0}%)"
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            LinearProgressIndicator(
                                progress = { if (totalSpent > 0) cat.amount / totalSpent else 0f },
                                color = cat.color,
                                trackColor = Color(0xFFE0E0E0),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
