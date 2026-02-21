package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    onBackClicked: () -> Unit,
    onCategoryClicked: () -> Unit,
    onDateClicked: () -> Unit,
    onNotesClicked: (String) -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    selectedCategory: String?,
    selectedDate: Long?,
    note: String?
) {
    var amount by remember { mutableStateOf("0.00") }
    var notes by remember { mutableStateOf(note ?: "Add notes...") }
    var showDialog by remember { mutableStateOf(false) }

    val category = getCategory(selectedCategory ?: "Food")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFF5F5F5))
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
                    onClick = { /* TODO */ },
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
        floatingActionButtonPosition = FabPosition.Center
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text("Amount", fontSize = 18.sp)
            BasicTextField(
                value = amount,
                onValueChange = { newAmount -> amount = newAmount },
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))

            ExpenseDetailRow(icon = category.icon, text = category.name, actionText = "Change", onClick = onCategoryClicked)
            Spacer(modifier = Modifier.height(16.dp))
            ExpenseDetailRow(icon = Icons.Default.CalendarToday, text = selectedDate?.let { formatDate(it) } ?: "Select Date", actionText = "Change", onClick = onDateClicked)
            Spacer(modifier = Modifier.height(16.dp))
            ExpenseDetailRow(icon = Icons.Default.Description, text = notes, actionText = "Edit", onClick = { onNotesClicked(notes) })

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4).copy(alpha = 0.7f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Save Expense", color = Color.White, fontSize = 18.sp)
            }
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Expense Saved") },
                text = { Text("Your expense has been saved successfully.") },
                confirmButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddExpenseScreenPreview() {
    BillpredictorTheme {
        AddExpenseScreen(onBackClicked = {}, onCategoryClicked = {}, onDateClicked = {}, onNotesClicked = {}, currentScreen = Screen.Home, onNavigate = {}, selectedCategory = "Food", selectedDate = null, note = null)
    }
}
