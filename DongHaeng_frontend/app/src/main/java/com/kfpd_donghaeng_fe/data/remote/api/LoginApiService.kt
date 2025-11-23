package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.LoginRequestDto
import com.kfpd_donghaeng_fe.data.remote.dto.LoginRespondDto


import retrofit2.http.Body
import retrofit2.http.POST




interface LoginApiService {

    @POST("/api/auth/verify")
    suspend fun login(
        @Body request: LoginRequestDto
    ): BaseResponseDto<LoginRespondDto> // Retrofit이 JSON 응답을 TokenResponse 객체로 변환해 줍니다.
}


