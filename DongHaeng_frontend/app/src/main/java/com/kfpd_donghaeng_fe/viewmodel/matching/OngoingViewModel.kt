package com.kfpd_donghaeng_fe.viewmodel.auth

import androidx.lifecycle.ViewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.StateFlow

import javax.inject.Inject

/**
 * 동행 진행 중 화면(TopSheet, BottomSheet)의 UI 상태를 정의합니다.
 * @param currentPage 현재 페이지(단계)를 나타냅니다. (0: 요청 접수, 1: 동행 중, 2: 완료 직전)
 */
data class OngoingUiState(
    val currentPage: Int = 0
)

/**
 * OngoingScreen의 상태 관리를 위한 ViewModel
 * Hilt를 사용하여 주입됩니다. (@HiltViewModel)
 */
@HiltViewModel
class OngoingViewModel @Inject constructor() : ViewModel() {

    // 1. UI 상태를 관리하는 'private'한 '변경 가능(Mutable)' 상자
    private val _uiState = MutableStateFlow(OngoingUiState(currentPage = 0))

    // 2. UI(Composable)가 'public'하게 관찰(collect)할 수 있는 '읽기 전용' 상자
    val uiState: StateFlow<OngoingUiState> = _uiState.asStateFlow()


    // --- 3. 이벤트 핸들러 (UI의 요청 처리) ---

    /** "동행 시작" 또는 "동행 종료(1단계)" 버튼 클릭 시 */
    fun goToNextStep() {
        // 현재 페이지가 2보다 작을 때만 (0 -> 1, 1 -> 2)
        if (_uiState.value.currentPage < 2) {
            _uiState.update { currentState ->
                currentState.copy(currentPage = currentState.currentPage + 1)
            }
        }
    }

    /** "동행 종료(2단계, 최종)" 버튼 클릭 시 */
    fun completeAccompany() {
        // 'uiState.value' 에 현재 상태(currentPage = 2)가 들어있습니다.
        val currentState = _uiState.value

        // TODO: 여기서 동행 완료 로직을
        //  data 레이어(Repository)를 통해 서버 API로 전송합니다.
        //  (지금은 UI 구현이 우선이므로 비워둡니다)

        // 예: Log.d("OngoingViewModel", "동행 완료! 상태: $currentState")
    }

    /** (참고) 이전 단계로 돌아가는 로직 (필요시 사용) */
    fun previousStep() {
        if (_uiState.value.currentPage > 0) {
            _uiState.update {
                it.copy(currentPage = it.currentPage - 1)
            }
        }
    }
}