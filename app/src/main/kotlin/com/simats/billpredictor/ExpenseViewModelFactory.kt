package com.simats.billpredictor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.simats.billpredictor.network.ExpenseApi

class ExpenseViewModelFactory(
    private val apiService: ExpenseApi
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExpenseViewModel(apiService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}