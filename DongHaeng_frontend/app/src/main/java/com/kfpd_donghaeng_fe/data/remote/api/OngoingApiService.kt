package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.MyRequestItemDto
import com.kfpd_donghaeng_fe.data.remote.dto.MyRequestResponseData
import com.kfpd_donghaeng_fe.data.remote.dto.QRDto
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanRequest
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateResponse
import com.kfpd_donghaeng_fe.data.remote.dto.RequestDto
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


//동행 종료 후 디테일 정보
//EndMatchDto

//시작 qr 정보
interface StartQRApiService{
    @GET("/api/matches/{match_id}/qr/start")
    suspend fun getStartQR(
        @Path("match_id") matchId: Long
    ): BaseResponseDto<QRDto>
}

//종료 qr 정보
interface EndQRApiService{
    @GET("/api/matches/{match_id}/qr/end")
    suspend fun getEndQR(
        @Path("match_id") matchId: Long
    ): BaseResponseDto<QRDto>
}


//qr 스캔 요청
interface QRScanApiService {
    @POST("/api/qr/scan")
    suspend fun postQRScan(
        @Body request: QRScanRequest //  요청 DTO를 @Body로 전달
    ): BaseResponseDto<QRScanResponseDto> //  응답 DTO를 받음
}


