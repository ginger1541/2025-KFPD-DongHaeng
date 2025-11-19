package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.BuildConfig
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute
import com.kfpd_donghaeng_fe.domain.repository.RouteRepository // 인터페이스 임포트
import com.kfpd_donghaeng_fe.data.mapper.toDomain
import com.kfpd_donghaeng_fe.data.remote.api.SKRouteApiService
import javax.inject.Inject

/**
 * RouteRepository 인터페이스의 SK API 기반 구현체
 * RouteRepository 인터페이스를 구현합니다.
 */
class RouteRepositoryImpl @Inject constructor( // <-- @Inject 추가
    private val skRouteApiService: SKRouteApiService
) : RouteRepository { // <-- 인터페이스 구현

    // local.properties의 API 키는 BuildConfig를 통해 접근 가능
    private val SK_API_KEY = BuildConfig.SK_OPEN_API_KEY

    override suspend fun fetchWalkingRoute(start: RouteLocation, end: RouteLocation): Result<WalkingRoute> {
        // null 체크: 위도/경도가 Double? 이므로 null이 아닌지 확인
        if (start.latitude == null || start.longitude == null ||
            end.latitude == null || end.longitude == null) {
            return Result.failure(IllegalArgumentException("출발지 또는 도착지 좌표가 유효하지 않습니다."))
        }

        return try {
            val response = skRouteApiService.getWalkingRoute(
                appKey = SK_API_KEY,
                startX = start.longitude, // SK API는 경도(X) 먼저
                startY = start.latitude,  // 위도(Y) 다음
                endX = end.longitude,
                endY = end.latitude
            )

            // DTO를 Domain Entity로 변환하여 반환
            val walkingRoute = response.toDomain()

            Result.success(walkingRoute)
        } catch (e: Exception) {
            // 네트워크 오류, API 응답 오류 등을 처리
            Result.failure(e)
        }
    }
}