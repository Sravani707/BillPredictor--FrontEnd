package com.simats.billpredictor

data class HistoryResponse(
    val expenses: List<ExpenseItemResponse>
)

data class ExpenseItemResponse(
    val expense_id: Int,
    val category_name: String?,
    val amount: String?,
    val expense_date: String?
)