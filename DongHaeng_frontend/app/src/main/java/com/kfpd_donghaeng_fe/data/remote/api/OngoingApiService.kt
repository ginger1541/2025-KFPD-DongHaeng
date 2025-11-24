package com.kfpd_donghaeng_fe.data.remote.api


import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.QRDto
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.OngoingRequestDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


//1. 시작 qr 조회
//GET /api/matches/{match_id}/qr/start

//2.종료 qr 조회
//GET /api/matches/{match_id}/qr/end

// 매칭된 요청,요청자  상세 정보
interface RequestApiService {
    @GET("/api/companion-requests/{request_id}")
    suspend fun getRequestDetail(
        @Path("requestId") requestId: Long
    ): BaseResponseDto<OngoingRequestDto>
}



//시작 qr 정보
interface StartQRApiService{
    @GET("/api/matches/{match_id}/qr/start")
    suspend fun getStartQR(
        @Path("match_id") matchId: Long
    ): BaseResponseDto< QRScanResponseDto>
}

//종료 qr 정보
interface EndQRApiService{
    @GET("/api/matches/{match_id}/qr/end")
    suspend fun getEndQR(
        @Path("match_id") matchId: Long
    ): BaseResponseDto< QRScanResponseDto>
}


//qr 스캔 요청
interface QRScanApiService {
    @POST("/api/qr/scan")
    suspend fun postQRScan(
        @Body request: QRDto //  요청 DTO를 @Body로 전달
    ): BaseResponseDto<QRScanResponseDto> //  응답 DTO를 받음
}





