package com.example.quanlykhunghiduong.CustomerActivity

data class CustomerRoom(
    val room_id: Int,
    val resort_id: Int,
    val room_number: String,
    val room_type: String,
    val price_per_night: Double,
    val status: String,
    val capacity: Int
) 