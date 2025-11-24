package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

// API 문서 12페이지 참고: GET /api/matches/{matchId} 응답
//data class MatchDetailResponseWrapper(
//    val success: Boolean,
//    val data: MatchDetailDTO
//)
//
//data class MatchDetailDTO(
//    val id: Long, // matchId
//    val status: String, // "ongoing", "pending", "completed" 등
//
//    val requester: MatchUserDTO, // 요청자 정보
//    val helper: MatchUserDTO?,   // 도우미 정보 (nullable)
//
//    val request: MatchRequestDetails, // 요청 상세 정보
//
//    val matchedAt: String?,
//    val startedAt: String?
//)
//
//data class MatchUserDTO(
//    val id: Long,
//    val name: String,
//    val profileImageUrl: String?,
//    val phone: String? // API 문서 13페이지: 전화번호 포함됨
//)
//
//data class MatchRequestDetails(
//    val startAddress: String,      // API 문서: "광주광역시 북구..."
//    val destinationAddress: String,
//
//    val startLatitude: Double,
//    val startLongitude: Double,
//    val destinationLatitude: Double,
//    val destinationLongitude: Double,
//
//    val scheduledAt: String,       // API 문서: "2025-12-01T15:00..."
//    val estimatedMinutes: Int?,    // 예상 소요 시간
//
//    // API 문서 예시에는 없으나 필요한 정보 (없으면 null 처리)
//    val description: String?,      // 요청 사항
//    val route: RouteDTO?           // 경로/금액 정보
//)
//
//data class RouteDTO(
//    @SerializedName("estimated_price")
//    val estimatedPrice: Int?
//)


// TODO: 되는지 확인
data class MatchDetailResponseWrapper(
    val success: Boolean,
    val data: MatchDetailDTO
)

data class MatchDetailDTO(
    // JSON: "matchId": "5" -> DTO: id (Long)
    @SerializedName("matchId", alternate = ["id", "match_id"])
    val id: Long,

    @SerializedName("requestId", alternate = ["request_id"])
    val requestId: Long,

    val status: String, // "pending", "ongoing", "completed" 등

    val requester: MatchUserDTO, // 요청자 정보
    val helper: MatchUserDTO?,   // 도우미 정보 (nullable)

    val request: MatchRequestDetails, // 요청 상세 정보 (Route 포함)

    @SerializedName("matchedAt", alternate = ["matched_at"])
    val matchedAt: String?,
    @SerializedName("startedAt", alternate = ["started_at"])
    val startedAt: String?,
    @SerializedName("completedAt", alternate = ["completed_at"])
    val completedAt: String?
)

data class MatchUserDTO(
    // JSON: "id": "1" -> DTO: id (Long)
    val id: Long,
    val name: String,
    @SerializedName("profileImageUrl", alternate = ["profile_image_url"])
    val profileImageUrl: String?,
    val phone: String?
)

data class MatchRequestDetails(
    val id: Long,
    val title: String?,
    val description: String?,

    // 💡 JSON의 "latitude" -> "startLatitude"로 매핑
    @SerializedName("startLatitude", alternate = ["latitude", "start_latitude"])
    val startLatitude: Double,

    // 💡 JSON의 "longitude" -> "startLongitude"로 매핑
    @SerializedName("startLongitude", alternate = ["longitude", "start_longitude"])
    val startLongitude: Double,

    @SerializedName("startAddress", alternate = ["start_address"])
    val startAddress: String,

    @SerializedName("destinationAddress", alternate = ["destination_address"])
    val destinationAddress: String,

    // 도착지 좌표가 JSON request 객체 내에 없다면 route의 마지막 포인트를 사용하거나 0.0 처리
    // 만약 JSON에 destinationLatitude가 있다면 여기에 추가
    @SerializedName("destinationLatitude", alternate = ["destination_latitude"])
    val destinationLatitude: Double = 0.0,

    @SerializedName("destinationLongitude", alternate = ["destination_longitude"])
    val destinationLongitude: Double = 0.0,

    @SerializedName("estimatedMinutes", alternate = ["estimated_minutes"])
    val estimatedMinutes: Int?,

    @SerializedName("scheduledAt", alternate = ["scheduled_at"])
    val scheduledAt: String?,

    val status: String,

    // 💡 Route 정보 (JSON 구조와 일치시킴)
    @SerializedName("route")
    val route: RouteDto?
)

// 경로 정보 DTO (기존 MatchResponse.kt에 정의된 것과 동일하게 유지하거나 통합)
data class RouteDto(
    @SerializedName("total_distance_meters", alternate = ["totalDistance", "distance"])
    val totalDistanceMeters: Int?,

    @SerializedName("total_duration_seconds", alternate = ["totalDuration", "duration"])
    val totalDurationSeconds: Int?,

    @SerializedName("estimated_price", alternate = ["estimatedPrice"])
    val estimatedPrice: Int?,

    @SerializedName("coord_type")
    val coordType: String?,

    @SerializedName("points", alternate = ["path", "coordinates"])
    val points: List<PointDto>?
)