package com.kfpd_donghaeng_fe.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.kfpd_donghaeng_fe.domain.usecase.MakeAccountUseCase
import com.kfpd_donghaeng_fe.ui.auth.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//임시 데이터 구조
data class MakeAccountUiState(
    val currentPage: Int = 0,          // 페이지 번호
    val nickname: String = "",         // 닉네임
    val bio: String = "",              // 자기소개
    val userType: UserType? = null,    // 유저 타입
    val termsAllAgreed: Boolean = false,    // 전체 동의
    val termServiceAgreed: Boolean = false, // 서비스 동의
    val termPrivacyAgreed: Boolean = false, // 개인정보 동의
    val termLocationAgreed: Boolean = false // 위치정보 동의
)



@HiltViewModel // Hilt를 쓴다면 필수 어노테이션
class MakeAccountViewModel @Inject constructor(
    // 1. 심판관(UseCase)을 데려옵니다.
    private val checkCanMoveToNextPageUseCase: MakeAccountUseCase
) : ViewModel() {

    // 내부용 (수정 가능)
    private val _uiState = MutableStateFlow(MakeAccountUiState(currentPage = 0))
    // 외부용 (읽기 전용)
    val uiState = _uiState.asStateFlow()

    /** "다음" 버튼 클릭 시 호출 */
    fun nextPage() {
        val current = _uiState.value.currentPage

        // 2. UseCase에게 "넘어가도 돼?"라고 물어봅니다. (비즈니스 로직)
        val canGoNext = checkCanMoveToNextPageUseCase(current)

        if (canGoNext && current < 8) {
            // 3. 허락받았으면 상태 업데이트 (UI 로직)
            _uiState.update { currentState ->
                currentState.copy(currentPage = currentState.currentPage + 1)
            }
        } else {
            // 못 넘어가면 에러 메시지 표시 등을 처리
        }
    }

    // 참고: 이전 페이지는 보통 검사가 필요 없어서 ViewModel에서 바로 처리하기도 함
    fun previousPage() {
        if (_uiState.value.currentPage > 0) {
            _uiState.update { it.copy(currentPage = it.currentPage - 1) }
        }
    }
}

// UI 상태 데이터 클래스




