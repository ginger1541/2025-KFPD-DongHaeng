package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

// 1. data 객체를 감싸는 래퍼 클래스
data class NearbyResponseData(
    @SerializedName("requests") val requests: List<NearbyRequestDto>,
    @SerializedName("count") val count: Int
    // searchArea 등은 필요 없으면 생략 가능
)

// 2. 개별 요청 아이템 DTO (상세 조회 DTO와 유사한 구조라고 가정)
data class NearbyRequestDto(
    @SerializedName("request_id", alternate = ["id"]) val requestId: Long,
    @SerializedName("title") val title: String,

    // 💡 수정: 서버의 snake_case 키와 매핑하고, null 가능하도록 ? 추가
    @SerializedName("start_address", alternate = ["startAddress"]) val startAddress: String?,
    @SerializedName("destination_address", alternate = ["destinationAddress"]) val destinationAddress: String?,
    @SerializedName("scheduled_at", alternate = ["scheduledAt"]) val scheduledAt: String?,

    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?
)