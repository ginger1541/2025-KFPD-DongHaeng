package com.kfpd_donghaeng_fe.domain.entity.auth

import com.google.gson.annotations.SerializedName

data class LoginAccountUiState(
    val currentPage: Int = 0,          // 페이지 번호 0 : 로그인 전 :1 로그인 후 메인 화면 진입
    val id: String = "",  //아이디
    val pw:String="", // 비번
    val userType: UserType? = null,    // 유저 타입

)
data class LoginUserEntity(
    val userId : Int,
    val email: String,
    val name: String?,
    val profileImageUrl: String?,
    val userType: String?,
    val companionScore: Double?
)


data class LoginResultEntity(
    val success: Boolean,
    val isNewUser: Boolean,
    val userData: LoginUserEntity,
    val token: String
)