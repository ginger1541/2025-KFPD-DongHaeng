package com.kfpd_donghaeng_fe.data.mapper

import com.kfpd_donghaeng_fe.data.remote.dto.SKWalkingRouteResponse
import com.kfpd_donghaeng_fe.domain.entity.RoutePoint
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute

/**
 * SKWalkingRouteResponse DTO를 WalkingRoute Domain Entity로 변환하는 확장 함수
 */
fun SKWalkingRouteResponse.toDomain(): WalkingRoute {
    // 1. LineString 타입의 Feature에서 모든 좌표 추출
    val allPoints = mutableListOf<RoutePoint>()
    var totalDistance = 0
    var totalTime = 0

    // Feature 리스트를 순회하며 경로 좌표를 추출합니다.
    this.features.forEach { feature ->
        // LineString 타입의 Geometry만 처리하여 폴리라인 좌표를 추출합니다.
        if (feature.geometry.type == "LineString") {
            // SK API의 좌표는 [경도(longitude), 위도(latitude)] 순서입니다.
            val routePoints = feature.geometry.coordinates.map { coords ->
                RoutePoint(
                    longitude = coords[0], // 경도 (X)
                    latitude = coords[1]  // 위도 (Y)
                )
            }
            allPoints.addAll(routePoints)
        }

        // 경로의 총 거리와 시간을 합산 (보통 마지막 Feature의 Properties에 최종 정보가 들어있음)
        // 하지만 안전하게 모든 Feature의 정보를 합치거나, 필요한 정보만 추출할 수 있습니다.
        if (feature.properties.totalDistance > 0) {
            totalDistance = feature.properties.totalDistance
        }
        if (feature.properties.totalTime > 0) {
            totalTime = feature.properties.totalTime
        }
    }

    // 2. 중복 좌표 제거 (경로가 이어질 때 중복될 수 있음)
    // LinkedHashSet을 사용하여 순서를 유지하면서 중복을 제거합니다.
    val distinctPoints = allPoints.toSet().toList()

    // 3. Domain Entity 생성
    return WalkingRoute(
        points = distinctPoints,
        totalDistance = totalDistance,
        totalTime = totalTime
    )
}