package com.kfpd_donghaeng_fe.domain.entity.auth


//임시 데이터 구조
data class MakeAccountUiState(
    val currentPage: Int = 0,          // 페이지 번호
    val nickname: String = "",         // 닉네임
    val bio: String = "",              // 자기소개
    val userType: UserType?=null,    // 유저 타입
    val selectedUserType : UserType?=null,
    var userInfoUiState: UserInfoUiState = UserInfoUiState()


)


// 사용자 유형을 나타내는 enum 클래스
enum class UserType {
    NEEDY, // 도움이 필요해요
    HELPER // 도움을 드릴게요
}

// MakeAccountUiState.kt 파일 (또는 ViewModel 파일 상단)

// UserInfoUiState.kt 파일 (또는 ViewModel 파일 상단)
data class UserInfoUiState(
    val userId: String = "",
    val userIdError: String? = null,
    val password: String = "",
    val passwordConfirm: String = "",
    val passwordError: String? = null,
    val gender: String? = null,
    val phoneNumber: String = "",
    val phoneValidationMessage: String? = null,
    val birthDate: String = "YYYY / MM / DD",
    val showPassword: Boolean = false,
    val showPasswordConfirm: Boolean = false,

    )