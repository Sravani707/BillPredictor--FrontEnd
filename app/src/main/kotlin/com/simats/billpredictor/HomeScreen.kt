package com.simats.billpredictor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.simats.billpredictor.model.EventSavingPlan
import com.simats.billpredictor.model.ExpenseItem
import com.simats.billpredictor.network.ExpenseApi
import com.simats.billpredictor.utils.EventPlannerUtils
import java.text.SimpleDateFormat
import java.util.*

fun getMonthYear(dateString: String): String {
    return try {
        val inputFormat =
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

        val outputFormat =
            SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)

        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "Unknown"
    }
}

fun getDayMonth(dateString: String): String {
    return try {
        val inputFormat =
            SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

        val outputFormat =
            SimpleDateFormat("dd MMM", Locale.ENGLISH)

        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        ""
    }
}

fun isCurrentMonth(dateString: String?): Boolean {
    return try {
        if (dateString.isNullOrEmpty()) return false

        val format = SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z",
            Locale.ENGLISH
        )

        val expenseDate = format.parse(dateString) ?: return false

        val expenseCal = Calendar.getInstance().apply {
            time = expenseDate
        }

        val nowCal = Calendar.getInstance()

        expenseCal.get(Calendar.MONTH) == nowCal.get(Calendar.MONTH) &&
        expenseCal.get(Calendar.YEAR) == nowCal.get(Calendar.YEAR)

    } catch (e: Exception) {
        false
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseItemCard(
    item: ExpenseItem,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .combinedClickable(
                onClick = { onEdit() },
                onLongClick = { showDialog = true }
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                val dateDisplay = getDayMonth(item.date)
                if (dateDisplay.isNotEmpty()) {
                    Text(
                        text = dateDisplay,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }
            Text(
                "₹${item.amount}",
                color = Color(0xFFD32F2F),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Expense Actions") },
            text = { Text("Choose an action for this expense.") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    onEdit()
                }) { Text("Edit") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDelete()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) { Text("Delete") }
            }
        )
    }
}

