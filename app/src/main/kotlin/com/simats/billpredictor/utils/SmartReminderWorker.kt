package com.simats.billpredictor.utils

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.simats.billpredictor.network.RetrofitClient
import java.text.SimpleDateFormat
import java.util.*

class SmartReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val sharedPrefs = applicationContext.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userId = sharedPrefs.getInt("user_id", -1)

        if (userId == -1) return Result.success()

        val api = RetrofitClient.instance

        // 1. Check for missing expenses today
        val shouldRemindExpenses = checkExpenseMissing(api, userId)
        if (shouldRemindExpenses) {
            NotificationHelper.showNotification(
                applicationContext,
                "Track Today's Expenses",
                "You haven't added expenses today. Stay on top of your budget!"
            )
        }

        // 2. Check for upcoming event saving goals
        checkEventSavingReminders(api, userId)

        return Result.success()
    }

    private suspend fun checkExpenseMissing(api: com.simats.billpredictor.network.ExpenseApi, userId: Int): Boolean {
        return try {
            val response = api.getHistory(userId)
            val today = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
            
            // Fix: Use expense_date instead of date to match model
            !response.expenses.any { it.expense_date?.contains(today) == true }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private suspend fun checkEventSavingReminders(api: com.simats.billpredictor.network.ExpenseApi, userId: Int) {
        try {
            val events = api.getEvents(userId)
            var totalSavingNeeded = 0.0

            events.forEach { event ->
                val cost = event.estimated_cost.toDoubleOrNull() ?: 0.0
                // Correct usage of event_date property
                val monthsLeft = EventPlannerUtils.monthsLeft(event.event_date)
                totalSavingNeeded += EventPlannerUtils.monthlySaving(cost, monthsLeft)
            }

            if (totalSavingNeeded > 0) {
                NotificationHelper.showNotification(
                    applicationContext,
                    "Event Saving Reminder",
                    "Remember to set aside ₹${totalSavingNeeded.toInt()} this month for your upcoming events."
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
