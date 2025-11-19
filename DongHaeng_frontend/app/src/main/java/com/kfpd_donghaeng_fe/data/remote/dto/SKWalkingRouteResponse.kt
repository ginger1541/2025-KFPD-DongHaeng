package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

/**
 * SK Planet 보행자 경로 API의 최상위 응답 모델
 */
data class SKWalkingRouteResponse(
    // GeoJSON FeatureCollection 구조를 따릅니다.
    val features: List<Feature>
)

/**
 * GeoJSON Feature 객체. 경로 지오메트리 및 속성을 포함합니다.
 */
data class Feature(
    val type: String, // "Feature"
    val geometry: Geometry,
    val properties: Properties
)

/**
 * 경로의 실제 좌표 정보 (Geometry)
 */
data class Geometry(
    val type: String, // "Point", "LineString" 등
    // 좌표 리스트. LineString 타입일 때 [ [경도, 위도], [경도, 위도], ... ]
    val coordinates: List<List<Double>>
)

/**
 * 경로의 속성 정보 (Properties)
 */
data class Properties(
    val totalDistance: Int = 0, // 총 거리 (미터)
    val totalTime: Int = 0,     // 총 시간 (초)
    val index: Int = 0,
    @SerializedName("lineIndex")
    val lineIndex: Int? = null,
    val description: String? = null // 경로 세부 설명 (예: "출발지점")
    // 필요한 다른 속성들을 추가할 수 있습니다.
)