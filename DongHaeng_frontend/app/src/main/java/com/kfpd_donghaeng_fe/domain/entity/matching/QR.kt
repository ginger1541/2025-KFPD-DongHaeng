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
    val qrImageUrl: String,
    val qrType : QRTypes,
    val qrScanned : Boolean,
)
{
    companion object {
        // 💡 QREntity가 초기 상태를 나타낼 때 사용할 Empty 객체 정의
        val Empty = QREntity(
            // 💡 이미지 URL은 초기에는 빈 문자열
            qrImageUrl = "",
            // 💡 QR 타입은 UNKNOWN (또는 사용되는 기본값)으로 설정
            qrType = QRTypes.START,
            // 💡 스캔 여부는 초기에는 false
            qrScanned = false
        )
    }
}
data class QRScreenUiState(
    val qrEntity: QREntity = QREntity.Empty, // 👈 Non-null QREntity (초기값 필요)
    val isLoading: Boolean = true,           // 👈 로딩 상태 플래그
    val isError: Boolean = false
)


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














