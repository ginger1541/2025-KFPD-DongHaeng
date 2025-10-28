package com.kfpd_donghaeng_fe.ui.auth

// 4개 화면의 모든 상태를 담는 데이터 클래스
data class SignUpUiState(
    val currentPage: Int = 1, // 현재 페이지 (1~4)
    val nickname: String = "",
    val bio: String = "",
    val userType: UserType? = null, // null, NEEDY, HELPER
    val termsAllAgreed: Boolean = false,
    val termServiceAgreed: Boolean = false,
    val termPrivacyAgreed: Boolean = false,
    val termLocationAgreed: Boolean = false,
)

// 사용자 유형을 나타내는 enum 클래스
enum class UserType {
    NEEDY, // 도움이 필요해요
    HELPER // 도움을 드릴게요
}