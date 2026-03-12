package com.simats.billpredictor

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.simats.billpredictor.network.RetrofitClient
import com.simats.billpredictor.network.LoginRequest

@Composable
fun LoginScreen(
    onBackClicked: () -> Unit,
    onRegisterClicked: () -> Unit,
    onForgotPasswordClicked: () -> Unit,
    onLoginSuccess: (String, Int) -> Unit
) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Logos Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 40.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Top Left Logo
                    Image(
                        painter = painterResource(id = R.drawable.leftlogosimats),
                        contentDescription = "Simats Left Logo",
                        modifier = Modifier
                            .height(80.dp)
                            .weight(1f),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart
                    )

                    // Top Right Logo
                    Image(
                        painter = painterResource(id = R.drawable.rightlogosimats),
                        contentDescription = "Simats Right Logo",
                        modifier = Modifier
                            .height(80.dp)
                            .weight(1f),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterEnd
                    )
                }

                // Welcome Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text("Welcome Back", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Sign in to continue tracking", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Input Section
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (passwordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (passwordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                TextButton(
                    onClick = onForgotPasswordClicked,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Forgot Password?", color = MaterialTheme.colorScheme.primary)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Login Button
                Button(
                    onClick = {
                        if (email.isEmpty() || password.isEmpty()) {
                            errorMessage = "Please enter email and password"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = ""

                        scope.launch {
                            try {
                                val body = RetrofitClient.instance.login(
                                    LoginRequest(email = email, password = password)
                                )

                                if (body.message.contains("success", ignoreCase = true)) {
                                    val finalName = body.name ?: "User"
                                    val finalId = body.user_id ?: -1
                                    onLoginSuccess(finalName, finalId)
                                } else {
                                    errorMessage = body.message
                                }

                            } catch (e: Exception) {
                                Log.e("LOGIN_FLOW", "Login failed", e)
                                errorMessage = "Network Error: ${e.message}"
                            } finally {
                                isLoading = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Login", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Footer
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Don't have an account?", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onRegisterClicked) {
                        Text("Register", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

                // Add spacer to ensure content doesn't hide behind footer on small screens when scrolling
                Spacer(modifier = Modifier.height(60.dp))
            }

            // Copyright Footer - Pinned to the bottom of the screen
            Text(
                text = "2026 © Powered by SIMATS engineering",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            )
        }
    }
}
