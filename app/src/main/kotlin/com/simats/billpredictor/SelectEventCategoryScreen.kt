package com.simats.billpredictor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectEventCategoryScreen(
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
            BottomAppBar(
                containerColor = Color.White,
                content = {
                    BottomNavigationBar(currentScreen, onNavigate)
                }
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

@Preview(showBackground = true)
@Composable
fun SelectEventCategoryScreenPreview() {
    SelectEventCategoryScreen(onBackClicked = {}, onCategorySelected = {}, currentScreen = Screen.Home, onNavigate = {})
}
