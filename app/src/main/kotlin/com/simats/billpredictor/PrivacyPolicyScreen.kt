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
fun PrivacyPolicyScreen(onBackClicked: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy") },
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
                .padding(24.dp)
        ) {
            PolicySection(
                title = "1. Information We Collect",
                content = "We collect information you provide directly to us when you create an account, log expenses, and set up special events. This includes your name, email, and transaction details."
            )

            PolicySection(
                title = "2. How We Use Your Information",
                content = "Your data is primarily used to provide accurate bill predictions and spending insights. We analyze patterns in your transaction history to help you manage your finances better."
            )

            PolicySection(
                title = "3. Data Storage",
                content = "All your data is stored securely on our encrypted servers. We implement industry-standard security measures to protect your personal and financial information."
            )

            PolicySection(
                title = "4. Data Sharing",
                content = "We do not sell or share your personal data with third-party advertisers. Your information is only used within the app to improve its core functionalities."
            )

            PolicySection(
                title = "5. Your Rights",
                content = "You have the right to access, edit, or delete your account and data at any time through the profile settings."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            lineHeight = 22.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
