package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

// API 문서 12페이지 참고: GET /api/matches/{matchId} 응답
data class MatchDetailResponseWrapper(
    val success: Boolean,
    val data: MatchDetailDTO
)

data class MatchDetailDTO(
    val id: Long, // matchId
    val status: String, // "ongoing", "pending", "completed" 등

    val requester: MatchUserDTO, // 요청자 정보
    val helper: MatchUserDTO?,   // 도우미 정보 (nullable)

    val request: MatchRequestDetails, // 요청 상세 정보

    val matchedAt: String?,
    val startedAt: String?
)

data class MatchUserDTO(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val phone: String? // API 문서 13페이지: 전화번호 포함됨
)

data class MatchRequestDetails(
    val startAddress: String,      // API 문서: "광주광역시 북구..."
    val destinationAddress: String,

    val startLatitude: Double,
    val startLongitude: Double,
    val destinationLatitude: Double,
    val destinationLongitude: Double,

    val scheduledAt: String,       // API 문서: "2025-12-01T15:00..."
    val estimatedMinutes: Int?,    // 예상 소요 시간

    // API 문서 예시에는 없으나 필요한 정보 (없으면 null 처리)
    val description: String?,      // 요청 사항
    val route: RouteDTO?           // 경로/금액 정보
)

data class RouteDTO(
    @SerializedName("estimated_price")
    val estimatedPrice: Int?
)