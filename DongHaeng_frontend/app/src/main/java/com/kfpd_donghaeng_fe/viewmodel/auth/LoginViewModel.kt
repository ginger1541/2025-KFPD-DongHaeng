package com.kfpd_donghaeng_fe.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val checkCanLoginUseCase: LoginUseCase
): ViewModel()
{
    // 내부용 (수정 가능)
    private val _uiState = MutableStateFlow(LoginAccountUiState(currentPage = 0))
    // 외부용 (읽기 전용)
    val uiState = _uiState.asStateFlow()

    private val _loginEvent = MutableSharedFlow<Boolean>()
    val loginEvent: SharedFlow<Boolean> = _loginEvent.asSharedFlow()

    fun login() {
        viewModelScope.launch {
            val current = _uiState.value.currentPage
            if (current<=1) {
                _uiState.update { currentState ->
                    currentState.copy(currentPage = currentState.currentPage + 1)
                }
            }
        }
    }

    fun MovetoMain(){
        viewModelScope.launch {
            try { // 👈 여기에 try 블록을 시작하고
                val canLogin = checkCanLoginUseCase("requester@test.com", "test1234")

                if (canLogin.success) {
                    // 성공 로직
                    Log.e("Login", "로그인 성공!")
                    _loginEvent.emit(true)
                } else {
                    // 실패 로직
                    Log.e("Login", "로그인 실패!")
                }
            } catch (e: Exception) { // 👈 여기에 catch 블록을 추가해야 합니다.
                // 앱이 꺼지지 않고 여기서 멈춥니다.
                Log.e("LOGIN_ERROR", "로그인 과정 중 예외 발생: ${e.message}", e) // 👈 여기서 실제 오류를 확인
            }
        }
    }
    fun MovetoMakeAccount(){
        val current = _uiState.value.currentPage
        _uiState.update { currentState ->
            currentState.copy(currentPage = currentState.currentPage + 2)
        }
    }
}















