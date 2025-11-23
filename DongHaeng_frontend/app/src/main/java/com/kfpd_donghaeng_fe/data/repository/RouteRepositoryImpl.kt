// main/java/com/kfpd_donghaeng_fe/data/repository/RouteRepositoryImpl.kt
package com.kfpd_donghaeng_fe.data.repository

import android.util.Log
import com.kfpd_donghaeng_fe.BuildConfig
import com.kfpd_donghaeng_fe.data.mapper.toDomain
import com.kfpd_donghaeng_fe.data.remote.api.SKRouteApiService
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute
import com.kfpd_donghaeng_fe.domain.repository.RouteRepository
import java.net.URLEncoder
import javax.inject.Inject

class RouteRepositoryImpl @Inject constructor(
    private val skRouteApiService: SKRouteApiService
) : RouteRepository {

    private val SK_API_KEY = BuildConfig.SK_OPEN_API_KEY

    override suspend fun fetchWalkingRoute(start: RouteLocation, end: RouteLocation): Result<WalkingRoute> {
        Log.d("RouteRepository", "🚀 경로 요청 시작 (POST)")
        Log.d("RouteRepository", "   👉 출발: ${start.placeName} (${start.latitude}, ${start.longitude})")
        Log.d("RouteRepository", "   👉 도착: ${end.placeName} (${end.latitude}, ${end.longitude})")

        if (start.latitude == null || start.longitude == null ||
            end.latitude == null || end.longitude == null) {
            return Result.failure(IllegalArgumentException("좌표 오류"))
        }

        return try {
            // 💡 [수정] 한글 이름 인코딩 처리 (Retrofit @Field가 처리해주지만 안전을 위해 명시 가능)
            // 보통은 그대로 넘겨도 Retrofit이 UTF-8로 인코딩합니다.
            val startNameValue = start.placeName.ifEmpty { "출발지" }
            val endNameValue = end.placeName.ifEmpty { "도착지" }

            val response = skRouteApiService.getWalkingRoute(
                appKey = SK_API_KEY,
                startX = start.longitude,
                startY = start.latitude,
                endX = end.longitude,
                endY = end.latitude,
                startName = startNameValue, // [추가]
                endName = endNameValue      // [추가]
            )

            Log.d("RouteRepository", "✅ API 응답 성공: ${response.features.size} features")
            val walkingRoute = response.toDomain()
            Result.success(walkingRoute)

        } catch (e: Exception) {
            Log.e("RouteRepository", "🔥 API 요청 실패: ${e.message}")
            e.printStackTrace()
            Result.failure(e)
        }
    }
}