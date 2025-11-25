package com.kfpd_donghaeng_fe.domain.repository


import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import java.time.LocalDateTime

interface OngoingInfoRepo {
    // QR 스캔을 수행하고 스캔 완료 시간(LocalDateTime)을 반환하는 계약
    suspend fun sendQRScanResult(
        requestEntity: QRScandEntity,
        qrType: QRTypes,
        matchId: Long
    ): Result<LocalDateTime>
}