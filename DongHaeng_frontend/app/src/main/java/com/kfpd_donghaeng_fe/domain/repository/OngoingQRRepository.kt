package com.kfpd_donghaeng_fe.domain.repository

import com.kfpd_donghaeng_fe.domain.entity.matching.*

/**
 * 진행 중인 매칭의 QR 코드 관련 데이터를 처리하는 Repository 인터페이스입니다.
 */
interface OngoingQRRepository {

    /**
     * 현재 매칭에 해당하는 QR 코드 정보를 가져옵니다. (QR 설정 및 표시용)
     * @param matchId 진행 중인 매칭의 ID
     * @return QRResponseEntity (QR 이미지 URL, 타입 등)
     */
    suspend fun getOngoingQRInfo(matchId: Int): Result<QRResponseEntity>

    /**
     * QR 코드 스캔 후, 서버에 스캔 완료 정보를 전송합니다.
     * @param requestEntity QR 코드와 스캔 장소 정보 (QRSRequestEntity)
     * @param qrType 스캔한 QR 코드의 타입 (START/END)
     * @return QRScanResultEntity (시작 또는 종료 결과 엔티티)
     */
    suspend fun sendQRScanResult(
        requestEntity: QRSRequestEntity,
        qrType: QRTypes
    ): Result<QRScanResultEntity>
}