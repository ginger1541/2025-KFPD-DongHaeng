package com.kfpd_donghaeng_fe.data

data class Request(
    val id: Long,
    val date: String,
    val departure: String,
    val arrival: String,
    val departureTime: String,
    val arrivalTime: String,
    val distance: String,
    val duration: String,      // ✅ [추가] 소요 시간 (예: "18분")
    val pricePoints: Int
)