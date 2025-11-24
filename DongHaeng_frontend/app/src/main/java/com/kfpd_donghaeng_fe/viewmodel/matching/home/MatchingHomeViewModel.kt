package com.kfpd_donghaeng_fe.viewmodel.matching.home

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.kfpd_donghaeng_fe.data.repository.CompanionRepository
import com.kfpd_donghaeng_fe.domain.entity.auth.UserType
import com.kfpd_donghaeng_fe.domain.repository.RequestRepository
import com.kfpd_donghaeng_fe.ui.matching.home.MatchingHomeUiState
import com.kfpd_donghaeng_fe.ui.matching.home.RequestUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.math.*

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class MatchingHomeViewModel @Inject constructor(
    private val companionRepository: CompanionRepository,
    private val requestRepository: RequestRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _userType = MutableStateFlow(UserType.NEEDY)
    val userType: StateFlow<UserType> = _userType.asStateFlow()

    private val _uiState = MutableStateFlow<MatchingHomeUiState>(MatchingHomeUiState.Loading)
    val uiState: StateFlow<MatchingHomeUiState> = _uiState.asStateFlow()

    init {
        loadHomeData()
    }

    fun setUserType(type: UserType) {
        if (_userType.value == type) return
        _userType.value = type
        loadHomeData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = MatchingHomeUiState.Loading
            try {
                when (_userType.value) {
                    UserType.NEEDY -> loadNeedyHome()
                    UserType.HELPER -> loadHelperHome()
                }
            } catch (e: Exception) {
                _uiState.value = MatchingHomeUiState.Error(message = e.message ?: "오류 발생")
            }
        }
    }

    private suspend fun loadNeedyHome() {
        // 1. 리포지토리에서 데이터 가져오기
        val requests = requestRepository.getRequestList()

        // 2. UI 모델로 변환
        val uiList = requests.map { req ->
            RequestUiModel(
                id = req.id,
                dateLabel = req.date,
                from = req.departure,
                to = req.arrival,
                departTime = req.departureTime,
                arriveTime = req.arrivalTime,
                distanceLabel = req.distance
            )
        }

        // 3. 상태 업데이트
        _uiState.value = MatchingHomeUiState.NeedyState(recentTrips = uiList)
    }

    // ✅ [핵심 수정] 날짜 및 거리 계산 로직 추가
    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadHelperHome() {
        // 1. 📍 임시 내 위치 (광주) - 백엔드 테스트용
        val myLat = 35.1595
        val myLng = 126.8526

        /* 실제 위치 사용 시 주석 해제
        val location = getCurrentLocation()
        val myLat = location?.latitude ?: 37.5665
        val myLng = location?.longitude ?: 126.9780
        */

        // 2. API 호출
        val result = companionRepository.getNearbyRequests(myLat, myLng)

        // 3. 결과 처리
        result.onSuccess { dtoList ->
            if (dtoList.isEmpty()) {
                _uiState.value = MatchingHomeUiState.HelperState(nearbyRequests = emptyList())
                return@onSuccess
            }

            val uiList = dtoList.map { dto ->
                // --- 📅 날짜 포맷팅 로직 ---
                val zdt = try {
                    ZonedDateTime.parse(dto.scheduledAt)
                } catch (e: Exception) {
                    ZonedDateTime.now()
                }
                val today = ZonedDateTime.now().toLocalDate()
                val reqDate = zdt.toLocalDate()

                // "오늘", "내일", "11월 25일" 형식으로 변환
                val dateLabelStr = when {
                    reqDate.isEqual(today) -> "오늘"
                    reqDate.isEqual(today.plusDays(1)) -> "내일"
                    else -> zdt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
                }

                val timeStr = zdt.format(DateTimeFormatter.ofPattern("a h시 m분 출발", Locale.KOREA))
                val arriveTimeStr = zdt.plusMinutes(30).format(DateTimeFormatter.ofPattern("a h시 m분 도착", Locale.KOREA))

                // --- 📏 거리 계산 로직 ---
                // dto에 위경도가 없다면 0.0 처리 (서버 데이터 확인 필요)
                val targetLat = dto.latitude ?: 0.0
                val targetLng = dto.longitude ?: 0.0

                // 거리 계산 함수 호출
                val distanceMeters = calculateDistance(myLat, myLng, targetLat, targetLng)

                val distanceLabelStr = if (targetLat == 0.0 || targetLng == 0.0) {
                    "위치 정보 없음"
                } else if (distanceMeters < 1000) {
                    "내 위치에서 ${distanceMeters.toInt()}m"
                } else {
                    String.format("내 위치에서 %.1fkm", distanceMeters / 1000)
                }

                RequestUiModel(
                    id = dto.requestId,
                    dateLabel = dateLabelStr,        // ✅ "오늘", "11월 24일" 등
                    from = dto.startAddress,
                    to = dto.destinationAddress,
                    departTime = timeStr,
                    arriveTime = arriveTimeStr,
                    distanceLabel = distanceLabelStr // ✅ "내 위치에서 1.2km"
                )
            }

            _uiState.value = MatchingHomeUiState.HelperState(nearbyRequests = uiList)

        }.onFailure { e ->
            _uiState.value = MatchingHomeUiState.Error("목록 조회 실패: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun getCurrentLocation(): Location? = suspendCancellableCoroutine { cont ->
        try {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location -> cont.resume(location) }
                .addOnFailureListener { cont.resume(null) }
                .addOnCanceledListener { cont.resume(null) }
        } catch (e: SecurityException) {
            cont.resume(null)
        }
    }

    // 💡 [추가] 두 좌표 간 거리 계산 함수 (Haversine Formula)
    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371e3 // 지구 반지름 (미터)
        val phi1 = lat1 * Math.PI / 180
        val phi2 = lat2 * Math.PI / 180
        val deltaPhi = (lat2 - lat1) * Math.PI / 180
        val deltaLambda = (lon2 - lon1) * Math.PI / 180

        val a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
                cos(phi1) * cos(phi2) *
                sin(deltaLambda / 2) * sin(deltaLambda / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return R * c // 결과: 미터(m) 단위
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun resetErrorState() {
        loadHomeData()
    }
}