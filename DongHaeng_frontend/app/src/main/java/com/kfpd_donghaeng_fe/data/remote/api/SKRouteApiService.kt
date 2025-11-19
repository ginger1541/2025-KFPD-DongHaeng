package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.SKWalkingRouteResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

// SK Planet API 엔드포인트
interface SKRouteApiService {
    @GET("v1/transit/routes/pedestrian")
    suspend fun getWalkingRoute(
        @Header("appKey") appKey: String,
        @Query("startX") startX: Double, // 출발지 경도
        @Query("startY") startY: Double, // 출발지 위도
        @Query("endX") endX: Double,     // 도착지 경도
        @Query("endY") endY: Double,       // 도착지 위도
        @Query("reqCoordType") reqCoordType: String = "WGS84GEO", // 요청 좌표계
        @Query("resCoordType") resCoordType: String = "WGS84GEO", // 응답 좌표계
    ): SKWalkingRouteResponse
}