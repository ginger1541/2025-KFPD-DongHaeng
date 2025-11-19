// domain/repository/MatchingRepository.kt
package com.kfpd_donghaeng_fe.domain.repository

import com.kfpd_donghaeng_fe.domain.entity.MatchingHistory

interface MatchingRepository {
    suspend fun getRecentMatchingHistory(limit: Int): List<MatchingHistory>
    suspend fun getNearbyRequests(): List<MatchingHistory>   // 일단 재활용, 나중에 Request 전용 entity 만들어도 됨
}