@Composable
fun QuickAction(text: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Surface(
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFFF5F5F5)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = text, tint = Color(0xFF4A90E2))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    userName: String,
    userId: Int,
    api: ExpenseApi,
    viewModel: ExpenseViewModel,
    onPredictClick: () -> Unit,
    onMonthSummaryClick: () -> Unit,
    onNavigate: (Screen) -> Unit,
    navController: NavController
) {
    val expenses by viewModel.expenses.collectAsState()
    val availableCategories by viewModel.availableCategories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<ExpenseItem?>(null) }

    var totalIncome by remember { mutableStateOf(0.0) }
    var eventPlans by remember {
        mutableStateOf<List<EventSavingPlan>>(emptyList())
    }

    val totalSpent = remember(expenses) {
        expenses.filter { isCurrentMonth(it.date) }
            .sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
    }

    val groupedExpenses = remember(expenses) {
        expenses
            .sortedByDescending {
                try {
                    SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH).parse(it.date)
                } catch (e: Exception) {
                    Date(0)
                }
            }
            .groupBy { getMonthYear(it.date) }
    }

    LaunchedEffect(userId) {
        if (userId > 0) {
            viewModel.loadExpenses(userId)
            viewModel.fetchCategories()
            
            // Fetch income
            try {
                val response = api.getIncome(userId)
                totalIncome = response.total_income
            } catch (e: Exception) {
                android.util.Log.e("HOME_DEBUG", "Error fetching income: ${e.message}")
            }
        }
    }

    LaunchedEffect(userId) {
        if (userId > 0) {
            try {
                val events = api.getEvents(userId)
                val plans = events.map { event ->
                    val cost = event.estimated_cost.toDoubleOrNull() ?: 0.0
                    val monthsLeft = EventPlannerUtils.monthsLeft(event.event_date)
                    val monthlySaving = EventPlannerUtils.monthlySaving(cost, monthsLeft)
                    EventSavingPlan(
                        eventId = event.id,
                        eventName = event.event_name,
                        monthlySaving = monthlySaving,
                        monthsLeft = monthsLeft
                    )
                }
                eventPlans = plans
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(currentScreen = Screen.Home, onNavigate = onNavigate)
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Welcome back,", fontSize = 16.sp, color = Color.Gray)
                Text(userName, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(8.dp, RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                listOf(Color(0xFF4A90E2), Color(0xFF356AE6))
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable { onMonthSummaryClick() }
                        .padding(24.dp)
                ) {
                    Column {
                        Text(
                            "Monthly Spending",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 14.sp
                        )
                        Text(
                            "₹${totalSpent.toInt()}",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "Financial Overview",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val savings = totalIncome - totalSpent
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.Start) {
                                Text("Income", color = Color.Gray, fontSize = 12.sp)
                                Text("₹${totalIncome.toInt()}",
                                    color = Color(0xFF2E7D32),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis)
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("Spent", color = Color.Gray, fontSize = 12.sp)
                                Text("₹${totalSpent.toInt()}",
                                    color = Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis)
                            }
                            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                                Text("Savings", color = Color.Gray, fontSize = 12.sp)
                                Text("₹${savings.toInt()}",
                                    color = if (savings >= 0) Color(0xFF2E7D32) else Color.Red,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (savings >= 0) "Great! You're saving money this month 🎉"
                            else "Warning: Spending exceeds income",
                            color = if (savings >= 0) Color(0xFF2E7D32) else Color.Red,
                            fontSize = 12.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text(
                            "🎯 Event Savings Planner",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (eventPlans.isEmpty()) {
                            Text("No upcoming events", color = Color.Gray, fontSize = 14.sp)
                        } else {
                            eventPlans.forEach { plan ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { 
                                            // CORRECT NAVIGATION CALL WITH userId AND eventId
                                            navController.navigate(Screen.EventSavingsPlanner.createRoute(userId, plan.eventId))
                                        }
                                        .padding(vertical = 6.dp)
                                ) {
                                    Text(
                                        plan.eventName,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        "Save ₹${plan.monthlySaving.toInt()} / month",
                                        color = Color(0xFF2E7D32),
                                        fontSize = 13.sp
                                    )
                                    Text(
                                        "${plan.monthsLeft} months left",
                                        color = Color.Gray,
                                        fontSize = 12.sp
                                    )
                                }
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), thickness = 0.5.dp, color = Color.LightGray)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    QuickAction("Add Expense", Icons.Default.Add) {
                        editingItem = null
                        showDialog = true
                    }
                    QuickAction("Add Income", Icons.Default.Add) {
                        onNavigate(Screen.AddIncome)
                    }
                    QuickAction("Add Event", Icons.AutoMirrored.Filled.EventNote) {
                        onNavigate(Screen.NewEvent)
                    }
                    QuickAction("View Events", Icons.Default.EventAvailable) {
                        onNavigate(Screen.SpecialEvents)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                Text("Recent Expenses", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (isLoading) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF4A90E2))
                    }
                }
            } else if (expenses.isEmpty()) {
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No transactions yet.", color = Color.Gray)
                        TextButton(onClick = { showDialog = true }) {
                            Text("Add your first expense")
                        }
                    }
                }
            } else {
                groupedExpenses.forEach { (month, monthExpenses) ->
                    stickyHeader {
                        Text(
                            text = month,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.background)
                                .padding(vertical = 8.dp)
                        )
                    }

                    items(
                        items = monthExpenses,
                        key = { item -> item.id }
                    ) { expense ->
                        ExpenseItemCard(
                            item = expense,
                            onDelete = {
                                viewModel.deleteExpense(expense.realId, userId) { }
                            },
                            onEdit = {
                                editingItem = expense
                                showDialog = true
                            }
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }

    if (showDialog) {
        AddEditExpenseDialog(
            item = editingItem,
            categories = availableCategories,
            onDismiss = {
                showDialog = false
                editingItem = null
            },
            onSave = { amount, categoryId ->
                if (editingItem != null) {
                    val updateRequest = UpdateExpenseRequest(
                        amount = amount,
                        category_id = categoryId
                    )
                    viewModel.updateExpense(editingItem!!.realId, updateRequest, userId) {
                        showDialog = false
                        editingItem = null
                    }
                } else {
                    val addRequest = AddExpenseRequest(
                        userId,
                        categoryId,
                        amount
                    )
                    viewModel.addExpense(addRequest) {
                        showDialog = false
                        editingItem = null
                    }
                }
            }
        )
    }
}
