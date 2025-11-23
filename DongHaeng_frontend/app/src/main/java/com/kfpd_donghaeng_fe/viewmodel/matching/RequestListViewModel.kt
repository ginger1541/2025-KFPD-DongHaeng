package com.kfpd_donghaeng_fe.viewmodel.matching

import android.app.DownloadManager
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import com.kfpd_donghaeng_fe.domain.repository.RequestRepository
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
/*
class RequestListViewModel @Inject constructor(
    private val repository: RequestRepository
) : ViewModel() {

    // 1. UI가 관찰할 상태를 StateFlow로 정의
    private val _requests = MutableStateFlow<List<DownloadManager.Request>>(emptyList())
    val requests: StateFlow<List<DownloadManager.Request>> = _requests.asStateFlow()

    // 2. 데이터 로딩 상태 (로딩 중, 성공, 에러) 관리
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading.asStateFlow()

    init {
        loadRequests()
    }

    fun loadRequests() {
        viewModelScope.launch {
            _loading.value = true
            try {
                // 3. Repository 호출 (데이터 계층과 통신)
                val result = repository.getRequestList()
                _requests.value = result
            } catch (e: Exception) {
                // 4. 오류 처리
                Log.e("RequestListViewModel", "Failed to load requests", e)
                _requests.value = emptyList()
            } finally {
                _loading.value = false
            }
        }
    }
}*/