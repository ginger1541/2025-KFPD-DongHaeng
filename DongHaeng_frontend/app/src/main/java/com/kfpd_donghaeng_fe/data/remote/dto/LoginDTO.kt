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
    @SerializedName("is_new_user") val isNewUser: Boolean,
    @SerializedName("user") val user: User
)

// user 정보
data class User(
    @SerializedName("user_id") val userId: Int, // Int로 가정
    @SerializedName("email") val email: String,
    @SerializedName("name") val name: String?,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
    @SerializedName("user_type") val userType: String?,
    @SerializedName("companion_score") val companionScore: Double?
)




