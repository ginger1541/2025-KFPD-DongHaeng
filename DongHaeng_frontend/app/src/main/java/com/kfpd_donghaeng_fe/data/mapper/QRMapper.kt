package com.kfpd_donghaeng_fe.data.remote.mapper

import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.data.remote.dto.*
import com.kfpd_donghaeng_fe.domain.entity.matching.*

// --- 기존 DTO -> Entity 변환 함수는 그대로 유지 ---




//qr 정보





//qr 스캔응답 (시작, 종료)
//POST /api/qr/scan


//qr 생성
fun  QRDto.toDomainQR(): QREntity{
    val qrTypeString = this.qrType
    return QREntity (
        qrCode=this.qrCode,
        qrImageUrl= this.qrImageUrl,
        qrType = QRTypes.fromString(qrTypeString),//start or end
        qrScanned =this.qrScanned, // 스캔 여부
    )

}


//qr 스캔 후 서버에 보내는
fun AfterQRScanDto.toDomainQRScan(): QRScandEntity{
    return QRScandEntity (
    qrCode = this.qrCode,
    latitude=this.latitude,
    longitude=this.longitude
    )
}



fun QRScanResponseDto.toDomainQRScanResponse():QRScanResultEntity{
    return when (this.authType.lowercase()) {
        "start" -> QRScanStartEntity(
            matchId = this.matchId,
            scannedAt = this.scannedAt
        )
        "end" -> {
            QRScanEndEntity(
                matchId = this.matchId,
                scannedAt = this.scannedAt,
                actualDurationMinutes = this.actualDurationMinutes ?: 0,
                earnedPoints = this.earnedPoints ?: 0,
                earnedVolunteerMinutes = this.earnedVolunteerMinutes ?: 0
            )
        }
        //일단 예외 처리..(예비)
        else -> throw IllegalArgumentException("Unknown auth type: ${this.authType}")
    }

}

