package com.kfpd_donghaeng_fe.domain.entity

/**
 * 장소 검색 결과 (Domain Model)
 */
data class PlaceSearchResult(
    val placeName: String,
    val addressName: String,
    val roadAddressName: String,
    val categoryName: String,
    val phone: String,
    val x: String, // longitude
    val y: String  // latitude
)

/**
 * 경로 위치 정보
 */
data class RouteLocation(
    val id: String,
    val type: LocationType,
    val placeName: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null
)

/**
 * PlaceSearchResult을 RouteLocation으로 변환하는 확장 함수
 * 카카오 검색 결과의 X, Y (String)를 SK API가 요구하는 Double로 변환합니다.
 */
fun PlaceSearchResult.toRouteLocation(type: LocationType): RouteLocation {
    // id는 placeName과 좌표를 조합하여 임시로 생성하거나, 고유 ID를 사용해야 합니다.
    val id = "${this.placeName}_${System.currentTimeMillis()}"

    // String을 Double로 안전하게 변환
    val lon = this.x.toDoubleOrNull()
    val lat = this.y.toDoubleOrNull()

    // 경도(x), 위도(y) 순서에 유의하며 RouteLocation 생성
    return RouteLocation(
        id = id,
        type = type,
        placeName = this.placeName,
        address = this.roadAddressName.ifEmpty { this.addressName },
        latitude = lat,
        longitude = lon
    )
}