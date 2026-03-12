package com.simats.billpredictor.network

// ---------- REQUEST ----------
data class PredictRequest(
    val user_id: Int
)

// ---------- RESPONSE ----------
data class PredictResponse(
    val expense_prediction: Double,
    val event_added_amount: Double,
    val ai_predicted_next_month_expense: Double,
    val events_count: Int,
    val based_on_months: List<String>
)