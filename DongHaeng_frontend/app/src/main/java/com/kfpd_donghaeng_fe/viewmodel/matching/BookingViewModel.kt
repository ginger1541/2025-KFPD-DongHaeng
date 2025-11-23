package com.kfpd_donghaeng_fe.viewmodel.matching

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateDto
import com.kfpd_donghaeng_fe.data.repository.MatchRepositoryImpl
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.repository.RequestRepository
import com.kfpd_donghaeng_fe.ui.matching.MatchingPhase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSelectedTime(newTime: LocalDateTime) {
        _selectedDateTime.value = newTime
    }

    // ✅ [핵심] 동행 요청 생성 (API 호출)
    // 화면에서 "결제하기" 버튼을 누르면 이 함수가 호출됩니다.
    @RequiresApi(Build.VERSION_CODES.O)
    fun createRequest(
        start: RouteLocation,
        end: RouteLocation,
        description: String?,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // DTO 생성
                val requestDto = RequestCreateDto(
                    startAddress = start.address,
                    destinationAddress = end.address,
                    startLatitude = start.latitude ?: 0.0,
                    startLongitude = start.longitude ?: 0.0,
                    destinationLatitude = end.latitude ?: 0.0,
                    destinationLongitude = end.longitude ?: 0.0,
                    scheduledAt = _selectedDateTime.value.toString(),
                    estimatedMinutes = 30, // TODO: 지도 API 연동 시 실제 값 사용
                    serviceType = "SIMPLE_MOVE",
                    description = description
                )

                // Repository 호출 (RequestRepository 사용)
                requestRepository.createRequest(requestDto)
                    .onSuccess {
                        onSuccess()
                    }
                    .onFailure { e ->
                        onError(e.message ?: "예약 요청 실패")
                    }
            } catch (e: Exception) {
                onError("네트워크 오류가 발생했습니다.")
            }
        }
    }
}