package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.remote.dto.CompanionRequestDetailDto
import com.kfpd_donghaeng_fe.data.repository.CompanionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanionRequestDetailViewModel @Inject constructor(
    private val repository: CompanionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<RequestDetailUiState>(RequestDetailUiState.Loading)
    val uiState: StateFlow<RequestDetailUiState> = _uiState.asStateFlow()

    // 상세 정보 로드
    fun loadDetail(requestId: Long) {
        viewModelScope.launch {
            _uiState.value = RequestDetailUiState.Loading
            repository.getRequestDetail(requestId)
                .onSuccess { data ->
                    _uiState.value = RequestDetailUiState.Success(data)
                }
                .onFailure { e ->
                    _uiState.value = RequestDetailUiState.Error(e.message ?: "오류 발생")
                }
        }
    }

    // 요청 수락 (매칭)
    fun acceptRequest(requestId: Long, onMatchSuccess: (Long) -> Unit) {
        viewModelScope.launch {
            repository.acceptRequest(requestId)
                .onSuccess { matchData ->
                    // ✅ 응답 데이터(matchData)에서 chatRoomId를 꺼내 콜백으로 전달
                    onMatchSuccess(matchData.chatRoomId)
                }
                .onFailure { e ->
                    _uiState.value = RequestDetailUiState.Error(e.message ?: "수락 실패")
                }
        }
    }
}

sealed class RequestDetailUiState {
    data object Loading : RequestDetailUiState()
    data class Success(val data: CompanionRequestDetailDto) : RequestDetailUiState()
    data class Error(val message: String) : RequestDetailUiState()
}