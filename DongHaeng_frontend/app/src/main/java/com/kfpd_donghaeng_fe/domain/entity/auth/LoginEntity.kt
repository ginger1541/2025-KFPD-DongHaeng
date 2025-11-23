package com.kfpd_donghaeng_fe.domain.entity.auth

data class LoginAccountUiState(
    val currentPage: Int = 0,          // 페이지 번호 0 : 로그인 전 :1 로그인 후 메인 화면 진입
    val id: String = "",  //아이디
    val pw:String="", // 비번
    val userType: UserType? = null,    // 유저 타입

)
