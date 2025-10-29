package com.kfpd_donghaeng_fe.ui.matching

enum class MatchingPhase {
    OVERVIEW,      // 초기 화면
    BOOKING,       // 예약 상세 입력 (경로 입력)
    TIME_SELECTION, // 💡 [추가] 시간 입력 화면
    CONFIRM        // 경로 확인 (최종 요청 전)
}