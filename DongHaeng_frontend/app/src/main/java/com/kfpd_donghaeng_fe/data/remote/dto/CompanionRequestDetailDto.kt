package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

// 상세 조회 응답 DTO
data class CompanionRequestDetailDto(
    val id: Long,
    val title: String,
    val description: String?, // 요청사항
    val startAddress: String,
    val destinationAddress: String,
    val startLatitude: Double,
    val startLongitude: Double,
    val destinationLatitude: Double,
    val destinationLongitude: Double,
    val estimatedMinutes: Int,
    val scheduledAt: String, // ISO 8601
    val route: RouteInfoDto?,
    val requester: RequesterProfileDto
)

data class RouteInfoDto(
    @SerializedName("estimated_price")
    val estimatedPrice: Int?,
    @SerializedName("total_distance_meters")
    val totalDistanceMeters: Int?,

    // TODO: API 수정사항 확인
    @SerializedName("points")
    val points: List<PointDto>?
)

data class PointDto(
    val lat: Double,
    val lng: Double
)

data class RequesterProfileDto(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val companionScore: Double?
)

// 매칭 수락 응답 DTO
data class MatchResponseDto(
    val matchId: Long,
    val chatRoomId: Long,
    val status: String,
    val matchedAt: String
)