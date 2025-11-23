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


data class QREntity(
    val QRCode : String,
    val QRImageUrl: String,
    val QRType : QRTypes,
    val QRScanned : Boolean,
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
data class QRScanLocationEntity(
    val x :Double,
    val y : Double
)