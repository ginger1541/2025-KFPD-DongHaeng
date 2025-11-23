package com.kfpd_donghaeng_fe.data.remote.dto

import retrofit2.http.Body
import retrofit2.http.POST

// TODO 1: 실제 서버의 Request/Response 데이터 클래스를 정의해야 합니다.
data class LoginRequest(
    val email: String,
    val password: String
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)

