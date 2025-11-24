package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.auth.UserType
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingRequestEntity
// 💡 누락된 Flow 관련 Import 추가
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// -----------------------------------------------------------
// 1. 일회성 내비게이션 이벤트를 위한 Sealed Class 정의
// -----------------------------------------------------------
sealed class OngoingUiEvent {
    object NavigateToReview : OngoingUiEvent()
    // object ShowErrorMessage : OngoingUiEvent()
}

// -----------------------------------------------------------
// 2. OngoingViewModel 클래스를 단일 정의
// -----------------------------------------------------------
class OngoingViewModel : ViewModel() {

    // A. UI 상태 (State) 관리 (화면 렌더링을 위한 데이터)
    private val _uiState = MutableStateFlow(OngoingEntity())
    val uiState = _uiState.asStateFlow()

    private val _uiState2 = MutableStateFlow(OngoingRequestEntity())
    val uiState2 = _uiState2.asStateFlow()

    // B. 일회성 이벤트 (Event) 관리 (화면 전환, Snackbar 표시 등)
    private val _eventFlow = MutableSharedFlow<OngoingUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()


    // --- UI State 변경 함수 ---

    fun nextPage() {
        if (_uiState.value.OngoingPage < 2) {
            _uiState.update { it.copy(OngoingPage = it.OngoingPage + 1) }
        }
    }

    fun previousPage() {
        if (_uiState.value.OngoingPage > 0) {
            _uiState.update { it.copy(OngoingPage = it.OngoingPage - 1) }
        }
    }



    // --- One-shot Event 발행 함수 ---

    /**
     * 리뷰 화면으로 이동 요청 이벤트를 발행합니다.
     * 이 함수는 Route/Screen에서 구독됩니다.
     */
    fun NavigateToReview() {
        viewModelScope.launch {
            _eventFlow.emit(OngoingUiEvent.NavigateToReview)
        }
    }

    // 💡 참고: 기존 NavigateToReview 함수는 이벤트 발행 로직과 중복되므로 제거하거나 이름을 변경해야 합니다.
    // fun NavigateToReview(){
    //     viewModelScope.launch {
    //         _navigationEvent.emit("review") // _navigationEvent 미정의 오류 발생 지점
    //     }
    // }
}


