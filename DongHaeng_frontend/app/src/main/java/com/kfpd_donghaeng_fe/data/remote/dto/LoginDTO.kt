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




//mapping!
// DTO를 Domain Entity로 변환하는 함수 (Mapper)

fun BaseResponseDto<LoginRespondDto>.toDomainLogin(): LoginResultEntity {

    if (!success) {

        throw Exception(message ?: "로그인 실패: 서버 응답 오류.")
    }

    val loginData = data ?: throw Exception("로그인 실패: 데이터 본문이 없습니다.")

    // 1. User DTO를 Domain Entity로 변환합니다. (User 상세 정보)
    val userDomainData = LoginUserEntity(
        userId = loginData.user.userId,
        email = loginData.user.email,
        name = loginData.user.name,
        profileImageUrl = loginData.user.profileImageUrl,
        userType = loginData.user.userType,
        companionScore = loginData.user.companionScore
    )

    return LoginResultEntity(
        success=true,
        isNewUser = loginData.isNewUser,
        userData = userDomainData
    )
}