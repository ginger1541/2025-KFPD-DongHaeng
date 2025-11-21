package com.kfpd_donghaeng_fe.ui.matching

enum class MatchingPhase {
    OVERVIEW,       // 초기 화면 (Map + 예상 시간/버튼)
    BOOKING,        // 경로 입력 (MainRouteScreen)
    SERVICE_TYPE,   // 💡 [추가] 서비스 유형 선택
    TIME_SELECTION, // 예약 시간 선택
    REQUEST_DETAIL, // 💡 [추가] 요청 사항 입력
    PAYMENT,        // 💡 [추가] 결제 화면
    CONFIRM         // 최종 확인 (사용되지 않을 수 있음)
}