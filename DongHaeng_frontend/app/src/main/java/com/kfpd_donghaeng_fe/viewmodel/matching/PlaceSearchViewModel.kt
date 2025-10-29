package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
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
    private val searchPlaceUseCase: SearchPlaceUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<PlaceSearchResult>>(emptyList())
    val searchResults: StateFlow<List<PlaceSearchResult>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchHistories = MutableStateFlow<List<PlaceSearchResult>>(emptyList())
    val searchHistories: StateFlow<List<PlaceSearchResult>> = _searchHistories.asStateFlow()

    init {
        // Debounce 적용: 300ms 대기 후 검색
        viewModelScope.launch {
            _searchQuery
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    searchPlaces(query)
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
        // 중복 제거
        currentHistories.removeAll { it.placeName == place.placeName }
        // 최신 항목을 맨 앞에 추가
        currentHistories.add(0, place)
        // 최대 10개만 유지
        if (currentHistories.size > 10) {
            currentHistories.removeAt(currentHistories.size - 1)
        }
        _searchHistories.value = currentHistories
    }

    fun clearSearchQuery() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
    }
}