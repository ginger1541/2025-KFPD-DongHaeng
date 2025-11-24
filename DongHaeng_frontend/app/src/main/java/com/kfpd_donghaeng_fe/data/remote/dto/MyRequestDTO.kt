package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MyRequestResponseData(
    @SerializedName("requests") val requests: List<MyRequestItemDto>,
    @SerializedName("count") val count: Int
)

data class MyRequestItemDto(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("startAddress") val startAddress: String,
    @SerializedName("destinationAddress") val destinationAddress: String,
    @SerializedName("estimatedMinutes") val estimatedMinutes: Int,
    @SerializedName("scheduledAt") val scheduledAt: String,
    @SerializedName("status") val status: String,
    @SerializedName("latitude") val latitude: Double?,   // 출발지 위도
    @SerializedName("longitude") val longitude: Double?, // 출발지 경도
    @SerializedName("route") val route: MyRequestRouteDto?,
    @SerializedName("match") val match: MyRequestMatchDto?
)

data class MyRequestRouteDto(
    @SerializedName("total_distance_meters") val totalDistanceMeters: Int?,

    @SerializedName("points") val points: List<MyRequestPointDto>?
)

// ✅ [추가] 좌표 하나를 담을 클래스
data class MyRequestPointDto(
    @SerializedName("lat") val lat: Double,
    @SerializedName("lng") val lng: Double
)

data class MyRequestMatchDto(
    @SerializedName("matchId") val matchId: Long,
    @SerializedName("requestId") val requestId: Long,
    @SerializedName("requesterId") val requesterId: Long,
    @SerializedName("helperId") val helperId: Long,
    @SerializedName("status") val status: String, // "pending", "ongoing", "completed" 등

    @SerializedName("matchedAt") val matchedAt: String?,
    @SerializedName("startedAt") val startedAt: String?,
    @SerializedName("completedAt") val completedAt: String?,

    @SerializedName("actualMinutes") val actualMinutes: Int?,
    @SerializedName("earnedPoints") val earnedPoints: Int?,
    @SerializedName("earnedVolunteerMinutes") val earnedVolunteerMinutes: Int?,

    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?,

    @SerializedName("helper") val helper: MyRequestHelperDto?
)

data class MyRequestHelperDto(
    @SerializedName("id") val id: Long,
    @SerializedName("name") val name: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?
)