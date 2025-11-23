package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.LoginRequest
import com.kfpd_donghaeng_fe.data.remote.dto.RequestDto
import com.kfpd_donghaeng_fe.data.remote.dto.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path



interface LoginApiService {

    @POST("api/v1/auth/login") // 👈 실제 백엔드 로그인 엔드포인트로 수정하세요.
    suspend fun login(
        @Body request: LoginRequest
    ): TokenResponse // Retrofit이 JSON 응답을 TokenResponse 객체로 변환해 줍니다.
}

