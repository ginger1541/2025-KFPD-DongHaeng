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
    val latitude: String? = null,
    val longitude: String? = null
)