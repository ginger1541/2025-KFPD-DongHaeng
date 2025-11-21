package com.kfpd_donghaeng_fe.viewmodel.matching.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.repository.MatchingRepository
import com.kfpd_donghaeng_fe.ui.auth.UserType
import com.kfpd_donghaeng_fe.ui.matching.home.MatchingHomeUiState
import com.kfpd_donghaeng_fe.ui.matching.home.RequestUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.lifecycle.SavedStateHandle

@HiltViewModel
class MatchingHomeViewModel @Inject constructor(
    private val matchingRepository: MatchingRepository
) : ViewModel() {

    // 실제 앱에선 로그인 / 프로필에서 받아오겠지?
    private val _userType = MutableStateFlow(UserType.NEEDY)
    val userType: StateFlow<UserType> = _userType.asStateFlow()

    private val _uiState = MutableStateFlow<MatchingHomeUiState>(MatchingHomeUiState.Loading)
    val uiState: StateFlow<MatchingHomeUiState> = _uiState.asStateFlow()

    init {
        // FIX: ViewModel 생성 시점에 기본 유형의 홈 데이터를 즉시 로드하도록 보장
        loadHomeData()
    }
    /**
     * 유저 타입 변경 (예: 설정에서 역할 바꾸는 경우 등)
     */
    fun setUserType(type: UserType) {
        if (_userType.value == type) return

        _userType.value = type
        loadHomeData()
    }

    /**
     * 홈 데이터 로드
     * - userType 에 따라 분기
     */
    private fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = MatchingHomeUiState.Loading

            try {
                when (_userType.value) {
                    UserType.NEEDY -> loadNeedyHome()
                    UserType.HELPER -> loadHelperHome()
                }
            } catch (e: Exception) {
                // TODO: 에러 메시지는 나중에 핸들링 규칙 정하면 거기에 맞춰 수정
                _uiState.value = MatchingHomeUiState.Error(
                    message = e.message ?: "알 수 없는 오류가 발생했어요."
                )
            }
        }
    }

    // =============================
    // NEEDY 홈: 최근 동행 내역
    // =============================

    private suspend fun loadNeedyHome() {
        // TODO: 나중에 실제 서버 연동 시:
        // val historyList = getRecentMatchingHistory(limit = 3)
        // val uiList = historyList.map { it.toUiModel() }

        // 지금은 목업 데이터
        val mock = listOf(
            RequestUiModel(
                id = 1L,
                dateLabel = "8월 13일",
                from = "서강대학교 인문대학 1호관",
                to = "루프 홍대점",
                departTime = "17시 10분 출발",
                arriveTime = "17시 30분 도착",
                distanceLabel = "내 위치에서 0.5km"
            ),
            RequestUiModel(
                id = 2L,
                dateLabel = "8월 14일",
                from = "서강대학교 인문대학 1호관",
                to = "루프 홍대점",
                departTime = "18시 00분 출발",
                arriveTime = "18시 20분 도착",
                distanceLabel = "내 위치에서 0.8km"
            ),
            RequestUiModel(
                id = 3L,
                dateLabel = "8월 15일",
                from = "서강대학교 정문",
                to = "공덕역 1번 출구",
                departTime = "19시 30분 출발",
                arriveTime = "19시 50분 도착",
                distanceLabel = "내 위치에서 1.2km"
            ),
        )

        _uiState.value = MatchingHomeUiState.NeedyState(
            recentTrips = mock.take(3)
        )
    }

    // =============================
    // HELPER 홈: 내 주변 요청
    // =============================

    private suspend fun loadHelperHome() {
        // TODO: 나중에 실제 서버 연동 시:
        // val requests = getNearbyRequests()
        // val uiList = requests.map { it.toUiModel() }

        // 지금은 목업 데이터
        val mock = listOf(
            RequestUiModel(
                id = 11L,
                dateLabel = "오늘",
                from = "서강대학교 인문대학 1호관",
                to = "루프 홍대점",
                departTime = "17시 10분 출발",
                arriveTime = "17시 30분 도착",
                distanceLabel = "내 위치에서 0.5km"
            ),
            RequestUiModel(
                id = 22L,
                dateLabel = "오늘",
                from = "서강대학교 정문",
                to = "광흥창역 4번 출구",
                departTime = "18시 00분 출발",
                arriveTime = "18시 20분 도착",
                distanceLabel = "내 위치에서 1.1km"
            ),
            RequestUiModel(
                id = 33L,
                dateLabel = "오늘",
                from = "마포구청역 2번 출구",
                to = "망원 한강공원 입구",
                departTime = "19시 00분 출발",
                arriveTime = "19시 25분 도착",
                distanceLabel = "내 위치에서 1.8km"
            )
        )

        _uiState.value = MatchingHomeUiState.HelperState(
            nearbyRequests = mock
        )
    }

    // =============================
    // 화면 상태 관리
    // =============================

    fun resetErrorState() {
        // ViewModelScope를 사용하지 않아도 되지만, suspend 함수를 호출할 경우 필요합니다.
        // 여기서는 단순히 상태만 변경합니다.
        _uiState.value = MatchingHomeUiState.Loading

        // 🚨 중요: 에러 상태를 리셋한 후, 반드시 데이터를 다시 불러오는 로직을 실행해야 합니다.
        // fetchInitialData() // 예시: 초기 데이터를 다시 불러오는 함수를 호출
    }
}