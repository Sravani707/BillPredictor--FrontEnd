package com.simats.billpredictor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelpAndFaqScreen(onBackClicked: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & FAQ") },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                "Frequently Asked Questions",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))

            FAQItem(
                question = "What is ExpenseAI?",
                answer = "ExpenseAI is a smart expense management app that helps you track your daily spending and predicts your future bills using AI based on your history and upcoming events."
            )

            FAQItem(
                question = "How do I add a new expense?",
                answer = "On the Home screen, tap the '+' button in the Quick Actions section. Enter the amount and select a category to save it."
            )

            FAQItem(
                question = "What are 'Special Events'?",
                answer = "Special Events are upcoming occasions (like festivals, birthdays, or trips) where you expect to spend extra. Adding these helps the AI provide more accurate predictions for those months."
            )

            FAQItem(
                question = "How accurate are the predictions?",
                answer = "Predictions become more accurate as you record more expenses. The AI analyzes your previous months' spending patterns along with any special events you've added."
            )

            FAQItem(
                question = "How do I see my monthly summary?",
                answer = "Tap on the 'Monthly Spending' card on the Home screen to see a detailed breakdown of your expenses by category for the current month."
            )

            FAQItem(
                question = "Can I edit or delete an expense?",
                answer = "Yes, in the 'History' tab or 'Recent Transactions' list, simply tap an expense to edit it, or long-press to see the delete option."
            )

            FAQItem(
                question = "Is my data secure?",
                answer = "Your data is stored securely and is only used to provide you with insights into your spending habits and accurate bill predictions."
            )

            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                "Need more help?",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                "Contact us at sravani.ch2004@gmail.com",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary
            )
        }
    }
}

@Composable
fun FAQItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 20.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = answer,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 20.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(modifier = Modifier.padding(top = 16.dp), thickness = 0.5.dp)
    }
}
