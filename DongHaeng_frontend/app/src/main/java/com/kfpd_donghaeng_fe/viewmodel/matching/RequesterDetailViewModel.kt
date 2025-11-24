package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.domain.repository.RequestRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RequesterDetailViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    // UI 상태 (로딩된 요청 정보)
    private val _uiState = MutableStateFlow<Request?>(null)
    val uiState: StateFlow<Request?> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    // 요청 ID로 정보 불러오기
    fun loadRequest(requestId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // Repository를 통해 서버에서 데이터 가져오기
                val request = repository.getRequestById(requestId)
                _uiState.value = request
            } catch (e: Exception) {
                e.printStackTrace()
                // 에러 처리 (필요 시 에러 상태 추가 가능)
            } finally {
                _isLoading.value = false
            }
        }
    }
}