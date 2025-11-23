package com.kfpd_donghaeng_fe.data.remote.dto
import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.domain.entity.MatchRequestData
import com.kfpd_donghaeng_fe.domain.entity.matching.EndMatchEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanEndEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanLocationEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanStartEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import kotlin.Double
import kotlin.Int


// 진행 정보

// 실제 이동 시간 및 이동 정보 전송
// TODO: qr 찍은 후 로직 구현 x (이동정보 실시간인가요?) ,
// TODO: 만약에 카메라를 그냥 키고 있다면? 어쩐담 : 로직 구현 필요

//요청자, 요청 정보 조회
//GET /api/companion-requests/{request_id}
data class RequestDto(
    @SerializedName("request_id") val requestId: Long,
    @SerializedName("requester") val request: RequesterDto, //요청자 정보
    @SerializedName ("status") val status : String,//진행정보
    @SerializedName("start_address") val startAddress: String, // 시작 주소 명칭
    @SerializedName("destination_address") val destinationAddress: String, // 목적지 주소 명칭

)
//요청자 정보
data class RequesterDto(
    @SerializedName("user_id") val UserID: Int,
    @SerializedName("name") val Name : String,
    @SerializedName ("profile_image_url") val ProfileImageUrl : String,
    @SerializedName ("companion_score") val DHScore : Int, //요청자 동행 점수
    )



//ongoigview top sheet의 요청자 정보 (요청 장소, 요청 진행 상태 이름, 동행지수)
fun RequestDto.toDomain() = MatchRequestData(
    startAddress=startAddress,
    destinationAddress=destinationAddress,
    status=status,
    Name =request.Name,
    ProfileImageUrl=request.ProfileImageUrl,
    DHScore=request.DHScore

)


//동행 종료 후 동행디테일 정보

















/************QR 따로 파일로 빼는게 나을 듯 *******/

// qr 인증 정보 조회 (시작,종료)

//1. 시작 qr 조회
//GET /api/matches/{match_id}/qr/start

//2.종료 qr 조회
//GET /api/matches/{match_id}/qr/end

data class QRDto(
    @SerializedName("qr_code") val QRCode: String,
    @SerializedName("qr_image_url") val QRImageUrl: String,
    @SerializedName ("auth_type") val QRType : String,//start or end
    @SerializedName("scanned") val QRScanned: Boolean, // 스캔 여부

)

//qr 정보
fun QRDto.toDomain() = QREntity(
    QRCode = QRCode,
    QRImageUrl = QRImageUrl,
    QRType =  QRTypes.fromString(this.QRType) ,
    QRScanned = QRScanned
)



//qr 스캔 요청
data class QRScanRequest(
    @SerializedName("qr_code")  val qrCode: String,
    @SerializedName("location") val location: Location
)

data class Location(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double
)


//스캔 위치
fun QRScanRequest.toDomain() = QRScanLocationEntity(
    x = location.latitude,
    y = location.longitude
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



//동행 종료 후 동행디테일 정보

fun QRScanResponseDto.toDomain() = EndMatchEntity(
    actualDurationMinutes=actualDurationMinutes,
    earnedPoints=earnedPoints,
    earnedVolunteerMinutes=earnedVolunteerMinutes
)


fun QRScanResponseDto.toDomaini2(): QRScanResultEntity{
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







