// data/remote/dto/RequestCreateDto.kt

package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RequestCreateDto(
    @SerializedName(value = "title", alternate = ["Title"])
    val title: String,

    @SerializedName(value = "description", alternate = ["Description"])
    val description: String?,

    @SerializedName(value = "startAddress", alternate = ["start_address", "StartAddress"])
    val startAddress: String,

    @SerializedName(value = "destinationAddress", alternate = ["destination_address", "DestinationAddress"])
    val destinationAddress: String,

    @SerializedName(value = "startLatitude", alternate = ["start_latitude", "StartLatitude"])
    val startLatitude: Double,

    @SerializedName(value = "startLongitude", alternate = ["start_longitude", "StartLongitude"])
    val startLongitude: Double,

    @SerializedName(value = "destinationLatitude", alternate = ["destination_latitude", "DestinationLatitude"])
    val destinationLatitude: Double,

    @SerializedName(value = "destinationLongitude", alternate = ["destination_longitude", "DestinationLongitude"])
    val destinationLongitude: Double,

    @SerializedName(value = "estimatedMinutes", alternate = ["estimated_minutes", "EstimatedMinutes"])
    val estimatedMinutes: Int,

    @SerializedName(value = "scheduledAt", alternate = ["scheduled_at", "ScheduledAt"])
    val scheduledAt: String,

    @SerializedName(value = "route", alternate = ["Route"])
    val route: RouteCreateDto
)

data class RouteCreateDto(
    @SerializedName("coord_type") val coordType: String = "WGS84",
    @SerializedName("total_distance_meters") val totalDistanceMeters: Int,
    @SerializedName("total_duration_seconds") val totalDurationSeconds: Int,
    @SerializedName("estimated_price") val estimatedPrice: Int,
    @SerializedName("points") val points: List<PointDto>
)

data class PointDto(
    val lat: Double,
    val lng: Double
)

// 응답 DTO는 기존과 동일
data class RequestCreateResponse(
    @SerializedName("request_id") val requestId: Long,
    @SerializedName("status") val status: String
)