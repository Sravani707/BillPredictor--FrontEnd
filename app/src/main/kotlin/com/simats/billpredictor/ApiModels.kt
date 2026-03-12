package com.simats.billpredictor

import com.google.gson.annotations.SerializedName

data class EventItemResponse(
    val id: Int,
    val event_name: String,
    val estimated_cost: String,
    val event_date: String,
    val user_id: Int
)

data class EventResponse(
    val id: Int,
    val event_name: String,
    val estimated_cost: String,
    val event_date: String,
    val user_id: Int
)

data class BasicResponse(val message: String)

data class GenericResponse(val message: String)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AddExpenseRequest(val user_id: Int, val category_id: Int, val amount: String)

data class UpdateExpenseRequest(val amount: String, val category_id: Int)

data class AddEventRequest(
    val user_id: Int,
    val event_name: String,
    val event_date: String,
    val estimated_cost: Double
)

data class ProfileResponse(
    val name: String,
    val email: String
)

data class MonthlySummaryResponse(
    val categories: List<CategorySummary>,
    val daily_avg: Double,
    val total: Double
)

data class CategorySummary(
    val amount: Double,
    val name: String
)
