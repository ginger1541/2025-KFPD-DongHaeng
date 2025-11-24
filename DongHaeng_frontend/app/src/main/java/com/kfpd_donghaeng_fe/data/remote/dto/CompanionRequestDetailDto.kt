package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

// 상세 조회 응답 DTO (data 객체 내부)
data class CompanionRequestDetailDto(
    val id: Long, // JSON은 String "7"이지만 Gson이 Long으로 자동 변환해줍니다.
    val requesterId: Long,
    val title: String,
    val description: String?,

    // 💡 JSON 필드명 매핑 (String으로 오지만 Double로 받기)
    @SerializedName("latitude") val startLatitude: Double,
    @SerializedName("longitude") val startLongitude: Double,

    val startAddress: String,
    val destinationAddress: String,

    // ⚠️ 목적지 좌표가 최상위에 없으므로, 필요하다면 route의 마지막 포인트나 별도 로직 사용
    // 일단 DTO에서는 제거하거나 nullable로 처리 (여기선 제거하고 UI에선 route 정보 활용 권장)

    val estimatedMinutes: Int,
    val scheduledAt: String, // ISO 8601

    val route: RouteInfoDto?,
    val requester: RequesterProfileDto,
    val status: String
)

data class RouteInfoDto(
    @SerializedName("estimated_price")
    val estimatedPrice: Int?,
    @SerializedName("total_distance_meters")
    val totalDistanceMeters: Int?,
    @SerializedName("total_duration_seconds")
    val totalDurationSeconds: Int?,

    @SerializedName("points")
    val points: List<PointDto>?
)

data class RequesterProfileDto(
    val id: Long,
    val name: String,
    val profileImageUrl: String?,
    val companionScore: Double?, // JSON "0" -> Double 자동 변환
    val userType: String?
)

data class MatchResponseDto(
    @SerializedName(value = "matchId", alternate = ["match_id", "MatchId"])
    val matchId: Long,

    @SerializedName(value = "chatRoomId", alternate = ["chat_room_id", "ChatRoomId"])
    val chatRoomId: Long,

    @SerializedName(value = "status", alternate = ["Status"])
    val status: String,

    @SerializedName(value = "matchedAt", alternate = ["matched_at", "MatchedAt"])
    val matchedAt: String,

    @SerializedName("route", alternate = ["Route", "path_info"]) // 다양한 이름 대응
val route: RouteDto? = null
)

//data class RouteDto(
//    @SerializedName("total_distance_meters", alternate = ["totalDistance", "distance"])
//    val totalDistanceMeters: Int?,
//
//    @SerializedName("points", alternate = ["path", "coordinates"])
//    val points: List<PointDto>?
//)