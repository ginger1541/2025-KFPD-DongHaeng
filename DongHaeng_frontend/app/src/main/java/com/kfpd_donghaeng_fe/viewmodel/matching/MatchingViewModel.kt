package com.kfpd_donghaeng_fe.viewmodel.matching

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.kakao.vectormap.LatLng
import com.kfpd_donghaeng_fe.domain.entity.LocationType
import com.kfpd_donghaeng_fe.ui.matching.MatchingPhase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class LocationInput(
    val id: Int,
    val address: String,
    val type: LocationType,
    val isEditable: Boolean = true
)

data class LatLng(val latitude: Double, val longitude: Double)

open class MatchingViewModel : ViewModel() {
    // 1. 요청자 모달 내부 상태 (Overview -> Booking -> Confirm)
    private val _currentPhase = MutableStateFlow(MatchingPhase.OVERVIEW)
    val currentPhase: StateFlow<MatchingPhase> = _currentPhase

    // 2. 💡 시간 피커 모달 표시 상태 추가
    private val _showTimePicker = MutableStateFlow(false)
    val showTimePicker: StateFlow<Boolean> = _showTimePicker

    // 3. 경로 입력 상태 (초기 상태 설정)
    private val initialLocations = listOf(
        LocationInput(
            id = 1,
            address = "내 위치: 서울 마포구 신촌로 24길 38-4", // 예시 데이터
            type = LocationType.START,
            isEditable = false
        ),
        LocationInput(
            id = 2,
            address = "도착지 입력",
            type = LocationType.END,
            isEditable = true
        )
    )
    private val _routeInputs = mutableStateOf(initialLocations)
    val routeInputs: androidx.compose.runtime.State<List<LocationInput>> = _routeInputs

    // 4. 예약 시간 및 날짜 상태
    @RequiresApi(Build.VERSION_CODES.O)
    private val _selectedDateTime = mutableStateOf(LocalDateTime.now().plusHours(1))
    @RequiresApi(Build.VERSION_CODES.O)
    val selectedDateTime: androidx.compose.runtime.State<LocalDateTime> = _selectedDateTime

    val formattedDateTime: String
        @RequiresApi(Build.VERSION_CODES.O)
        get() {
            val dayOfWeek = when (_selectedDateTime.value.dayOfWeek) {
                DayOfWeek.MONDAY -> "월"
                DayOfWeek.TUESDAY -> "화"
                DayOfWeek.WEDNESDAY -> "수"
                DayOfWeek.THURSDAY -> "목"
                DayOfWeek.FRIDAY -> "금"
                DayOfWeek.SATURDAY -> "토"
                DayOfWeek.SUNDAY -> "일"
            }
            return _selectedDateTime.value.format(DateTimeFormatter.ofPattern("M월 d일 ($dayOfWeek) a h시 m분"))
        }

    // 💡 예약 확인 화면에 표시할 경로 데이터 (Mock)
    private val _confirmedRoute = mutableStateOf(listOf(
        "서강대학교 인문대학 1호관", "현대백화점 더 현대 서울", "루프 홍대점"
    ))
    val confirmedRoute: androidx.compose.runtime.State<List<String>> = _confirmedRoute

    // 💡 예약 확인 화면에 표시할 시간 데이터 (Mock)
    private val _confirmedTimes = mutableStateOf(Pair("17시 30분 출발", "20시 00분 도착"))
    val confirmedTimes: androidx.compose.runtime.State<Pair<String, String>> = _confirmedTimes


    fun navigateToBooking() {
        _currentPhase.value = MatchingPhase.BOOKING
    }

    fun navigateToOverview() {
        _currentPhase.value = MatchingPhase.OVERVIEW
    }

    fun navigateToServiceType() {
        _currentPhase.value = MatchingPhase.SERVICE_TYPE
    }

    fun navigateToTimeSelection() {
        _currentPhase.value = MatchingPhase.TIME_SELECTION
    }

    fun navigateToRequestDetail() {
        _currentPhase.value = MatchingPhase.REQUEST_DETAIL
    }

    fun navigateToPayment() {
        // TODO: 여기서 SK API 예상 경로/요금 계산 로직을 호출해야 함.
        _currentPhase.value = MatchingPhase.PAYMENT
    }



    @RequiresApi(Build.VERSION_CODES.O)
    fun updateSelectedTime(newDateTime: LocalDateTime) {
        _selectedDateTime.value = newDateTime
    }

    fun findRouteAndCalculatePrice(start: Double, end: Double, waypoints: List<Any>) {
        // TODO: SK Open API 호출 로직 (보행자 경로)
        // TODO: 카카오맵 폴리라인 데이터로 변환
        // TODO: 서버에서 예상 요금 계산

        _currentPhase.value = MatchingPhase.CONFIRM
    }

    fun removeLocation(locationId: Int) {
        _routeInputs.value = _routeInputs.value.filter { it.id != locationId }
    }
}