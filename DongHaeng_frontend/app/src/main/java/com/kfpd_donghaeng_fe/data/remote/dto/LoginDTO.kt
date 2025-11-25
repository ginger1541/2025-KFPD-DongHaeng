package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginResultEntity
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginUserEntity


// TODO : 영찬님 보낸거 확인하기! ( 일반 로그인 )
data class LoginRequestDto(
    @SerializedName("email") val email: String,
    @SerializedName("password")val password: String
)


data class LoginRespondDto(
    @SerializedName("is_new_user") val isNewUser: Boolean, // 명세서엔 없는데 기존 코드에 있어서 유지 (필요없으면 삭제)
    @SerializedName("user") val user: UserDto,             // 명세서의 data.user
    @SerializedName("tokens") val tokens: TokensDto        // 💡 명세서의 data.tokens
)

// user 정보
data class UserDto(
    @SerializedName("id") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String,
    @SerializedName("userType") val userType: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    @SerializedName("companionScore") val companionScore: Double
)

data class TokensDto(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("refreshToken") val refreshToken: String
)