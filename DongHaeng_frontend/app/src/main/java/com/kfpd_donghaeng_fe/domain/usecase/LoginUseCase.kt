package com.kfpd_donghaeng_fe.domain.usecase

import jakarta.inject.Inject

class LoginUseCase  @Inject constructor() {
    operator fun invoke(currentPage: Int): Boolean {
        // TODO: 로그인 확인 로직
        return true
    }
}