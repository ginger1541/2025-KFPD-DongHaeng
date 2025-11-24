package com.kfpd_donghaeng_fe.data.repository


import com.kfpd_donghaeng_fe.data.remote.mapper.* // 작성했던 Mapper 함수들을 import
import com.kfpd_donghaeng_fe.domain.entity.matching.*
import com.kfpd_donghaeng_fe.domain.repository.OngoingQRRepository

import javax.inject.Inject




class OngoingQRRepositoryImpl @Inject constructor(

   // private val dataSource: OngoingQRDataSource
) : OngoingQRRepository {
/*
    override suspend fun getOngoingQRInfo(matchId: Int): Result<QRResponseEntity> {
        return try {
            val response = dataSource.fetchCreatedQRInfo(matchId)
            val entity = response.toQRResponseEntity() // BaseResponse 처리 및 매핑

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
        requestEntity: QRSRequestEntity,
        qrType: QRTypes
    ): Result<QRScanResultEntity> {

        // 1. Entity -> DTO 변환 (API 요청용)
        val requestDto = ScanedQRDto(
            QRCode = requestEntity.QRCode,
            latitude = requestEntity.y.toFloat(),
            longitude = requestEntity.x.toFloat()
        )

        return try {
            val resultEntity = when (qrType) {
                QRTypes.START -> {
                    val response = dataSource.scanStartQR(requestDto)
                    response.toQRScanStartEntity() // BaseResponse 처리 및 매핑
                }
                QRTypes.END -> {
                    val response = dataSource.scanEndQR(requestDto)
                    response.toQRScanEndEntity() // BaseResponse 처리 및 매핑
                }
                QRTypes.NONE -> throw IllegalArgumentException("유효하지 않은 QR 타입입니다.")
            }

            if (resultEntity != null) {
                Result.success(resultEntity)
            } else {
                // API 응답은 받았으나 success: false 이거나 data: null 인 경우
                Result.failure(Exception("QR 스캔 결과 전송에 실패했습니다."))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }*/
}
