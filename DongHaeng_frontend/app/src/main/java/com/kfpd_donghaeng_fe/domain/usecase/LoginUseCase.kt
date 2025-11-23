package com.kfpd_donghaeng_fe.domain.usecase

import com.kfpd_donghaeng_fe.domain.repository.LoginRepository
import jakarta.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository // 👈 다시 주입받습니다!
) {
    suspend operator fun invoke(currentPage: Int): Boolean {
        // 예시: 페이지가 마지막 단계라면 실제 로그인 요청
        // 현재 로직에 맞춰 수정이 필요할 수 있지만, 일단 연결부터 합니다.
        return loginRepository.isLoggedIn()
    }
}