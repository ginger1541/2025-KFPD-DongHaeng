package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.LocationType
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.toRouteLocation
import com.kfpd_donghaeng_fe.domain.repository.HistoryRepository
import com.kfpd_donghaeng_fe.domain.usecase.SearchPlaceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class PlaceSearchViewModel @Inject constructor(
    private val searchPlaceUseCase: SearchPlaceUseCase,
    private val historyRepository: HistoryRepository
) : ViewModel() {
    private val _isSelectingStart = MutableStateFlow(true)
    val isSelectingStart: StateFlow<Boolean> = _isSelectingStart.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PlaceSearchResult>>(emptyList())
    val searchResults: StateFlow<List<PlaceSearchResult>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _startLocation = MutableStateFlow<RouteLocation?>(null)
    val startLocation: StateFlow<RouteLocation?> = _startLocation.asStateFlow()

    private val _endLocation = MutableStateFlow<RouteLocation?>(null)
    val endLocation: StateFlow<RouteLocation?> = _endLocation.asStateFlow()

    // 현재 선택/검색 중인 장소가 출발지인지 도착지인지
    private val _searchHistories = MutableStateFlow<List<PlaceSearchResult>>(emptyList())
    val searchHistories: StateFlow<List<PlaceSearchResult>> = _searchHistories.asStateFlow()

    init {
        // 1. Debounce 적용: 300ms 대기 후 검색
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    searchPlaces(query)
                }
        }

        // 2. 💾 DataStore에서 검색 기록 로드 및 StateFlow에 연결
        viewModelScope.launch {
            historyRepository.searchHistoriesFlow.collect { histories ->
                _searchHistories.value = histories
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    private fun searchPlaces(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // UseCase를 통해 검색 수행
                val result = searchPlaceUseCase(query)
                result.onSuccess { places ->
                    _searchResults.value = places
                }.onFailure { error ->
                    error.printStackTrace()
                    _searchResults.value = emptyList()
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addToHistory(place: PlaceSearchResult) {
        val currentHistories = _searchHistories.value.toMutableList()

        // 1. 중복 제거
        currentHistories.removeAll { it.placeName == place.placeName }

        // 2. 최신 항목을 맨 앞에 추가
        currentHistories.add(0, place)

        // 3. 최대 10개만 유지
        if (currentHistories.size > 10) {
            currentHistories.removeAt(currentHistories.size - 1)
        }

        // 4. 화면 상태 업데이트 (즉시 반영)
        _searchHistories.value = currentHistories

        // 5. 💾 [핵심 추가] DataStore에 영구 저장!
        viewModelScope.launch {
            historyRepository.saveHistories(currentHistories)
        }
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }

    fun selectPlace(place: PlaceSearchResult) {
        addToHistory(place) // 히스토리에 추가

        val type = if (_isSelectingStart.value) LocationType.START else LocationType.END
        val routeLocation = place.toRouteLocation(type)

        if (_isSelectingStart.value) {
            _startLocation.value = routeLocation
        } else {
            _endLocation.value = routeLocation
        }

        // 선택 후 검색 상태 초기화
        clearSearchQuery()
    }

    // 현재 검색/선택 중인 대상을 변경하는 함수 (UI 버튼 클릭 등에 사용)
    fun setSelectingTarget(isStart: Boolean) {
        _isSelectingStart.value = isStart
    }

    // 출발지와 도착지가 모두 선택되었는지 확인하는 Computed Property (옵션)
    val isReadyForRoute: Boolean
        get() = _startLocation.value != null && _endLocation.value != null
}