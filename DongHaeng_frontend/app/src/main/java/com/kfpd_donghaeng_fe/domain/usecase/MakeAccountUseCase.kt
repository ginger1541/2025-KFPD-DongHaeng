package com.kfpd_donghaeng_fe.domain.usecase

import jakarta.inject.Inject

class MakeAccountUseCase @Inject constructor() {
    /**
     * @param currentPage 현재 페이지 번호
     * @return 다음으로 넘어갈 수 있으면 true, 없으면 false
     */
    operator fun invoke(currentPage: Int): Boolean {
        // TODO: 나중에는 여기서 페이지별 유효성 검사 로직을 넣습니다.
        // 예: if (currentPage == 0 && id.isEmpty()) return false

        // 지금은 무조건 통과!
        return true
    }

}