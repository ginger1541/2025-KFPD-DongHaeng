package com.kfpd_donghaeng_fe.viewmodel.matching
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class OngoingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(OngoingEntity())
    val uiState = _uiState.asStateFlow()

    fun nextPage() {
        if (_uiState.value.OngoingPage < 2) {
            _uiState.update { it.copy(OngoingPage = it.OngoingPage + 1) }
        }
    }

    fun NavigateToReview(){
        viewModelScope.launch {
            _navigationEvent.emit("review")
        }

    }


    fun previousPage() {
        if (_uiState.value.OngoingPage > 0) {
            _uiState.update { it.copy(OngoingPage = it.OngoingPage - 1) }
        }
    }


}


// 1. ViewModel에서 발생시킬 이벤트 클래스 정의
sealed class OngoingUiEvent {
    object NavigateToReview : OngoingUiEvent()
    // object ShowErrorMessage : OngoingUiEvent() // 다른 이벤트도 여기에 추가 가능
}

class OngoingViewModel : ViewModel() {

    // 2. 일회성 이벤트를 위한 Private MutableSharedFlow
    private val _eventFlow = MutableSharedFlow<OngoingUiEvent>()
    val eventFlow = _eventFlow.asSharedFlow() // UI에서 수집할 Flow

    // 3. ViewModel의 퍼블릭 함수: 이벤트 발행
    fun onNavigateToReviewClick() {
        viewModelScope.launch {
            _eventFlow.emit(OngoingUiEvent.NavigateToReview)
        }
    }
}



