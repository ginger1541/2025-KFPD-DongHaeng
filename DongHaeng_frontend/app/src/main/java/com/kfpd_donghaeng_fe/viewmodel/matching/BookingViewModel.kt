package com.kfpd_donghaeng_fe.viewmodel.matching

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.remote.dto.PointDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateDto
import com.kfpd_donghaeng_fe.data.remote.dto.RouteCreateDto
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute
import com.kfpd_donghaeng_fe.domain.repository.RequestRepository
import com.kfpd_donghaeng_fe.ui.matching.MatchingPhase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val requestRepository: RequestRepository
) : ViewModel() {

    // 1. 예약 진행 단계 관리 (Overview -> Booking -> ... -> Payment)
    private val _currentPhase = MutableStateFlow(MatchingPhase.OVERVIEW)
    val currentPhase: StateFlow<MatchingPhase> = _currentPhase.asStateFlow()

    // 2. 예약 시간 관리
    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDateTime = MutableStateFlow(LocalDateTime.now().plusHours(1))
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDateTime: StateFlow<LocalDateTime> = _selectedDateTime.asStateFlow()

    // 3. 화면 이동 함수들
    fun navigateToBooking() { _currentPhase.value = MatchingPhase.BOOKING }
    fun navigateToServiceType() { _currentPhase.value = MatchingPhase.SERVICE_TYPE }
    fun navigateToTimeSelection() { _currentPhase.value = MatchingPhase.TIME_SELECTION }
    fun navigateToRequestDetail() { _currentPhase.value = MatchingPhase.REQUEST_DETAIL }
    fun navigateToPayment() { _currentPhase.value = MatchingPhase.PAYMENT }
    fun navigateToOverview() { _currentPhase.value = MatchingPhase.OVERVIEW }

    // 4. 경로, 요청사항 저장
    private var _calculatedRoute: WalkingRoute? = null
    private val _requestDescription = MutableStateFlow("")
    val requestDescription: StateFlow<String> = _requestDescription.asStateFlow()

    fun setCalculatedRoute(route: WalkingRoute) {
        _calculatedRoute = route
    }
    fun updateDescription(text: String) {
        _requestDescription.value = text
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSelectedTime(newTime: LocalDateTime) {
        _selectedDateTime.value = newTime
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createRequest(
        start: RouteLocation,
        end: RouteLocation,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        Log.d("MatchingViewModel", "=== 동행 요청 시작 ===")
        Log.d("MatchingViewModel", "출발지: ${start.placeName} (${start.address})")
        Log.d("MatchingViewModel", "목적지: ${end.placeName} (${end.address})")

        val route = _calculatedRoute
        if (route == null) {
            Log.e("MatchingViewModel", "경로 정보 없음")
            onError("경로 정보가 없습니다.")
            return
        }

        Log.d("MatchingViewModel", "경로 정보: 거리=${route.totalDistance}m, 시간=${route.totalTime}초")

        viewModelScope.launch {
            try {
                // 💡 ISO 8601 형식으로 변환 (타임존 포함)

                val koreanFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
                val localTimeStr = _selectedDateTime.value.format(koreanFormatter)
                val scheduledAtString = "${localTimeStr}+09:00"

                Log.d("MatchingViewModel", "변환된 scheduledAt: $scheduledAtString")

                val requestDto = RequestCreateDto(
                    title = "${start.placeName} -> ${end.placeName} 동행 요청",
                    description = _requestDescription.value,
                    startAddress = start.address,
                    destinationAddress = end.address,
                    startLatitude = start.latitude ?: 0.0,
                    startLongitude = start.longitude ?: 0.0,
                    destinationLatitude = end.latitude ?: 0.0,
                    destinationLongitude = end.longitude ?: 0.0,
                    estimatedMinutes = route.totalTime / 60,
                    scheduledAt = scheduledAtString, // 💡 변경
                    route = RouteCreateDto(
                        coordType = "WGS84",
                        totalDistanceMeters = route.totalDistance,
                        totalDurationSeconds = route.totalTime,
                        estimatedPrice = calculatePrice(route.totalDistance),
                        points = route.points.map { PointDto(it.latitude, it.longitude) }
                    )
                )

                // 💡 보내는 데이터 로그 (JSON 형태로 확인)
                Log.d("MatchingViewModel", "=== 전송할 데이터 ===")
                Log.d("MatchingViewModel", "title: ${requestDto.title}")
                Log.d("MatchingViewModel", "description: ${requestDto.description}")
                Log.d("MatchingViewModel", "startAddress: ${requestDto.startAddress}")
                Log.d("MatchingViewModel", "destinationAddress: ${requestDto.destinationAddress}")
                Log.d("MatchingViewModel", "startLatitude: ${requestDto.startLatitude}")
                Log.d("MatchingViewModel", "startLongitude: ${requestDto.startLongitude}")
                Log.d("MatchingViewModel", "destinationLatitude: ${requestDto.destinationLatitude}")
                Log.d("MatchingViewModel", "destinationLongitude: ${requestDto.destinationLongitude}")
                Log.d("MatchingViewModel", "estimatedMinutes: ${requestDto.estimatedMinutes}")
                Log.d("MatchingViewModel", "scheduledAt: ${requestDto.scheduledAt}")
                Log.d("MatchingViewModel", "route.coordType: ${requestDto.route.coordType}")
                Log.d("MatchingViewModel", "route.totalDistanceMeters: ${requestDto.route.totalDistanceMeters}")
                Log.d("MatchingViewModel", "route.totalDurationSeconds: ${requestDto.route.totalDurationSeconds}")
                Log.d("MatchingViewModel", "route.estimatedPrice: ${requestDto.route.estimatedPrice}")
                Log.d("MatchingViewModel", "route.points 개수: ${requestDto.route.points.size}")
                Log.d("MatchingViewModel", "route.points 첫 점: lat=${requestDto.route.points.firstOrNull()?.lat}, lng=${requestDto.route.points.firstOrNull()?.lng}")
                Log.d("MatchingViewModel", "route.points 마지막 점: lat=${requestDto.route.points.lastOrNull()?.lat}, lng=${requestDto.route.points.lastOrNull()?.lng}")

                Log.d("MatchingViewModel", "API 호출 시작...")

                // API 호출
                requestRepository.createRequest(requestDto)
                    .onSuccess { response ->
                        Log.d("MatchingViewModel", "✅ API 호출 성공!")
                        Log.d("MatchingViewModel", "응답 데이터: $response")
                        onSuccess()
                    }
                    .onFailure { e ->
                        Log.e("MatchingViewModel", "❌ API 호출 실패: ${e.message}")
                        Log.e("MatchingViewModel", "스택 트레이스:", e)
                        onError(e.message ?: "예약 요청 실패")
                    }
            } catch (e: Exception) {
                Log.e("MatchingViewModel", "❌ 예외 발생: ${e.message}")
                Log.e("MatchingViewModel", "스택 트레이스:", e)
                onError("네트워크 오류가 발생했습니다: ${e.message}")
            }
        }
    }

    // 임시 요금 계산 로직 (기본 1000원 + 100m당 100원)
    private fun calculatePrice(distanceMeters: Int): Int {
        return 1000 + (distanceMeters / 100) * 100
    }
}