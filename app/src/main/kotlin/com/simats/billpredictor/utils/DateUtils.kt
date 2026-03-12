package com.simats.billpredictor.utils

import java.text.SimpleDateFormat
import java.util.Locale

fun getMonthYear(date: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        if (parsedDate != null) {
            outputFormat.format(parsedDate)
        } else {
            date
        }
    } catch (e: Exception) {
        date
    }
}