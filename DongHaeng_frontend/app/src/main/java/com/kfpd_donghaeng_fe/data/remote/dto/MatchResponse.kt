package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MatchListResponse(
    val success: Boolean,
    val data: MatchData
)

data class MatchData(
    val asHelper: List<MatchItemResponse>,
    val asRequester: List<MatchItemResponse>
)

data class MatchItemResponse(
    val matchId: Long,
    val requestId: Long,
    val status: String, // "pending", "in_progress", "completed", "cancelled"

    val matchedAt: String?,
    val startedAt: String?,
    val completedAt: String?,

    val request: RequestDetail, // 요청 상세 정보

    val requester: UserProfile?,
    val helper: UserProfile?
)

data class RequestDetail(
    val requestId: Long,

    // ✅ API 변경 사항 반영 (호환성 확보를 위해 alternate 사용)
    @SerializedName("startAddress", alternate = ["departureLocation"])
    val startAddress: String,

    @SerializedName("destinationAddress", alternate = ["arrivalLocation"])
    val destinationAddress: String,

    // ✅ 기존 departureTime 대신 scheduledAt 사용
    @SerializedName("scheduledAt", alternate = ["departureTime"])
    val scheduledAt: String,

    val estimatedMinutes: Int,

    // ✅ 신규 추가된 route 필드 (nullable)
    val route: RouteInfo? = null
)

data class RouteInfo(
    @SerializedName("total_distance_meters")
    val totalDistanceMeters: Int?,
    @SerializedName("estimated_price")
    val estimatedPrice: Int?
    // points 배열 등은 필요 시 추가
)

data class UserProfile(
    val id: Long,
    val name: String,
    val phone: String?,
    val profileImageUrl: String?
)