package com.simats.billpredictor.network.model

data class UpdateProfileRequest(
    val user_id: Int,
    val name: String
)
