package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.CompanionRequestDetailDto
import com.kfpd_donghaeng_fe.data.remote.dto.MatchResponseDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface CompanionApiService {

    // 3.4 요청 상세 조회
    @GET("/api/companions/requests/{requestId}")
    suspend fun getRequestDetail(
        @Path("requestId") requestId: Long
    ): Response<BaseResponseDto<CompanionRequestDetailDto>>

    // 3.7 요청 수락 (매칭 생성)
    @POST("/api/companions/requests/{requestId}/accept")
    suspend fun acceptRequest(
        @Path("requestId") requestId: Long
    ): Response<BaseResponseDto<MatchResponseDto>>
}