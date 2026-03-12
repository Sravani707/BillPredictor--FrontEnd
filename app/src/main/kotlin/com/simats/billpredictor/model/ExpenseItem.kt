package com.simats.billpredictor.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExpenseItem(
    val id: String,
    val realId: Int,
    val title: String,
    val amount: String,
    val date: String,
    val categoryName: String,
    val categoryId: Int
) : Parcelable
