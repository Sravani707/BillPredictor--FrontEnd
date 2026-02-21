package com.simats.billpredictor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun PredictionScreen(
    onBackClicked: () -> Unit,
    onAddEventClicked: () -> Unit,
    onTrendingUpClicked: () -> Unit,
    onTrendingDownClicked: () -> Unit,
    onMarchForecastClicked: () -> Unit,
    onCalculationClicked: () -> Unit,
    onAddClicked: () -> Unit,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {

    var predictionResult by remember { mutableStateOf("No prediction yet") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "AI Expense Prediction",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                isLoading = true

                ApiClient.instance.predictNextMonth()
                    .enqueue(object : Callback<Map<String, Any>> {

                        override fun onResponse(
                            call: Call<Map<String, Any>>,
                            response: Response<Map<String, Any>>
                        ) {
                            isLoading = false

                            if (response.isSuccessful) {
                                predictionResult =
                                    response.body()?.get("prediction")?.toString()
                                        ?: "No data received"
                            } else {
                                predictionResult = "Server error: ${response.code()}"
                            }
                        }

                        override fun onFailure(call: Call<Map<String, Any>>, t: Throwable) {
                            isLoading = false
                            predictionResult = "Network Error: ${t.message}"
                        }
                    })
            }
        ) {
            Text("Get Prediction")
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = predictionResult,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onBackClicked) {
            Text("Back")
        }
    }
}
