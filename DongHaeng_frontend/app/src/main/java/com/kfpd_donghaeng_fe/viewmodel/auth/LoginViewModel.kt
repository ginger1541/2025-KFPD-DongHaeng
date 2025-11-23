package com.kfpd_donghaeng_fe.viewmodel.auth

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
            val canGoNext = checkCanLoginUseCase(current)  // 이제 suspend 호출 OK

            if (canGoNext) {
                _uiState.update { currentState ->
                    currentState.copy(currentPage = currentState.currentPage + 1)
                }
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















