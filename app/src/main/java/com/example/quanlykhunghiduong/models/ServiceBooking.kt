package com.example.quanlykhunghiduong.models

data class ServiceBooking(
    val service_booking_id: Int,
    val booking_id: Int,
    val service_id: Int,
    val quantity: Int,
    val total_price: Double
) 