package com.kfpd_donghaeng_fe.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.kfpd_donghaeng_fe.ui.auth.SignUpUiState
import com.kfpd_donghaeng_fe.ui.auth.UserType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignUpViewModel : ViewModel() {

    // 1. UI 상태를 관리하는 'private'한 '변경 가능(Mutable)' 상자
    // _uiState는 ViewModel 내부에서만 수정할 수 있습니다.
    // SignUpUiState(currentPage = 1) : 1페이지부터 시작하도록 초기값 설정
    private val _uiState = MutableStateFlow(SignUpUiState(currentPage = 1))

    // 2. UI(Composable)가 'public'하게 관찰(collect)할 수 있는 '읽기 전용' 상자
    // .asStateFlow()를 붙여서 외부(UI)에서는 이 값을 읽기만 하고 수정은 못하게 막습니다.
    val uiState = _uiState.asStateFlow()


    // --- 3. 이벤트 핸들러 (UI의 요청 처리) ---
    // UI(Composable)에서 "다음 버튼 눌렀어!" 같은 이벤트가 발생하면
    // 이 함수들이 호출되어 _uiState의 값을 'update' 합니다.

    /** "다음" 버튼 클릭 시 */
    fun nextPage() {
        // 현재 페이지가 4보다 작을 때만
        if (_uiState.value.currentPage < 4) {
            // update { ... } 안의 'it'은 현재 UiState 값입니다.
            // it.copy(...)는 'it'(현재 상태)을 복사하되,
            // 괄호 안의 값(currentPage)만 변경해서 새 UiState 객체를 만듭니다.
            _uiState.update { currentState ->
                currentState.copy(currentPage = currentState.currentPage + 1)
            }
        }
    }

    /** "뒤로" 가기 (상단바에서 처리) */
    fun previousPage() {
        if (_uiState.value.currentPage > 1) {
            _uiState.update {
                it.copy(currentPage = it.currentPage - 1)
            }
        }
    }

    /** 닉네임 입력 시 */
    fun onNicknameChange(nickname: String) {
        _uiState.update {
            it.copy(nickname = nickname)
        }
    }

    /** 자기소개 입력 시 (100자 제한) */
    fun onBioChange(bio: String) {
        if (bio.length <= 100) {
            _uiState.update {
                it.copy(bio = bio)
            }
        }
    }

    /** 사용자 유형 선택 시 */
    fun onUserTypeSelect(userType: UserType) {
        _uiState.update {
            it.copy(userType = userType)
        }
    }

    /** "약관 전체 동의" 체크박스 클릭 시 */
    fun onAllAgreeChange(isChecked: Boolean) {
        _uiState.update {
            it.copy(
                termsAllAgreed = isChecked,
                termServiceAgreed = isChecked,
                termPrivacyAgreed = isChecked,
                termLocationAgreed = isChecked
            )
        }
    }

    /** "[필수] 서비스 이용약관" 체크박스 클릭 시 */
    fun onServiceAgreeChange(isChecked: Boolean) {
        _uiState.update { currentState ->
            // 개별 체크 변경 후, '전체 동의' 상태도 업데이트
            val allRequiredAgreed = isChecked &&
                    currentState.termPrivacyAgreed &&
                    currentState.termLocationAgreed
            currentState.copy(
                termServiceAgreed = isChecked,
                termsAllAgreed = allRequiredAgreed
            )
        }
    }

    /** "[필수] 개인정보 처리방침" 체크박스 클릭 시 */
    fun onPrivacyAgreeChange(isChecked: Boolean) {
        _uiState.update { currentState ->
            val allRequiredAgreed = currentState.termServiceAgreed &&
                    isChecked &&
                    currentState.termLocationAgreed
            currentState.copy(
                termPrivacyAgreed = isChecked,
                termsAllAgreed = allRequiredAgreed
            )
        }
    }

    /** "[필수] 복지정보 이용약관" 체크박스 클릭 시 */
    fun onLocationAgreeChange(isChecked: Boolean) {
        _uiState.update { currentState ->
            val allRequiredAgreed = currentState.termServiceAgreed &&
                    currentState.termPrivacyAgreed &&
                    isChecked
            currentState.copy(
                termLocationAgreed = isChecked,
                termsAllAgreed = allRequiredAgreed
            )
        }
    }

    /** "가입 완료하기" 버튼 클릭 시 */
    fun submitSignUp() {
        // 'uiState.value' 에 닉네임, 자기소개, 유형 등 모든 정보가 들어있습니다.
        val currentState = _uiState.value

        // TODO: 여기서 currentState에 담긴 정보(nickname, bio, userType 등)를
        //  data 레이어(Repository)를 통해 서버 API로 전송합니다.
        //  (지금은 UI 구현이 우선이므로 비워둡니다)

        // 예: Log.d("SignUpViewModel", "가입 정보: $currentState")
    }
}