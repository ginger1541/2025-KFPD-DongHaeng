package com.kfpd_donghaeng_fe.data.local

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.CreatedQRDto
import com.kfpd_donghaeng_fe.data.remote.dto.ScanedEndQRResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.ScanedQRDto
import com.kfpd_donghaeng_fe.data.remote.dto.ScanedStartQRResponseDto


// domain/datasource/TokenLocalDataSource.kt (인터페이스)
interface TokenLocalDataSource {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun deleteToken()
}


interface OngoingQRDataSource {
    // QR 정보를 가져오는 API 호출 DTO 반환
    suspend fun fetchCreatedQRInfo(matchId: Int): BaseResponseDto<CreatedQRDto>

    // 시작 QR 스캔 결과를 전송하고 응답 DTO 반환
    suspend fun scanStartQR(requestDto: ScanedQRDto): BaseResponseDto<ScanedStartQRResponseDto>

    // 종료 QR 스캔 결과를 전송하고 응답 DTO 반환
    suspend fun scanEndQR(requestDto: ScanedQRDto): BaseResponseDto<ScanedEndQRResponseDto>
}



