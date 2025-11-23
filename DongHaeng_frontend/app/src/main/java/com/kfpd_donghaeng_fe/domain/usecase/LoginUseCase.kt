package com.kfpd_donghaeng_fe.domain.usecase

import jakarta.inject.Inject

// 생성자에서 LoginRepository를 제거합니다.
class LoginUseCase @Inject constructor() {

    // ViewModel에서 current를 넘겨주고 있으므로 파라미터는 유지합니다.
    suspend operator fun invoke(currentPage: Int): Boolean {

        // 리포지토리 없이 그냥 무조건 "통과(true)"라고 거짓말을 합니다.
        // 나중에 실제 로직이 필요할 때 다시 리포지토리를 연결하면 됩니다.
        return true
    }
}