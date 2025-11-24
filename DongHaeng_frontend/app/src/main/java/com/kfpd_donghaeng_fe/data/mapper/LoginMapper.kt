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