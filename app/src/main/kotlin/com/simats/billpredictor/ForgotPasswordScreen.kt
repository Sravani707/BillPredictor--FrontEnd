package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.simats.billpredictor.network.ForgotPasswordRequest
import com.simats.billpredictor.network.RetrofitClient
import kotlinx.coroutines.launch

@Composable
fun ForgotPasswordScreen(navController: NavController) {

    var email by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {

        Text("Forgot Password", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = {
                if (email.isEmpty()) {
                    errorMessage = "Please enter your email"
                    return@Button
                }

                isLoading = true
                errorMessage = ""

                scope.launch {
                    try {
                        val response = RetrofitClient.instance.forgotPassword(
                            ForgotPasswordRequest(email)
                        )
                        if (response.message.contains("success", ignoreCase = true)) {
                            showSuccessDialog = true
                        } else {
                            errorMessage = response.message
                        }
                    } catch (e: Exception) {
                        errorMessage = "Network Error: ${e.message}"
                    } finally {
                        isLoading = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
            } else {
                Text("Send OTP")
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("OTP Sent") },
            text = { Text("An OTP has been sent to $email. Please check your inbox.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    navController.navigate(Screen.Otp.createRoute(email))
                }) {
                    Text("OK")
                }
            }
        )
    }
}
