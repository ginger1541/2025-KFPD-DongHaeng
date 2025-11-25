package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

// qr 인증 정보 조회 (시작,종료)

//1. 시작 qr 조회
//GET /api/matches/{match_id}/qr/start

//2.종료 qr 조회
//GET /api/matches/{match_id}/qr/end

data class QRDto(
    @SerializedName("matchId") val matchId :Long,
    @SerializedName("qrCode") val qrCodeImg: String,
    //@SerializedName("qrData") val qrData:String,
    @SerializedName ("authType") val qrType : String,//start or end
    @SerializedName("scanned") val qrScanned: Boolean, // 스캔 여부

)



//qr 스캔 후
data class AfterQRScanDto(
    @SerializedName("qrCode")  val qrCode: String,
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)



//qr 스캔응답 (시작, 종료)
//POST /api/qr/scan

data class QRScanResponseDto(
    @SerializedName("match_id") val matchId: Int,
    @SerializedName("auth_type") val authType: String, // start or end
    @SerializedName("scanned_at") val scannedAt: String,
    @SerializedName("status") val status: String, // ongoing or completed

    // --- '종료' 응답에만 있는 Nullable 필드 ---
    @SerializedName("actual_duration_minutes") val actualDurationMinutes: Int?,
    @SerializedName("earned_points") val earnedPoints: Int?,
    @SerializedName("earned_volunteer_minutes") val earnedVolunteerMinutes: Int?
)








// 동행 시작(QR스캔)
data class QRScanRequest(
    @SerializedName("qr_code") val qrCode: String,      // 스캔한 QR 코드 문자열
    @SerializedName("latitude") val latitude: Double,   // 스캔한 위치 위도
    @SerializedName("longitude") val longitude: Double  // 스캔한 위치 경도
)