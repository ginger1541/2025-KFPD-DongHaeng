package com.kfpd_donghaeng_fe.domain.repository.fake

import com.kfpd_donghaeng_fe.domain.entity.MatchingHistory
import com.kfpd_donghaeng_fe.domain.repository.MatchingRepository
import javax.inject.Inject

class FakeMatchingRepository @Inject constructor() : MatchingRepository {

    private val mockHistory = listOf(
        MatchingHistory(
            id = 1,
            date = "2024-08-13",
            fromName = "서강대학교 인문대학 1호관",
            toName = "루프 홍대점",
            departTime = "17:10",
            arriveTime = "17:30",
            distanceMeter = 500
        ),
        MatchingHistory(
            id = 2,
            date = "2024-08-14",
            fromName = "서강대학교 인문대학 1호관",
            toName = "루프 홍대점",
            departTime = "17:10",
            arriveTime = "17:30",
            distanceMeter = 500
        )
    )

    override suspend fun getRecentMatchingHistory(limit: Int): List<MatchingHistory> {
        return mockHistory.take(limit)
    }

    override suspend fun getNearbyRequests(): List<MatchingHistory> {
        // 일단 같은 데이터를 재사용, 나중에 별도 mock 리스트 만들어도 됨
        return mockHistory
    }
}
