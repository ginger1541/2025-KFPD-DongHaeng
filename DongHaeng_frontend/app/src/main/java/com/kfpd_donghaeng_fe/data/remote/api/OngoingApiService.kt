package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.MyRequestItemDto
import com.kfpd_donghaeng_fe.data.remote.dto.MyRequestResponseData
import com.kfpd_donghaeng_fe.data.remote.dto.QRDto
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanRequest
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.Response


// 매칭된 요청,요청자  상세 정보
interface RequestApiService {
    @GET("/api/companion-requests/{request_id}")
    suspend fun getRequestDetail(
        @Path("requestId") requestId: Long
    ): Response<BaseResponseDto<MyRequestItemDto>>

    @POST("/api/companions/requests")
    suspend fun createRequest(
        @Body request: RequestCreateDto
    ):Response<BaseResponseDto<RequestCreateResponse>>

    @GET("/api/companions/requests")
    suspend fun getMyRequests(): Response<BaseResponseDto<MyRequestResponseData>>
}




