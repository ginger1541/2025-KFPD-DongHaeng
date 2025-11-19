package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.repository.RouteRepository // <-- 인터페이스로 변경
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 지도 화면의 UI 상태를 정의하는 데이터 클래스.
 * 모든 지도 관련 데이터를 Compose UI에 제공합니다.
 */
data class MapUiState(
    val centerLocation: RouteLocation? = null,         // 지도의 중심 좌표 (예: 현재 사용자 위치)
    val userLocations: List<RouteLocation> = emptyList(), // 근처 사용자 및 마커 위치 리스트
    val route: WalkingRoute? = null,                  // SK API에서 받아온 폴리라인 경로 데이터
    val isLoading: Boolean = false,
    val error: String? = null
)

/**
 * 지도 화면의 비즈니스 로직과 데이터 흐름을 관리하는 ViewModel.
 * RouteRepository 인터페이스에 의존합니다.
 */
class MapViewModel(
    private val routeRepository: RouteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState: StateFlow<MapUiState> = _uiState.asStateFlow()

    /**
     * 지도를 초기화하고 초기 위치 및 근처 사용자 마커를 설정합니다.
     * * @param currentLocation 지도의 초기 중심이 될 위치 (보통 현재 사용자 위치)
     * @param nearbyUsers 지도에 마커로 표시할 근처 사용자 목록
     */
    fun initializeMap(currentLocation: RouteLocation, nearbyUsers: List<RouteLocation>) {
        _uiState.update {
            it.copy(
                centerLocation = currentLocation,
                // 현재 위치와 근처 사용자 마커를 합쳐서 관리
                userLocations = listOf(currentLocation) + nearbyUsers
            )
        }
    }

    /**
     * SK API를 사용하여 출발지와 도착지 사이의 보행자 경로를 요청합니다.
     *
     * @param start 출발지 위치 정보
     * @param end 도착지 위치 정보
     */
    fun requestWalkingRoute(start: RouteLocation, end: RouteLocation) {
        viewModelScope.launch {
            // 경로 요청 시작: 로딩 상태 설정 및 에러 초기화
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = routeRepository.fetchWalkingRoute(start, end)

            _uiState.update { currentState ->
                result.fold(
                    onSuccess = { walkingRoute ->
                        // 성공: 경로 업데이트 및 로딩 종료
                        currentState.copy(
                            route = walkingRoute,
                            isLoading = false
                        )
                    },
                    onFailure = { error ->
                        // 실패: 경로 제거, 에러 메시지 설정 및 로딩 종료
                        currentState.copy(
                            route = null,
                            isLoading = false,
                            error = error.message ?: "경로를 가져오는 데 실패했습니다."
                        )
                    }
                )
            }
        }
    }

    /**
     * 지도에서 현재 표시 중인 경로를 제거합니다.
     */
    fun clearRoute() {
        _uiState.update {
            it.copy(route = null)
        }
    }
}