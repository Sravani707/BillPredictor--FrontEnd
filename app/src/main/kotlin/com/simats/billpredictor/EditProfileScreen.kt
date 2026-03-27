package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.network.ExpenseApi
import com.simats.billpredictor.network.model.UpdateProfileRequest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userId: Int,
    currentName: String,
    api: ExpenseApi,
    onBackClicked: () -> Unit,
    onProfileUpdated: (String) -> Unit
) {
    // Using remember(currentName) ensures the local state updates if the parent name changes
    var name by remember(currentName) { mutableStateOf(currentName) }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    LaunchedEffect(userId) {
        isLoading = true
        try {
            val response = api.getProfile(userId)
            // Only update if the user hasn't started typing yet
            if (name == currentName) {
                name = response.name
            }
        } catch (e: Exception) {
            // Error handling
        } finally {
            isLoading = false
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClicked) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { newValue -> 
                    // Filter input instead of blocking it entirely.
                    // This allows for a much smoother typing experience.
                    name = newValue.filter { it.isLetter() || it.isWhitespace() }
                },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (name.trim().isEmpty()) {
                        message = "Name cannot be empty"
                        return@Button
                    }
                    
                    isLoading = true
                    scope.launch {
                        try {
                            val response = api.updateProfile(UpdateProfileRequest(userId, name.trim()))
                            if (response.message.contains("Success", ignoreCase = true) || 
                                response.message.contains("Updated", ignoreCase = true)) {
                                onProfileUpdated(name.trim())
                                message = "Profile updated successfully ✅"
                            } else {
                                message = response.message
                            }
                        } catch (e: Exception) {
                            message = "Update failed: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(message, color = if (message.contains("successfully")) Color(0xFF2E7D32) else Color.Red)
            }
        }
    }
}
