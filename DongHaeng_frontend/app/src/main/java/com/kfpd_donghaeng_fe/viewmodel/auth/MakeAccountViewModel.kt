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
    val selectedUserType : UserType?=null,
    var userInfoUiState: UserInfoUiState = UserInfoUiState()


)


// 사용자 유형을 나타내는 enum 클래스
enum class UserType {
    NEEDY, // 도움이 필요해요
    HELPER // 도움을 드릴게요
}

// MakeAccountUiState.kt 파일 (또는 ViewModel 파일 상단)

// UserInfoUiState.kt 파일 (또는 ViewModel 파일 상단)
data class UserInfoUiState(
    val userId: String = "",
    val userIdError: String? = null,
    val password: String = "",
    val passwordConfirm: String = "",
    val passwordError: String? = null,
    val gender: String? = null,
    val phoneNumber: String = "",
    val phoneValidationMessage: String? = null,
    val birthDate: String = "YYYY / MM / DD",
    val showPassword: Boolean = false,
    val showPasswordConfirm: Boolean = false,

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

    fun updateUserType(type: UserType) {
        _uiState.update { currentState ->
            // ⭐ 이 부분이 선택된 유저 타입을 업데이트하는 핵심입니다.
            currentState.copy(userType = type) // UiState의 필드명(userType)을 사용해야 합니다.
        }
    }

    // 참고: 이전 페이지는 보통 검사가 필요 없어서 ViewModel에서 바로 처리하기도 함
    fun previousPage() {
        if (_uiState.value.currentPage > 0) {
            _uiState.update { it.copy(currentPage = it.currentPage - 1) }
        }
    }

    //--------------


    fun updateUserId(newId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                userInfoUiState = currentState.userInfoUiState.copy(userId = newId)
            )
        }
    }

    fun updatePassword(newPw: String) {
        _uiState.update { currentState ->
            val newUserInfoState = currentState.userInfoUiState.copy(password = newPw)
            // 비밀번호 변경 시, 확인 비밀번호와 즉시 비교하여 오류 상태 업데이트
            val updatedErrorState = validatePasswords(newUserInfoState)
            currentState.copy(userInfoUiState = updatedErrorState)
        }
    }

    fun updatePasswordConfirm(newPwC: String) {
        _uiState.update { currentState ->
            val newUserInfoState = currentState.userInfoUiState.copy(passwordConfirm = newPwC)
            // 확인 비밀번호 변경 시, 원본 비밀번호와 즉시 비교하여 오류 상태 업데이트
            val updatedErrorState = validatePasswords(newUserInfoState)
            currentState.copy(userInfoUiState = updatedErrorState)
        }
    }

    // ⭐ 비밀번호 일치 검증 로직 (ViewModel 내부에 정의)
    private fun validatePasswords(state: UserInfoUiState): UserInfoUiState {
        val pw = state.password
        val pwConfirm = state.passwordConfirm

        val error = if (pw.isNotEmpty() && pwConfirm.isNotEmpty() && pw != pwConfirm) {
            "비밀번호가 일치하지 않습니다."
        } else {
            null
        }
        return state.copy(passwordError = error)
    }

    // ----------------------------------------------------
    // 3. 다른 필드 업데이트 로직
    // ----------------------------------------------------



    fun updateGender(newGender: String) {
        _uiState.update {
            it.copy(userInfoUiState = it.userInfoUiState.copy(gender = newGender))
        }
    }

    fun updatePhoneNumber(newPhone: String) {
        _uiState.update {
            it.copy(userInfoUiState = it.userInfoUiState.copy(phoneNumber = newPhone))
        }
    }

    fun updateBirthDate(newDate: String) {
        _uiState.update {
            it.copy(userInfoUiState = it.userInfoUiState.copy(birthDate = newDate))
        }
    }

    // ----------------------------------------------------
    // 4. UI 상호작용 로직 (비밀번호 가시성 토글)
    // ----------------------------------------------------

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(userInfoUiState = it.userInfoUiState.copy(
                showPassword = !it.userInfoUiState.showPassword
            ))
        }
    }

    fun togglePasswordConfirmVisibility() {
        _uiState.update {
            it.copy(userInfoUiState = it.userInfoUiState.copy(
                showPasswordConfirm = !it.userInfoUiState.showPasswordConfirm
            ))
        }
    }



}

// UI 상태 데이터 클래스




