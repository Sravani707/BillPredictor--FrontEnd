package com.simats.billpredictor

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST

interface ExpenseApi {

    @POST("add-expense")
    fun addExpense(): Call<Map<String, String>>

    @GET("predict")
    fun predictNextMonth(): Call<Map<String, Any>>
}
