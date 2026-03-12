package com.simats.billpredictor.model

data class EventSavingPlan(
    val eventId: Int,
    val eventName: String,
    val monthlySaving: Double,
    val monthsLeft: Int
)
