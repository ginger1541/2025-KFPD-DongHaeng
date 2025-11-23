package com.kfpd_donghaeng_fe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
import com.kfpd_donghaeng_fe.domain.repository.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val loginRepository: LoginRepository,
    private val tokenDataSource: TokenLocalDataSource
) : ViewModel() {

    private val _startDestination = MutableStateFlow<String?>(null)
    val startDestination = _startDestination.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            // 🗑️ [테스트용] 앱 켤 때마다 저장된 토큰 삭제 (로그아웃 효과)
            tokenDataSource.deleteToken()

            // 로고 노출을 위한 지연 (1초)
            delay(1000)

            val isLoggedIn = loginRepository.isLoggedIn()

            if (isLoggedIn) {
                // ✅ [추가 구현] 저장된 유저 타입 가져오기
                val savedType = tokenDataSource.getUserType()

                if (savedType == "HELPER") {
                    _startDestination.value = "home/HELPER"
                } else {
                    // "NEEDY" 이거나 값이 없을 경우 기본값
                    _startDestination.value = "home/NEEDY"
                }
            } else {
                _startDestination.value = "splash"
            }
        }
    }
}