package com.simats.billpredictor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.billpredictor.ui.theme.BillpredictorTheme

@Composable
fun EventSavedScreen(
    onAddAnotherEvent: () -> Unit,
    onViewAllEvents: () -> Unit,
    onSeePrediction: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5F5F5)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = "Success", tint = Color(0xFF34A853), modifier = Modifier.size(60.dp))
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text("Event Saved!", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Your event has been added successfully.", textAlign = TextAlign.Center, color = Color.Gray)
            Text("The AI prediction for that month has been updated.", textAlign = TextAlign.Center, color = Color(0xFFFBC02D))
            Spacer(modifier = Modifier.height(48.dp))
            Button(
                onClick = onAddAnotherEvent,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Add Another Event")
            }
            TextButton(onClick = onViewAllEvents) {
                Text("View All Events", color = Color.Gray)
            }
            TextButton(onClick = onSeePrediction) {
                Text("See Updated Prediction", color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventSavedScreenPreview() {
    BillpredictorTheme {
        EventSavedScreen(onAddAnotherEvent = {}, onViewAllEvents = {}, onSeePrediction = {})
    }
}
