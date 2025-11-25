package com.kfpd_donghaeng_fe.data.repository


import android.util.Log
import com.kfpd_donghaeng_fe.data.remote.api.EndQRApiService
import com.kfpd_donghaeng_fe.data.remote.api.EndQRScanApiService
import com.kfpd_donghaeng_fe.data.remote.api.StartQRApiService
import com.kfpd_donghaeng_fe.data.remote.api.StartQRScanApiService
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanRequest
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanResponseDto
import com.kfpd_donghaeng_fe.data.remote.mapper.toDomainQR

import com.kfpd_donghaeng_fe.data.remote.mapper.toDomainQRScanResponse
import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes

import com.kfpd_donghaeng_fe.domain.repository.OngoingQRRepository

import javax.inject.Inject




class OngoingQRRepositoryImpl @Inject constructor(
    private val qrStart : StartQRApiService,
    private val qrEnd :  EndQRApiService,
    private val qrStartScan : StartQRScanApiService,
    private val qrEndScan : EndQRScanApiService,

    ) : OngoingQRRepository {
    override suspend fun getOngoingQRStartInfo(matchId: Long): Result<QREntity> {
        Log.d("QR_DEBUG", "Repository: getStartInfo 진입!")
        return try {
            // 이 dataSource를 통해 API를 호출합니다.
            Log.d("QR_DEBUG", "Retrofit 호출 직전") // 💡 1차 로그
            val response = qrStart.getStartQR(matchId)
            Log.d("QR_DEBUG", "Retrofit 응답 수신 완료") // 💡 2차 로그

            val entity = response.toDomainQR() // 💡 매핑 시작
            Log.d("QR_DEBUG", "매핑 완료") // 💡 3차 로그// BaseResponse 처리 및 매핑

            if (entity != null) {
                Log.e("QR_BASE64", "Entity URL start: ${entity.qrImageUrl.take(50)}")
                Result.success(entity)
            } else {
                Log.e("null","null 존재함")
                // success: false 이거나 data: null 인 경우
                Result.failure(Exception("QR 정보 로드에 실패했습니다. (Success: ${response.success})"))
            }
        } catch (e: Exception) {
            Log.e("QR_FATAL", "API 호출 후 알 수 없는 예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }
    override suspend fun getOngoingQREndInfo(matchId: Long): Result<QREntity> {
        return try {
            val response = qrEnd.getEndQR(matchId)
            val entity = response.toDomainQR() // BaseResponse 처리 및 매핑
            if (entity != null) {
                Result.success(entity)
            } else {
                // success: false 이거나 data: null 인 경우
                Result.failure(Exception("QR 정보 로드에 실패했습니다. (Success: ${response.success})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendQRScanResult(
        requestEntity: QRScandEntity,
        qrType: QRTypes,
        matchId:Long
    ): Result<QRScanResultEntity> {

        // 1. Entity -> DTO 변환 (API 요청용)
        val requestDto = QRScanRequest(
            qrCode = requestEntity.qrCode,
            latitude = requestEntity.latitude,
            longitude = requestEntity.longitude
        )

        // 2. 요청 실행 및 결과 처리
        return runCatching {
            // 3. QR 타입에 따라 다른 API 함수 호출
            val response = when (qrType) {
                QRTypes.START -> qrStartScan.postStartQRScan(matchId,requestDto)
                QRTypes.END -> qrEndScan.postEndQRScan(matchId,requestDto)
                QRTypes.NONE -> throw IllegalArgumentException("유효하지 않은 QR 타입입니다.")
            }

            // 4. API 응답의 성공/실패 확인 및 DTO -> Entity 매핑
            // BaseResponseDto<T>가 있다고 가정하고, 응답이 성공(response.success)하지 않으면 예외 발생
            if (!response.success) {
                throw Exception(response.message ?: "QR 스캔 결과 전송 실패 (서버 오류).")
            }

            // 5. 응답 데이터(DTO)를 Entity로 매핑 후 반환
            val resultEntity = response.toDomainQRScanResponse()
            // 6. null 체크 후 반환
            resultEntity ?: throw Exception("QR 스캔 결과 데이터가 없습니다 (데이터: null).")

        } // runCatching 블록이 성공하면 Result.success로 자동 반환됩니다.
    }


}
