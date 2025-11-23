package com.kfpd_donghaeng_fe.domain.repository

import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute

/**
 * 경로 관련 데이터 작업을 정의하는 인터페이스
 */
interface RouteRepository {
    /**
     * 출발지와 도착지를 기반으로 보행자 경로 정보를 가져옵니다.
     */
    suspend fun fetchWalkingRoute(start: RouteLocation, end: RouteLocation): Result<WalkingRoute>
}