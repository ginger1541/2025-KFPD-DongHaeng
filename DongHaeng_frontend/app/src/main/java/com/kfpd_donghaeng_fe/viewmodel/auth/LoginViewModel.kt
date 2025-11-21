package com.kfpd_donghaeng_fe.viewmodel.auth

import androidx.lifecycle.ViewModel
import com.kfpd_donghaeng_fe.domain.usecase.LoginUseCase
import com.kfpd_donghaeng_fe.ui.auth.UserType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

//임시 데이터 구조
data class LoginAccountUiState(
    val currentPage: Int = 0,          // 페이지 번호 0 : 로그인 전 :1 로그인 후 메인 화면 진입
    val id: String = "",  //아이디
    val pw:String="", // 비번
    val userType: UserType? = null,    // 유저 타입

)


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val checkCanLoginUseCase: LoginUseCase
): ViewModel()
{
    // 내부용 (수정 가능)
    private val _uiState = MutableStateFlow(LoginAccountUiState(currentPage = 0))
    // 외부용 (읽기 전용)
    val uiState = _uiState.asStateFlow()
    fun login() {
        val current = _uiState.value.currentPage
        // 2. 로그인 가능 ? 시 다음 페이지 넘어가기 //TODO : 일단 all true로 적용 해놓음
        val canGoNext = checkCanLoginUseCase(current)
        if (canGoNext) {
            _uiState.update { currentState ->
                currentState.copy(currentPage = currentState.currentPage + 1)
            }
        }}
    fun MovetoMakeAccount(){
        val current = _uiState.value.currentPage
        _uiState.update { currentState ->
            currentState.copy(currentPage = currentState.currentPage + 2)
        }
    }
}















