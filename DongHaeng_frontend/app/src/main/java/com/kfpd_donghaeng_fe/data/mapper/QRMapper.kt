package com.kfpd_donghaeng_fe.data.remote.mapper

import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.data.remote.dto.*
import com.kfpd_donghaeng_fe.domain.entity.matching.*

// --- 기존 DTO -> Entity 변환 함수는 그대로 유지 ---




//qr 정보





//qr 스캔응답 (시작, 종료)
//POST /api/qr/scan


//qr 생성
fun  BaseResponseDto<QRDto>.toDomainQR(): QREntity{
    val Data = data ?: throw IllegalStateException("서버 응답 데이터(data)가 null입니다.")
    val qrTypeString = Data.qrType
    return QREntity (
        qrCode=Data.qrCode,
        qrImageUrl= Data.qrImageUrl,
        qrType = QRTypes.fromString(qrTypeString),//start or end
        qrScanned =Data.qrScanned, // 스캔 여부
    )

}


//qr 스캔 후 서버에 보내는
fun BaseResponseDto<AfterQRScanDto>.toDomainQRScan(): QRScandEntity{
    val Data = data ?: throw IllegalStateException("서버 응답 데이터(data)가 null입니다.")
    return QRScandEntity (
    qrCode = Data.qrCode,
    latitude=Data.latitude,
    longitude=Data.longitude
    )
}



fun BaseResponseDto<QRScanResponseDto>.toDomainQRScanResponse():QRScanResultEntity{
    val Data = data ?: throw IllegalStateException("서버 응답 데이터(data)가 null입니다.")
    return when (Data.authType.lowercase()) {
        "start" -> QRScanStartEntity(
            matchId = Data.matchId,
            scannedAt = Data.scannedAt
        )
        "end" -> {
            QRScanEndEntity(
                matchId = Data.matchId,
                scannedAt =Data.scannedAt,
                actualDurationMinutes = Data.actualDurationMinutes ?: 0,
                earnedPoints = Data.earnedPoints ?: 0,
                earnedVolunteerMinutes = Data.earnedVolunteerMinutes ?: 0
            )
        }
        //일단 예외 처리..(예비)
        else -> throw IllegalArgumentException("Unknown auth type: ${Data.authType}")
    }

}

