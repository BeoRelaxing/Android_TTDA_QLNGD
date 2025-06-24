package com.example.quanlykhunghiduong.models

data class User(
    val user_id: Int,
    val name: String,
    val email: String,
    val password_hash: String,
    val phone: String,
    val role: String,
    val created_at: String
) 