package com.kfpd_donghaeng_fe.data

data class Request(
    val id: Long,
    val date: String,
    val departure: String,
    val arrival: String,
    val departureTime: String,
    val arrivalTime: String,
    val distance: String,
    val duration: String,
    val pricePoints: Int,
    val startLatitude: Double,
    val startLongitude: Double,
    val endLatitude: Double,
    val endLongitude: Double
)