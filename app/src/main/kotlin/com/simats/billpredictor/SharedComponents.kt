package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Calendar

@Composable
fun BottomNavigationBar(currentScreen: Screen, onNavigate: (Screen) -> Unit) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.History, contentDescription = "History") },
            label = { Text("History") },
            selected = currentScreen is Screen.History,
            onClick = { onNavigate(Screen.History) }
        )
        // This is a placeholder for the FAB
        Box(modifier = Modifier.weight(1f))
        NavigationBarItem(
            icon = { Icon(Icons.Default.Psychology, contentDescription = "Predict") },
            label = { Text("Predict") },
            selected = currentScreen is Screen.Prediction,
            onClick = { onNavigate(Screen.Prediction) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentScreen is Screen.Profile,
            onClick = { onNavigate(Screen.Profile) }
        )
    }
}

@Composable
fun ExpenseItem(icon: ImageVector, category: String, date: String, amount: String, color: Color, iconColor: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = category, tint = iconColor)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (date.isNotEmpty()) {
                        Text(date)
                    }
                }
            }
            Text(amount, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }
    }
}

fun getCategory(name: String): Category {
    val categories = listOf(
        Category("Food", Icons.Default.Fastfood, Color(0xFFFFF9C4), Color(0xFFFBC02D)),
        Category("Transport", Icons.Default.DirectionsBus, Color(0xFFE3F2FD), Color(0xFF4285F4)),
        Category("Shopping", Icons.Default.ShoppingBag, Color(0xFFFCE4EC), Color(0xFFEC407A)),
        Category("Bills", Icons.Default.Receipt, Color(0xFFF3E5F5), Color(0xFF8E24AA)),
        Category("Entertainment", Icons.Default.Movie, Color(0xFFE8EAF6), Color(0xFF5C6BC0)),
        Category("Health", Icons.Default.Favorite, Color(0xFFFFEBEE), Color(0xFFD32F2F)),
        Category("Education", Icons.Default.School, Color(0xFFE8F5E9), Color(0xFF34A853)),
        Category("Other", Icons.Default.MoreHoriz, Color(0xFFE0E0E0), Color(0xFF616161))
    )
    return categories.first { it.name == name }
}

fun formatDate(millis: Long): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
}

@Composable
fun ExpenseDetailRow(icon: ImageVector, text: String, actionText: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = text)
                Spacer(modifier = Modifier.width(16.dp))
                Text(text, fontSize = 16.sp)
            }
            Text(actionText, color = Color(0xFF4285F4), fontWeight = FontWeight.Bold)
        }
    }
}
