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
{
    // 💡 1. 빈 상태를 나타내는 내부 데이터 클래스 정의
    data class Empty(
        val scanTime: String = "",
        val isSuccess: Boolean = false,
        val message: String = ""
    ) : QRScanResultEntity // 💡 QRScanResultEntity를 구현해야 합니다.

    // 💡 2. 성공 상태 (예시)
    data class Success(
        val scanTime: String,
        val message: String
    ) : QRScanResultEntity

    // 💡 3. 실패 상태 (예시)
    data class Failure(
        val errorCode: Int,
        val errorMessage: String
    ) : QRScanResultEntity

    // 4. Companion Object에서 Dummy 객체 제공 (Empty 클래스 인스턴스)
    companion object {
        val EmptyState: QRScanResultEntity = Empty() // 💡 Empty 클래스의 인스턴스를 반환합니다.
    }
}
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

//QR 스캔 한 장소
data class QRScandEntity( // qr 스캔 후 서버에 보냄
    val qrCode : String,
    val latitude:Double,
    val longitude: Double
){
    companion object {
        val Empty = QRScandEntity(qrCode = "",0.0,0.0)
    }
}














