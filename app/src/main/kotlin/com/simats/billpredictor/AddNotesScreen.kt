package com.simats.billpredictor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNotesScreen(
    navController: NavController, // use NavController for navigation
    expenseId: Int? = null, // if you want to attach notes to an expense
    viewModel: ExpenseViewModel
) {
    var noteText by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expenseId == null) "Add Note" else "Edit Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Enter your note:", style = MaterialTheme.typography.titleMedium)

            OutlinedTextField(
                value = noteText,
                onValueChange = { noteText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                placeholder = { Text("Type your note here...") }
            )

            Button(
                onClick = {
                    // TODO: Call your ViewModel function to save note
                    // Example:
                    // viewModel.addNote(expenseId, noteText) { navController.popBackStack() }
                    navController.popBackStack() // remove after adding backend logic
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                Text(if (expenseId == null) "Add Note" else "Update Note")
            }

            if (isLoading) CircularProgressIndicator()
            errorMessage?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    }
}
