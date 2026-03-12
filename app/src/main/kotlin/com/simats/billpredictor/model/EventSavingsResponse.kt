package com.simats.billpredictor.model

data class EventSavingsItem(
    val id: Int,
    val month_year: String,
    val required_amount: String,
    val saved: Boolean
)

data class EventSavingsResponse(
    val savings_plan: List<EventSavingsItem>
)
