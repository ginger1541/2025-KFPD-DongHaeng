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
{
    companion object {
        /**
         * 초기 상태 또는 데이터가 로드되지 않았을 때 사용되는 더미 객체입니다.
         * 모든 필드는 Null이 아닌 안전한 기본값으로 초기화됩니다.
         */
        val Empty = QREntity(
            qrCode = "",
            qrImageUrl = "", // 👈 빈 URL 문자열
            qrType = QRTypes.NONE,
            qrScanned = false
        )
    }}


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














