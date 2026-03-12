package com.simats.billpredictor

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.simats.billpredictor.network.RetrofitClient
import com.simats.billpredictor.network.VerifyOtpRequest
import kotlinx.coroutines.launch

@Composable
fun OtpScreen(navController: NavController, email: String) {

    var otp by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(24.dp)
    ) {

        Text("Enter OTP", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(30.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { otp = it },
            label = { Text("OTP") },
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
                if (otp.isEmpty()) {
                    errorMessage = "Please enter the OTP"
                    return@Button
                }

                isLoading = true
                errorMessage = ""

                scope.launch {
                    try {
                        val response = RetrofitClient.instance.verifyOtp(
                            VerifyOtpRequest(email, otp)
                        )
                        
                        // Debugging: Check the exact message from backend in Logcat
                        Log.d("OTP_FLOW", "Email: $email, OTP: $otp, Response: ${response.message}")

                        // Updated check: inclusion of "verified" based on Logcat output
                        if (response.message.contains("success", ignoreCase = true) || 
                            response.message.contains("verified", ignoreCase = true)) {

                            val route = Screen.ResetPassword.createRoute(email)
                            Log.d("OTP_FLOW", "Navigating to: $route")
                            navController.navigate(route)
                        } else {
                            errorMessage = response.message
                        }
                    } catch (e: Exception) {
                        Log.e("OTP_FLOW", "Error during verification", e)
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
                Text("Verify OTP")
            }
        }
    }
}
