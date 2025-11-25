package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.QRDto
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanRequest
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

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
interface StartQRScanApiService {
    @POST("/api/matches/{match_id}/start")
    suspend fun postStartQRScan(
        @Path("match_id") matchId: Long,
        @Body request: QRScanRequest //  요청 DTO를 @Body로 전달
    ): BaseResponseDto<QRScanResponseDto> //  응답 DTO를 받음
}

interface EndQRScanApiService {
    @POST("/api/matches/{match_id}/end")
    suspend fun postEndQRScan(
        @Path("match_id") matchId: Long,
        @Body request: QRScanRequest //  요청 DTO를 @Body로 전달
    ): BaseResponseDto<QRScanResponseDto> //  응답 DTO를 받음
}

