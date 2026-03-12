package com.simats.billpredictor

data class TrendResponse(
    val trending_up: List<TrendItem>,
    val trending_down: List<TrendItem>
)
