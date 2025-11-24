package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MyRequestResponseData(
    @SerializedName("requests") val requests: List<MyRequestItemDto>,
    @SerializedName("count") val count: Int
)

data class MyRequestItemDto(
    @SerializedName("id") val id: Long, // ✅ ID 매핑
    @SerializedName("title") val title: String,
    @SerializedName("startAddress") val startAddress: String,
    @SerializedName("destinationAddress") val destinationAddress: String,
    @SerializedName("estimatedMinutes") val estimatedMinutes: Int,
    @SerializedName("scheduledAt") val scheduledAt: String,
    @SerializedName("status") val status: String, // "pending", "matching", "completed" 등

    @SerializedName("route") val route: MyRequestRouteDto?, // Route는 null일 수 있음
    @SerializedName("match") val match: MyRequestMatchDto?  // Match도 null일 수 있음
)

data class MyRequestRouteDto(
    @SerializedName("total_distance_meters") val totalDistanceMeters: Int?
)

data class MyRequestMatchDto(
    @SerializedName("matchId") val matchId: Long,
    @SerializedName("helper") val helper: MyRequestHelperDto?
)

data class MyRequestHelperDto(
    @SerializedName("name") val name: String
)