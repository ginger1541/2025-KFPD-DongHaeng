package com.kfpd_donghaeng_fe.data.remote.mapper

import android.util.Log
import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.data.remote.dto.AfterQRScanDto
import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.QRDto
import com.kfpd_donghaeng_fe.data.remote.dto.QRScanResponseDto
import com.kfpd_donghaeng_fe.domain.entity.matching.*

// --- 기존 DTO -> Entity 변환 함수는 그대로 유지 ---




//qr 정보





//qr 스캔응답 (시작, 종료)
//POST /api/qr/scan


//qr 생성
fun BaseResponseDto<QRDto>.toDomainQR(): QREntity? {

    Log.d("QR_DEBUG", "Mapper: toDomainQR 진입!")
    //val data = this.data
    val data = this.data ?: return null
    // 2. 필수 필드: Base64 이미지와 QR Type이 null인지 체크 (Null이면 매핑 실패)
    val qrCodeBase64 = data.qrCodeImg
    val qrTypeString = data.qrType

    // 3. 타입 변환 중 실패 체크
    val qrType = QRTypes.fromString(qrTypeString)

    // 4. QREntity 생성
    return QREntity(
        // 💡 QREntity의 qrImageUrl 필드에 DTO의 qrCodeImg를 할당
        qrImageUrl = qrCodeBase64,
        qrType = qrType,
        qrScanned = data.qrScanned,
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

