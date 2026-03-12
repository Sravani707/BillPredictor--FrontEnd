package com.simats.billpredictor.network

data class LoginResponse(
    val message: String,
    val name: String?,
    val user_id: Int?
)