package com.kfpd_donghaeng_fe.data.remote.dto
import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.domain.entity.MatchRequestData
import com.kfpd_donghaeng_fe.domain.entity.matching.EndMatchEntity

import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanEndEntity

import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanStartEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import kotlin.Double
import kotlin.Int


// 진행 정보

// 실제 이동 시간 및 이동 정보 전송
// TODO: qr 찍은 후 로직 구현 x (이동정보 실시간인가요? ㅇㅇ...) ,
// TODO: 만약에 카메라를 그냥 키고 있다면? 어쩐담 : 로직 구현 필요

//요청자, 요청 정보 조회
//GET /api/companion-requests/{request_id}
data class OngoingRequestDto(
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



















