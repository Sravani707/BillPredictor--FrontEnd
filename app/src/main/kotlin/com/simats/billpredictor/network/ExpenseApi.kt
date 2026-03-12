package com.simats.billpredictor.network

import com.simats.billpredictor.*
import com.simats.billpredictor.model.EventPlannerModel
import com.simats.billpredictor.model.EventSavingsItem
import com.simats.billpredictor.model.EventSavingsResponse
import retrofit2.http.*

interface ExpenseApi {

    // ---------------- AUTH ----------------

    @POST("login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): LoginResponse

    @POST("register")
    suspend fun register(
        @Body request: RegisterRequest
    ): BasicResponse

    @POST("forgot_password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): BasicResponse

    @POST("verify_otp")
    suspend fun verifyOtp(
        @Body request: VerifyOtpRequest
    ): BasicResponse

    @POST("reset_password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): BasicResponse


    // ---------------- EXPENSE ----------------

    @POST("add_expense")
    suspend fun addExpense(
        @Body request: AddExpenseRequest
    ): BasicResponse

    @POST("add_income")
    suspend fun addIncome(
        @Body request: AddIncomeRequest
    ): BasicResponse

    @GET("get_income/{user_id}")
    suspend fun getIncome(
        @Path("user_id") userId: Int
    ): IncomeResponse

    @GET("history/{user_id}")
    suspend fun getHistory(
        @Path("user_id") userId: Int
    ): HistoryResponse

    @GET("categories")
    suspend fun getCategories(): List<CategoryResponse>

    @PUT("update_expense/{expense_id}")
    suspend fun updateExpense(
        @Path("expense_id") expenseId: Int,
        @Body request: UpdateExpenseRequest
    ): BasicResponse

    @DELETE("delete_expense/{expense_id}")
    suspend fun deleteExpense(
        @Path("expense_id") expenseId: Int
    ): BasicResponse


    // ---------------- EVENTS ----------------

    @POST("add_event")
    suspend fun addEvent(
        @Body request: AddEventRequest
    ): BasicResponse

    @GET("events/{user_id}")
    suspend fun getEvents(
        @Path("user_id") userId: Int
    ): List<EventResponse>

    @DELETE("delete_event/{event_id}")
    suspend fun deleteEvent(
        @Path("event_id") eventId: Int
    ): GenericResponse

    @GET("event_savings/{userId}/{eventId}")
    suspend fun getEventSavings(
        @Path("userId") userId: Int,
        @Path("eventId") eventId: Int
    ): EventSavingsResponse

    @POST("recalculate_event_savings")
    suspend fun recalculateEventSavings(
        @Body request: RecalculateRequest
    ): BasicResponse

    @POST("update_saving_status")
    suspend fun updateSavingStatus(
        @Body request: UpdateSavingStatusRequest
    ): BasicResponse

    @GET("event_planner/{user_id}")
    suspend fun getEventPlanner(
        @Path("user_id") userId: Int
    ): List<EventPlannerModel>


    // ---------------- AI ----------------

    @POST("ai_predict")
    suspend fun predict(
        @Body request: PredictRequest
    ): PredictResponse

    @GET("trends/{userId}")
    suspend fun getTrends(
        @Path("userId") userId: Int
    ): TrendResponse
}

data class ForgotPasswordRequest(val email: String)
data class VerifyOtpRequest(val email: String, val otp: String)
data class ResetPasswordRequest(val email: String, val password: String)
data class UpdateSavingStatusRequest(val savingId: Int, val saved: Boolean)
