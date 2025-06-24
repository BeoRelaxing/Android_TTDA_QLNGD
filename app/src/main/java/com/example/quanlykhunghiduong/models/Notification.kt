package com.example.quanlykhunghiduong.models

data class Notification(
    val notification_id: Int,
    val user_id: Int,
    val title: String,
    val message: String,
    val sent_at: String,
    val is_read: Boolean
) 