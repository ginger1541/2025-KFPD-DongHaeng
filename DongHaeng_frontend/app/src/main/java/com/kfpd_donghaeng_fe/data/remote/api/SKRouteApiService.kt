// main/java/com/kfpd_donghaeng_fe/data/remote/api/SKRouteApiService.kt
package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.SKWalkingRouteResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

interface SKRouteApiService {
    // 💡 [수정] GET -> POST, 경로 변경, version=1 추가
    @FormUrlEncoded
    @POST("tmap/routes/pedestrian?version=1")
    suspend fun getWalkingRoute(
        @Header("appKey") appKey: String,

        // 💡 [수정] Query -> Field 로 변경 (POST Body 전송)
        @Field("startX") startX: Double,
        @Field("startY") startY: Double,
        @Field("endX") endX: Double,
        @Field("endY") endY: Double,

        // 💡 [추가] TMAP API 필수 파라미터
        @Field("startName") startName: String,
        @Field("endName") endName: String,

        @Field("reqCoordType") reqCoordType: String = "WGS84GEO",
        @Field("resCoordType") resCoordType: String = "WGS84GEO"
    ): SKWalkingRouteResponse
}