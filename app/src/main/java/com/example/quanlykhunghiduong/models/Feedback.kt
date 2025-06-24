package com.example.quanlykhunghiduong.models

data class Feedback(
    val feedback_id: Int,
    val user_id: Int,
    val content: String,
    val created_at: String
) 