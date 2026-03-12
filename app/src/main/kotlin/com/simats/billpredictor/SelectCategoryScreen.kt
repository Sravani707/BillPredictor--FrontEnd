package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectCategoryScreen(
    onBackClicked: () -> Unit,
    onCategorySelected: (String) -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Category") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            BottomNavigationBar(
                currentScreen = currentScreen,
                onNavigate = onNavigate
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(categories) { category ->
                CategoryGridItem(category = category, onCategorySelected = onCategorySelected)
            }
        }
    }
}

data class Category(val name: String, val icon: ImageVector, val color: Color, val iconColor: Color)

@Composable
fun CategoryGridItem(category: Category, onCategorySelected: (String) -> Unit) {
    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .clickable { onCategorySelected(category.name) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(category.color),
                contentAlignment = Alignment.Center
            ) {
                Icon(category.icon, contentDescription = category.name, tint = category.iconColor, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(category.name, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SelectCategoryScreenPreview() {
    SelectCategoryScreen(
        onBackClicked = {},
        onCategorySelected = {},
        currentScreen = Screen.Home,
        onNavigate = { _: Screen -> }
    )
}
