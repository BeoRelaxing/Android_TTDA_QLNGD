package com.example.quanlykhunghiduong.models

data class Booking(
    val booking_id: Int,
    val user_id: Int,
    val room_id: Int,
    val check_in_date: String,
    val check_out_date: String,
    val total_price: Double,
    val status: String,
    val created_at: String
) 