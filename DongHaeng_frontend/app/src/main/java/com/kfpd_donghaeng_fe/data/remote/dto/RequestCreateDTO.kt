package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RequestCreateDto(
    @SerializedName("start_address") val startAddress: String,
    @SerializedName("destination_address") val destinationAddress: String,
    @SerializedName("start_latitude") val startLatitude: Double,
    @SerializedName("start_longitude") val startLongitude: Double,
    @SerializedName("destination_latitude") val destinationLatitude: Double,
    @SerializedName("destination_longitude") val destinationLongitude: Double,
    @SerializedName("scheduled_at") val scheduledAt: String, // "2025-11-24T15:00:00" (ISO 8601)
    @SerializedName("estimated_minutes") val estimatedMinutes: Int,
    @SerializedName("service_type") val serviceType: String, // "SIMPLE_MOVE" etc
    @SerializedName("description") val description: String?
)

data class RequestCreateResponse(
    @SerializedName("request_id") val requestId: Long,
    @SerializedName("status") val status: String
)