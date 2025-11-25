package com.kfpd_donghaeng_fe.domain.entity

data class RouteInput(
    val id: String,
    val type: LocationType,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isEditable: Boolean = true
)

enum class LocationType {
    START,      // 출발지
    END,        // 도착지
    REQUESTER,  // 요청자
    COMPANION,   // 동행자
    PLACE, // 장소 검색
    TARGET
}

data class RouteState(
    val locations: List<RouteInput> = emptyList(),
)

/**
 * 지도에 표시될 경로 상의 단일 지점
 * SK API 응답의 좌표를 Double로 변환하여 저장합니다.
 */
data class RoutePoint(
    val longitude: Double,
    val latitude: Double
)

/**
 * 보행자 경로 전체 데이터
 * SK API 응답에서 필요한 Polyline 좌표 리스트를 담습니다.
 */
data class WalkingRoute(
    val points: List<RoutePoint>,
    val totalDistance: Int, // 미터
    val totalTime: Int      // 초
    // 추가적인 정보를 포함할 수 있습니다.
)