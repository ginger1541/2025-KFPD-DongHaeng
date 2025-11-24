package com.kfpd_donghaeng_fe.domain.usecase

import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.repository.OngoingQRRepository
import javax.inject.Inject



//qr 정보 받기
class GetOngoingQRStartInfoUseCase @Inject constructor(
    private val repository: OngoingQRRepository
) {
    // matchId를 받아 QREntity를 Result로 반환
    suspend operator fun invoke(matchId: Long): Result<QREntity> {
        return repository.getOngoingQRStartInfo(matchId)
    }
}





// qr 스캔 결과 서버 , 응답 받기
class SendQRScanResultUseCase @Inject constructor(
    private val repository: OngoingQRRepository
) {
    // QRScandEntity, QRTypes, matchId를 받아 QRScanResultEntity를 Result로 반환
    suspend operator fun invoke(
        requestEntity: QRScandEntity,
        qrType: QRTypes,
        matchId: Long
    ): Result<QRScanResultEntity> {
        return repository.sendQRScanResult(requestEntity, qrType, matchId)
    }
}