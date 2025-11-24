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
    val startLat: Double,
    val startLng: Double,
    val endLat: Double,
    val endLng: Double
)

fun RequestUiModel.toRequest(): Request {
    return Request(
        id = this.id,
        date = this.dateLabel,
        departure = this.from,
        arrival = this.to,
        departureTime = this.departTime,
        arrivalTime = this.arriveTime,
        distance = this.distanceLabel,
        duration = "0분", // (상세화면용)
        pricePoints = 0,
        startLatitude = this.startLat,
        startLongitude = this.startLng,
        endLatitude = this.endLat,
        endLongitude = this.endLng
    )
}
