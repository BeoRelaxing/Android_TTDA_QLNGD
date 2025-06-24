package com.example.quanlykhunghiduong.models

data class Resort(
    val resort_id: Int,
    val name: String,
    val location: String,
    val type: String,
    val description: String,
    val price_range: String,
    val amenities: String,
    val created_at: String
) 