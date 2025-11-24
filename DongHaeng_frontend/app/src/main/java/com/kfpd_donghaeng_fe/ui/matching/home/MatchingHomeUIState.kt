package com.kfpd_donghaeng_fe.ui.matching.home

import com.kfpd_donghaeng_fe.data.Request

sealed class MatchingHomeUiState {
    data object Loading : MatchingHomeUiState()

    data class NeedyState(
        val recentTrips: List<RequestUiModel>
    ) : MatchingHomeUiState()

    data class HelperState(
        val nearbyRequests: List<RequestUiModel>
    ) : MatchingHomeUiState()

    data class Error(val message: String? = null) : MatchingHomeUiState()
}


data class RequestUiModel(
    val id: Long,
    val dateLabel: String,
    val from: String,
    val to: String,
    val departTime: String,
    val arriveTime: String,
    val distanceLabel: String,
)

// 💡 변환 로직 업데이트
fun RequestUiModel.toRequest(): Request {
    return Request(
        id = this.id,
        date = this.dateLabel,
        departure = this.from,
        arrival = this.to,
        departureTime = this.departTime,
        arrivalTime = this.arriveTime,
        distance = this.distanceLabel,
        duration = "0분", // ✅ [추가] 홈 화면 모델에서 변환 시엔 기본값 (상세 화면에선 API로 채움)
        pricePoints = 0
    )
}
