package com.kfpd_donghaeng_fe.viewmodel.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.remote.dto.MatchItemResponse
import com.kfpd_donghaeng_fe.data.repository.MatchRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val matchRepository: MatchRepositoryImpl
) : ViewModel() {

    private val _activeMatches = MutableStateFlow<List<MatchItemResponse>>(emptyList())
    val activeMatches: StateFlow<List<MatchItemResponse>> = _activeMatches.asStateFlow()

    private val _pastMatches = MutableStateFlow<List<MatchItemResponse>>(emptyList())
    val pastMatches: StateFlow<List<MatchItemResponse>> = _pastMatches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        fetchMatches()
    }

    fun fetchMatches() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = matchRepository.getMyMatches()
                result.onSuccess { response ->
                    val allMatches = response.data.asHelper + response.data.asRequester

                    // ✅ 예약중: 날짜(scheduledAt)가 가까운 순 정렬
                    _activeMatches.value = allMatches.filter {
                        it.status == "pending" || it.status == "in_progress" || it.status == "accepted"
                    }.sortedBy { it.request.scheduledAt }

                    // ✅ 완료/취소: 날짜(scheduledAt)가 최신인 순 정렬
                    _pastMatches.value = allMatches.filter {
                        it.status == "completed" || it.status == "cancelled"
                    }.sortedByDescending { it.request.scheduledAt }

                }.onFailure { e ->
                    Log.e("ScheduleVM", "API Error", e)
                }
            } catch (e: Exception) {
                Log.e("ScheduleVM", "Network Error", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}