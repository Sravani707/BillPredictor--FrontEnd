package com.simats.billpredictor.model

data class EventPlannerModel(
    val id: Int,
    val event_name: String,
    val event_date: String,
    val estimated_cost: Double,
    val total_saved: Double
)
