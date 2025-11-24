package com.kfpd_donghaeng_fe.data.mapper

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.LoginRespondDto
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginResultEntity
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginUserEntity

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