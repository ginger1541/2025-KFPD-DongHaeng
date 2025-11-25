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
import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
import com.kfpd_donghaeng_fe.data.repository.CompanionRepository
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
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
    @ApplicationContext private val context: Context,
    private val tokenDataSource: TokenLocalDataSource
) : ViewModel() {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val _userType = MutableStateFlow(UserType.NEEDY)
    val userType: StateFlow<UserType> = _userType.asStateFlow()

    private val _uiState = MutableStateFlow<MatchingHomeUiState>(MatchingHomeUiState.Loading)
    val uiState: StateFlow<MatchingHomeUiState> = _uiState.asStateFlow()


    private val _targetLocation = MutableStateFlow<RouteLocation?>(null)
    val targetLocation = _targetLocation.asStateFlow()

    init {
        loadHomeData()

        viewModelScope.launch {
            tokenDataSource.getTargetLocationFlow().collect { savedLoc ->
                if (savedLoc != null) {
                    _targetLocation.value = savedLoc
                    loadHomeData() // 저장된 위치가 있으면 새로고침
                }
            }
        }
    }

    fun updateTargetLocation(location: RouteLocation) {
        viewModelScope.launch {
            tokenDataSource.saveTargetLocation(location)
            // Flow를 구독하고 있으므로 _targetLocation.value는 자동 업데이트됨
        }
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
        val requests = requestRepository.getRequestList() // Request 리스트 가져옴 (좌표 포함되어야 함)

        val uiList = requests.map { req ->
            RequestUiModel(
                id = req.id,
                dateLabel = req.date,
                from = req.departure,
                to = req.arrival,
                departTime = req.departureTime,
                arriveTime = req.arrivalTime,
                distanceLabel = req.distance,
                startLat = req.startLatitude,
                startLng = req.startLongitude,
                endLat = req.endLatitude,
                endLng = req.endLongitude
            )
        }
        _uiState.value = MatchingHomeUiState.NeedyState(recentTrips = uiList)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private suspend fun loadHelperHome() {
        // ✅ [수정] 저장된 타겟 위치 확인
        val target = _targetLocation.value

        // 1. 타겟 위치가 있으면 사용, 없으면 광주 기본값 사용
        val searchLat = target?.latitude ?: 35.1595
        val searchLng = target?.longitude ?: 126.8526

        // 2. API 호출 (결정된 좌표로 요청)
        val result = companionRepository.getNearbyRequests(searchLat, searchLng)
        // 3. 결과 처리
        result.onSuccess { dtoList ->
            if (dtoList.isEmpty()) {
                _uiState.value = MatchingHomeUiState.HelperState(nearbyRequests = emptyList())
                return@onSuccess
            }

            val uiList = dtoList.map { dto ->
                // --- 📅 날짜 포맷팅 ---
                val zdt = try {
                    ZonedDateTime.parse(dto.scheduledAt)
                } catch (e: Exception) {
                    ZonedDateTime.now()
                }
                val today = ZonedDateTime.now().toLocalDate()
                val reqDate = zdt.toLocalDate()

                val dateLabelStr = when {
                    reqDate.isEqual(today) -> "오늘"
                    reqDate.isEqual(today.plusDays(1)) -> "내일"
                    else -> zdt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
                }

                val timeStr = zdt.format(DateTimeFormatter.ofPattern("H시 m분 출발", Locale.KOREA))

                val estimatedMin = dto.estimatedMinutes?.toLong() ?: 30L
                val arriveTimeStr = zdt.plusMinutes(estimatedMin)
                    .format(DateTimeFormatter.ofPattern("H시 m분 도착", Locale.KOREA))

                // --- 📏 거리 계산 ---
                val targetLat = dto.latitude ?: 0.0
                val targetLng = dto.longitude ?: 0.0

                val distanceMeters = calculateDistance(searchLat, searchLng, targetLat, targetLng)

                val distanceLabelStr = if (targetLat == 0.0 || targetLng == 0.0) {
                    "위치 정보 없음"
                } else if (distanceMeters < 1000) {
                    "내 위치에서 ${distanceMeters.toInt()}m"
                } else {
                    String.format("내 위치에서 %.1fkm", distanceMeters / 1000)
                }

                RequestUiModel(
                    id = dto.requestId,
                    dateLabel = dateLabelStr,
                    from = dto.startAddress ?: "출발지 미정",
                    to = dto.destinationAddress ?: "목적지 미정",
                    departTime = timeStr,
                    arriveTime = arriveTimeStr,
                    distanceLabel = distanceLabelStr,
                    startLat = targetLat,
                    startLng = targetLng,
                    endLat = 0.0,
                    endLng = 0.0
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