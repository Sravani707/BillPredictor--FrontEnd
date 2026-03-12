package com.simats.billpredictor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    userId: Int,
    expenseId: Int?,
    initialAmount: String = "",
    initialCategoryName: String = "",
    viewModel: ExpenseViewModel,
    onBackClicked: () -> Unit
) {
    val categories by viewModel.availableCategories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var amount by remember { mutableStateOf(initialAmount) }
    var selectedCategoryName by remember { mutableStateOf(initialCategoryName) }

    LaunchedEffect(Unit) {
        viewModel.fetchCategories()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (expenseId == null) "Add Expense" else "Edit Expense") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
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
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // CATEGORY DROPDOWN
            CategoryDropdown(
                categories = categories,
                selectedCategoryName = selectedCategoryName,
                onCategorySelected = { category ->
                    selectedCategoryName = category.name
                }
            )

            // AMOUNT FIELD
            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val category = categories.find { it.name == selectedCategoryName }
                    val amountValue = amount.toFloatOrNull()

                    if (category != null && amountValue != null && amountValue > 0) {
                        if (expenseId == null) {
                            val request = AddExpenseRequest(
                                user_id = userId,
                                category_id = category.id,
                                amount = amount
                            )
                            viewModel.addExpense(request) {
                                onBackClicked()
                            }
                        } else {
                            val request = UpdateExpenseRequest(
                                amount = amount,
                                category_id = category.id
                            )
                            viewModel.updateExpense(expenseId, request, userId) {
                                onBackClicked()
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isLoading
            ) {
                Text(if (expenseId == null) "Add Expense" else "Update Expense")
            }

            if (isLoading) {
                CircularProgressIndicator()
            }

            errorMessage?.let {
                Text(it, color = Color.Red)
            }
        }
    }
}