// data/remote/dto/RequestCreateDto.kt

package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class RequestCreateDto(
    @SerializedName("title") val title: String, // 💡 "출발지 -> 도착지" 형태로 자동 생성
    @SerializedName("description") val description: String?, // 💡 사용자가 입력한 요청사항

    @SerializedName("startAddress") val startAddress: String,
    @SerializedName("destinationAddress") val destinationAddress: String,

    @SerializedName("startLatitude") val startLatitude: Double,
    @SerializedName("startLongitude") val startLongitude: Double,

    @SerializedName("destinationLatitude") val destinationLatitude: Double,
    @SerializedName("destinationLongitude") val destinationLongitude: Double,

    @SerializedName("estimatedMinutes") val estimatedMinutes: Int,
    @SerializedName("scheduledAt") val scheduledAt: String, // "2025-12-01T15:00:00+09:00"

    @SerializedName("route") val route: RouteCreateDto
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