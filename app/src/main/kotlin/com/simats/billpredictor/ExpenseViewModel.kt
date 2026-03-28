package com.simats.billpredictor

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.billpredictor.model.*
import com.simats.billpredictor.network.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class ExpenseViewModel(private val apiService: ExpenseApi) : ViewModel() {

    private val _expenses = MutableStateFlow<List<ExpenseItem>>(emptyList())
    val expenses: StateFlow<List<ExpenseItem>> = _expenses

    private val _categories = MutableStateFlow<List<CategorySummary>>(emptyList())
    val categories: StateFlow<List<CategorySummary>> = _categories

    private val _availableCategories = MutableStateFlow<List<CategoryResponse>>(emptyList())
    val availableCategories: StateFlow<List<CategoryResponse>> = _availableCategories

    private val _events = MutableStateFlow<List<EventResponse>>(emptyList())
    val events: StateFlow<List<EventResponse>> = _events

    private val _eventPlanner = MutableStateFlow<List<EventPlannerModel>>(emptyList())
    val eventPlanner: StateFlow<List<EventPlannerModel>> = _eventPlanner

    private val _eventSavingsPlan = MutableStateFlow<List<EventSavingsItem>>(emptyList())
    val eventSavingsPlan: StateFlow<List<EventSavingsItem>> = _eventSavingsPlan

    private val _savedStatusMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val savedStatusMap: StateFlow<Map<Int, Boolean>> = _savedStatusMap

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _trendingUp = MutableStateFlow<List<TrendItem>>(emptyList())
    val trendingUp: StateFlow<List<TrendItem>> = _trendingUp

    private val _trendingDown = MutableStateFlow<List<TrendItem>>(emptyList())
    val trendingDown: StateFlow<List<TrendItem>> = _trendingDown

    private val rfcFormatter = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

    private var lastUserIdLoaded = -1
    private var lastEventIdLoaded = -1

    // ---------- Expenses ----------
    fun loadExpenses(userId: Int) {
        if (userId <= 0) return
        if (userId == lastUserIdLoaded && _expenses.value.isNotEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = apiService.getHistory(userId)

                _expenses.value = response.expenses.map { expense ->
                    ExpenseItem(
                        id = expense.expense_id.toString(),
                        realId = expense.expense_id,
                        title = expense.category_name ?: "Uncategorized",
                        amount = expense.amount ?: "0.0",
                        date = expense.expense_date ?: "",
                        categoryName = expense.category_name ?: "Uncategorized",
                        categoryId = 0
                    )
                }

                _categories.value = response.expenses.map {
                    CategorySummary(
                        name = it.category_name ?: "Uncategorized",
                        amount = it.amount?.toDoubleOrNull() ?: 0.0
                    )
                }

                lastUserIdLoaded = userId
                fetchTrends(userId)
                _errorMessage.value = null

            } catch (e: Exception) {
                Log.e("VM_LOAD", "Error loading expenses", e)
                _expenses.value = emptyList()
                _categories.value = emptyList()
                _errorMessage.value = "Failed to fetch history"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTrends(userId: Int) {
        viewModelScope.launch {
            try {
                val result = apiService.getTrends(userId)
                _trendingUp.value = result.trending_up
                _trendingDown.value = result.trending_down
            } catch (e: Exception) {
                Log.e("TREND_ERROR", e.message.toString())
            }
        }
    }

    fun fetchExpenses(userId: Int) = loadExpenses(userId)

    // ---------- Categories ----------
    fun fetchCategories() {
        if (_availableCategories.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = apiService.getCategories()
                Log.d("VM_CAT", "Categories loaded: ${result.size}")
                _availableCategories.value = result
                _errorMessage.value = null
            } catch (e: Exception) {
                Log.e("VM_CAT", "Error loading categories", e)
                _errorMessage.value = "Failed to fetch categories"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------- Events ----------
    fun fetchEvents(userId: Int) {
        if (userId == lastUserIdLoaded && _events.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = apiService.getEvents(userId)
                _events.value = result
            } catch (e: Exception) {
                Log.e("EVENT_FETCH_ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchEventPlanner(userId: Int) {
        if (userId == lastUserIdLoaded && _eventPlanner.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                val result = apiService.getEventPlanner(userId)
                _eventPlanner.value = result
            } catch (e: Exception) {
                Log.e("EVENT_PLANNER_ERROR", e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------- Event Savings ----------
    fun fetchEventSavings(userId: Int, eventId: Int) {
        if (userId == lastUserIdLoaded && eventId == lastEventIdLoaded && _eventSavingsPlan.value.isNotEmpty()) return

        viewModelScope.launch {
            try {
                _isLoading.value = true
                Log.d("API_DEBUG", "Calling API user=$userId event=$eventId")
                val response = apiService.getEventSavings(userId, eventId)
                Log.d("API_DEBUG", "RAW RESPONSE = $response")
                Log.d("API_DEBUG", "PLAN SIZE = ${response.savings_plan.size}")
                _eventSavingsPlan.value = response.savings_plan
                // Initialize map with current saved values
                _savedStatusMap.value = response.savings_plan.associate { it.id to it.saved }
                lastEventIdLoaded = eventId
            } catch (e: Exception) {
                Log.e("API_DEBUG", "ERROR", e)
                _eventSavingsPlan.value = emptyList()
                _savedStatusMap.value = emptyMap()
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Update the saved status of an Event Savings item
     * - Optimistically updates UI via _savedStatusMap
     * - Calls backend asynchronously
     */
    fun updateSavingStatus(savingId: Int, saved: Boolean) {
        Log.d("UPDATE_SAVING", "Toggling id=$savingId to $saved")
        
        // Optimistic update
        _savedStatusMap.value = _savedStatusMap.value.toMutableMap().apply { put(savingId, saved) }
        Log.d("UPDATE_SAVING", "Map after optimistic update: ${_savedStatusMap.value}")

        // Update backend asynchronously
        viewModelScope.launch {
            try {
                apiService.updateSavingStatus(UpdateSavingStatusRequest(savingId, saved))
                Log.d("UPDATE_SAVING", "Backend updated successfully for id=$savingId")
            } catch (e: Exception) {
                Log.e("UPDATE_SAVING", "Backend failed, reverting", e)
                // Revert on error
                _savedStatusMap.value = _savedStatusMap.value.toMutableMap().apply { put(savingId, !saved) }
            }
        }
    }

    fun recalculateEventSaving(eventId: Int, newAmount: Double) {
        viewModelScope.launch {
            try {
                apiService.recalculateEventSavings(RecalculateRequest(eventId, newAmount))
                lastEventIdLoaded = -1 // Force reload
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ---------- Expense CRUD ----------
    fun addExpense(request: AddExpenseRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.addExpense(request)
                lastUserIdLoaded = -1 // Force reload
                loadExpenses(request.user_id)
                fetchTrends(request.user_id)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                Log.e("VM_ADD", "Error adding expense", e)
                _errorMessage.value = "Failed to add expense"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateExpense(realId: Int, request: UpdateExpenseRequest, userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.updateExpense(realId, request)
                lastUserIdLoaded = -1 // Force reload
                loadExpenses(userId)
                fetchTrends(userId)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                Log.e("VM_UPDATE", "Error updating expense", e)
                _errorMessage.value = "Update failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteExpense(expenseId: Int, userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.deleteExpense(expenseId)
                lastUserIdLoaded = -1 // Force reload
                loadExpenses(userId)
                fetchTrends(userId)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                Log.e("VM_DELETE", "Error deleting expense", e)
                _errorMessage.value = "Delete failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------- Event CRUD ----------
    fun addEvent(request: AddEventRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.addEvent(request)
                _events.value = emptyList() // Force reload
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                Log.e("VM_ADD_EVENT", "Error adding event", e)
                _errorMessage.value = "Failed to add event"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteEvent(eventId: Int, userId: Int, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                apiService.deleteEvent(eventId)
                _events.value = emptyList() // Force reload
                fetchEvents(userId)
                _errorMessage.value = null
                onSuccess()
            } catch (e: Exception) {
                Log.e("VM_DELETE_EVENT", "Error deleting event", e)
                _errorMessage.value = "Delete failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // ---------- Utility Functions ----------
    fun getMonthlyExpenses(expenses: List<ExpenseItem>): List<MonthlyExpense> {
        val monthNameFormatter = SimpleDateFormat("MMM", Locale.ENGLISH)
        val months = listOf(
            "Jan","Feb","Mar","Apr","May","Jun",
            "Jul","Aug","Sep","Oct","Nov","Dec"
        )

        return expenses.mapNotNull { expense ->
            try {
                val date = rfcFormatter.parse(expense.date)
                if (date != null) {
                    val monthName = monthNameFormatter.format(date)
                    monthName to (expense.amount.toDoubleOrNull() ?: 0.0)
                } else null
            } catch (e: Exception) { null }
        }
            .groupBy({ it.first }, { it.second })
            .map { (month, amounts) -> MonthlyExpense(month = month, amount = amounts.sum()) }
            .sortedBy { months.indexOf(it.month) }
    }

    fun calculateTrends(): Pair<List<TrendItem>, List<TrendItem>> {
        if (_expenses.value.isEmpty()) return Pair(emptyList(), emptyList())

        val grouped = _expenses.value.groupBy { it.categoryName }

        val trends = grouped.map { (category, list) ->
            val sorted = list.sortedBy { it.date }
            val mid = sorted.size / 2

            val previous = sorted.take(mid).sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
            val current = sorted.drop(mid).sumOf { it.amount.toDoubleOrNull() ?: 0.0 }

            val change = if (previous == 0.0 && current > 0) 100.0
            else if (previous == 0.0) 0.0
            else ((current - previous) / previous) * 100

            TrendItem(category, previous, current, change)
        }

        val trendingUp = trends.filter { it.percentageChange > 0 }.sortedByDescending { it.percentageChange }
        val trendingDown = trends.filter { it.percentageChange < 0 }.sortedBy { it.percentageChange }

        return Pair(trendingUp, trendingDown)
    }
}
