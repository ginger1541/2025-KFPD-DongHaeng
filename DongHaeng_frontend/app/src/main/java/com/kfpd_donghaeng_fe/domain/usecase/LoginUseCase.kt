package com.kfpd_donghaeng_fe.domain.usecase

import com.kfpd_donghaeng_fe.domain.entity.auth.LoginResultEntity
import com.kfpd_donghaeng_fe.domain.repository.LoginRepository
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository // 👈 다시 주입받습니다!
) {
    suspend operator fun invoke(email: String, password: String): LoginResultEntity {
        // 💡 Repository의 attemptLogin 함수를 호출합니다.
        return loginRepository.attemptLogin(email, password)
    }
}

class LoingingcheckUseCase @Inject constructor(
    private val loginRepository : LoginRepository
){
    // 앱 시작 시 호출되어 로그인 여부를 확인합니다.
    suspend operator fun invoke(): Boolean {
        return loginRepository.isLoggedIn() }}