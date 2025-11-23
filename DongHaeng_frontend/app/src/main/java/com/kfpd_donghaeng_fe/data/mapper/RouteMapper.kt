package com.kfpd_donghaeng_fe.data.mapper

import com.kfpd_donghaeng_fe.data.remote.dto.SKWalkingRouteResponse
import com.kfpd_donghaeng_fe.domain.entity.RoutePoint
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute

fun SKWalkingRouteResponse.toDomain(): WalkingRoute {
    val allPoints = mutableListOf<RoutePoint>()
    var totalDistance = 0
    var totalTime = 0

    this.features.forEach { feature ->
        // 💡 [수정] Geometry 타입에 따라 분기 처리
        val geometry = feature.geometry

        if (geometry.type == "LineString") {
            // LineString인 경우: coordinates는 이중 배열 [[x,y], [x,y], ...]
            try {
                val coordinatesArray = geometry.coordinates.asJsonArray

                val routePoints = coordinatesArray.map { jsonElement ->
                    val pointArray = jsonElement.asJsonArray
                    RoutePoint(
                        longitude = pointArray[0].asDouble, // 경도
                        latitude = pointArray[1].asDouble   // 위도
                    )
                }
                allPoints.addAll(routePoints)
            } catch (e: Exception) {
                e.printStackTrace() // 파싱 에러 로그
            }
        }
        // 참고: type == "Point"인 경우는 단순 좌표 하나([x, y])이므로
        // 경로선 그리기에 포함하지 않거나 별도로 처리할 수 있습니다. (현재는 무시)

        // 총 거리/시간 계산 (마지막 Feature에 전체 정보가 있거나 누적될 수 있음)
        if (feature.properties.totalDistance > 0) {
            totalDistance = feature.properties.totalDistance
        }
        if (feature.properties.totalTime > 0) {
            totalTime = feature.properties.totalTime
        }
    }

    // 중복 제거 및 반환
    val distinctPoints = allPoints.toSet().toList()

    return WalkingRoute(
        points = distinctPoints,
        totalDistance = totalDistance,
        totalTime = totalTime
    )
}