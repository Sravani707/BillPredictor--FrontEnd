package com.simats.billpredictor.utils

import java.text.SimpleDateFormat
import java.util.*

object EventPlannerUtils {

    fun monthsLeft(eventDate: String?): Int {

        if (eventDate.isNullOrEmpty()) return 1

        return try {

            // ✅ FORMAT MATCHES BACKEND RESPONSE
            val format = SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z",
                Locale.ENGLISH
            )

            val event = format.parse(eventDate)!!

            val today = Calendar.getInstance()
            val eventCal = Calendar.getInstance().apply { time = event }

            // Ignore day differences
            today.set(Calendar.DAY_OF_MONTH, 1)
            eventCal.set(Calendar.DAY_OF_MONTH, 1)

            val months =
                (eventCal.get(Calendar.YEAR) - today.get(Calendar.YEAR)) * 12 +
                (eventCal.get(Calendar.MONTH) - today.get(Calendar.MONTH))

            if (months <= 0) 1 else months

        } catch (e: Exception) {
            e.printStackTrace()
            1
        }
    }

    /**
     * Calculates monthly saving needed based on total cost and months remaining.
     */
    fun monthlySaving(totalCost: Double, monthsLeft: Int): Double {
        return if (monthsLeft > 0) totalCost / monthsLeft else totalCost
    }
}
