package com.simats.billpredictor.utils

import com.simats.billpredictor.model.EventSavingPlan
import java.text.SimpleDateFormat
import java.util.*

object EventPlannerCalculator {

    private val monthFormat =
        SimpleDateFormat("MMMM yyyy", Locale.ENGLISH)

    // calculate months difference
    private fun monthsBetween(start: Calendar, end: Calendar): Int {
        return (end.get(Calendar.YEAR) - start.get(Calendar.YEAR)) * 12 +
                (end.get(Calendar.MONTH) - start.get(Calendar.MONTH))
    }

    fun calculateNewMonthlySaving(
        eventCost: Double,
        eventDate: String,
        plans: List<EventSavingPlan>
    ): Double {

        val sdf = SimpleDateFormat(
            "EEE, dd MMM yyyy HH:mm:ss z",
            Locale.ENGLISH
        )

        val event = sdf.parse(eventDate) ?: return 0.0

        val today = Calendar.getInstance()
        val eventCal = Calendar.getInstance().apply { time = event }

        val monthsLeft = monthsBetween(today, eventCal).coerceAtLeast(1)

        // total already saved
        // Note: EventSavingPlan model may need update to include 'saved' and 'requiredAmount'
        // or this should use EventSavingsItem model instead.
        val savedAmount = plans
            .sumOf { 0.0 } // Placeholder until model is matched

        val remainingAmount = eventCost - savedAmount

        return (remainingAmount / monthsLeft)
            .coerceAtLeast(0.0)
    }
}
