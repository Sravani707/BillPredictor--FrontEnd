package com.simats.billpredictor

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onLoginClicked: () -> Unit,
    onRegisterSuccess: () -> Unit
) {

    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    fun isEmailValid(email: String): Boolean {
        return email.endsWith("@gmail.com")
    }

    fun isPasswordValid(password: String): Boolean {
        val hasUpperCase = password.any { it.isUpperCase() }
        val hasLowerCase = password.any { it.isLowerCase() }
        val hasSpecialChar = password.any { !it.isLetterOrDigit() }
        return password.length >= 8 && hasUpperCase && hasLowerCase && hasSpecialChar
    }

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
                        .padding(bottom = 32.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Top Left Logo
                    Image(
                        painter = painterResource(id = R.drawable.leftlogosimats),
                        contentDescription = "Simats Left Logo",
                        modifier = Modifier
                            .height(60.dp)
                            .weight(1f),
                        contentScale = ContentScale.Fit,
                        alignment = Alignment.CenterStart
                    )

                    // Top Right Logo
                    Image(
                        painter = painterResource(id = R.drawable.rightlogosimats),
                        contentDescription = "Simats Right Logo",
                        modifier = Modifier
                            .height(60.dp)
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
                    Text("Join AIMint", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Text("Start your financial journey today", fontSize = 16.sp, color = MaterialTheme.colorScheme.secondary)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Input Section
                OutlinedTextField(
                    value = fullName,
                    onValueChange = {
                        // Allow only letters and spaces
                        if (it.all { char -> char.isLetter() || char.isWhitespace() }) {
                            fullName = it
                        }
                    },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

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

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    singleLine = true,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        val image = if (confirmPasswordVisible)
                            Icons.Filled.Visibility
                        else Icons.Filled.VisibilityOff

                        val description = if (confirmPasswordVisible) "Hide password" else "Show password"

                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(imageVector = image, contentDescription = description)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Register Button
                Button(
                    onClick = {
                        if (fullName.isEmpty()) {
                            errorMessage = "Full Name is required"
                            return@Button
                        }
                        if (!isEmailValid(email)) {
                            errorMessage = "Email must be a valid @gmail.com address"
                            return@Button
                        }
                        if (!isPasswordValid(password)) {
                            errorMessage = "Password must be at least 8 chars and contain Upper, Lower, and Special characters"
                            return@Button
                        }
                        if (password != confirmPassword) {
                            errorMessage = "Passwords do not match"
                            return@Button
                        }

                        isLoading = true
                        errorMessage = ""

                        scope.launch {
                            try {
                                val response = RetrofitClient.instance.register(
                                    RegisterRequest(fullName, email, password)
                                )
                                if (response.message.contains("Success", ignoreCase = true)) {
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
                        Text("Register", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                    Text("Already have an account?", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onLoginClicked) {
                        Text("Login", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }

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

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Success") },
            text = { Text("Registered successfully!") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    onRegisterSuccess()
                }) {
                    Text("OK")
                }
            }
        )
    }
}
