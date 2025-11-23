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


//mapping!
// DTO를 Domain Entity로 변환하는 함수 (Mapper)

fun BaseResponseDto<LoginRespondDto>.toDomainLogin(): LoginResultEntity {
    if (!success) {
        throw Exception(message ?: "로그인 실패: 서버 응답 오류.")
    }

    val loginData = data ?: throw Exception("로그인 실패: 데이터 본문이 없습니다.")

    // DTO -> Entity 변환
    val userDomainData = LoginUserEntity(
        userId = loginData.user.id,
        email = loginData.user.email,
        name = loginData.user.name,
        profileImageUrl = loginData.user.profileImageUrl,
        userType = loginData.user.userType,
        companionScore = loginData.user.companionScore
    )

    return LoginResultEntity(
        success = true,
        isNewUser = loginData.isNewUser, // 명세에 없으면 false 처리 등을 고려
        userData = userDomainData,

        // 💡 핵심: 토큰을 꺼내서 Entity에 담습니다.
        token = loginData.tokens.accessToken
    )
}