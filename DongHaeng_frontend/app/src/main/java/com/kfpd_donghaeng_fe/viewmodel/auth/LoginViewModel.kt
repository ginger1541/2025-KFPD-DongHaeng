package com.kfpd_donghaeng_fe.viewmodel.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.domain.usecase.LoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


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
            val canLogin = checkCanLoginUseCase()
            if (canLogin) {Log.d("LOGIN_CHECK", "로그인 가능 상태입니다. 다음 화면으로 이동합니다.")
            }else{Log.w("LOGIN_CHECK", "로그인 불가 상태입니다. 오류 메시지를 출력합니다.")}

        }
    }
    fun MovetoMakeAccount(){
        val current = _uiState.value.currentPage
        _uiState.update { currentState ->
            currentState.copy(currentPage = currentState.currentPage + 2)
        }
    }
}















