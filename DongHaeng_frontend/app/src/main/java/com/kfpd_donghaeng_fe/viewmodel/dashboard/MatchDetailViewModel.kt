package com.kfpd_donghaeng_fe.viewmodel.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.remote.dto.MatchDetailDTO
import com.kfpd_donghaeng_fe.data.remote.dto.MatchUserDTO
import com.kfpd_donghaeng_fe.data.repository.MatchRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MatchDetailViewModel @Inject constructor(
    private val matchRepository: MatchRepositoryImpl
) : ViewModel() {

    private val _matchDetail = MutableStateFlow<MatchDetailDTO?>(null)
    val matchDetail: StateFlow<MatchDetailDTO?> = _matchDetail.asStateFlow()

    private val _partnerInfo = MutableStateFlow<MatchUserDTO?>(null)
    val partnerInfo: StateFlow<MatchUserDTO?> = _partnerInfo.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // 상세 정보 로드
    fun loadMatchDetail(matchId: Long, myUserId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            matchRepository.getMatchDetail(matchId)
                .onSuccess { data ->
                    _matchDetail.value = data

                    // 상대방 정보 설정 로직
                    // 내가 요청자(requester)라면 파트너는 helper, 반대라면 requester
                    if (data.requester.id == myUserId) {
                        _partnerInfo.value = data.helper
                    } else {
                        _partnerInfo.value = data.requester
                    }
                }
                .onFailure { e ->
                    _error.value = e.message
                }

            _isLoading.value = false
        }
    }
}