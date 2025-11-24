package com.kfpd_donghaeng_fe.domain.entity.matching



//enum 맵핑
enum class QRTypes {START, END, NONE;
    companion object {
        fun fromString(type: String) = when (type.lowercase()) {
            "start" -> START
            "end" -> END
            else->NONE }
    }
}


data class QREntity(  // qr 생성
    val qrCode : String,
    val qrImageUrl: String,
    val qrType : QRTypes,
    val qrScanned : Boolean,
)


sealed interface QRScanResultEntity
//QRtype == "start"
data class QRScanStartEntity(
    val matchId: Int,
    val scannedAt: String
) : QRScanResultEntity

//QRtype == "end"
data class QRScanEndEntity(
    val matchId: Int,
    val scannedAt: String,
    val actualDurationMinutes: Int,
    val earnedPoints: Int,
    val earnedVolunteerMinutes: Int
) : QRScanResultEntity

//QR 카메라 킨 장소
data class QRScandEntity( // qr 스캔 후 서버에 보냄
    val qrCode : String,
    val latitude:Double,
    val longitude: Double
)














