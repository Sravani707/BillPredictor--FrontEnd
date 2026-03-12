package com.simats.billpredictor

data class TrendItem(
    val category: String,
    val previousAmount: Double,
    val currentAmount: Double,
    val percentageChange: Double
) {
    val difference: Double get() = currentAmount - previousAmount
    val percentChange: Int get() = kotlin.math.abs(percentageChange.toInt())
    val isUp: Boolean get() = percentageChange > 0
}