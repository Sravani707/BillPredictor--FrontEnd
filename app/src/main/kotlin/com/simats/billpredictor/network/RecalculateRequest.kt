package com.simats.billpredictor.network

data class RecalculateRequest(
    val event_id: Int,
    val new_amount: Double
)
