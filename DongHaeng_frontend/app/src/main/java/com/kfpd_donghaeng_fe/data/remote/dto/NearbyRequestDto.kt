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
    @SerializedName("id") val requestId: Long,
    val title: String,
    val startAddress: String,
    val destinationAddress: String,
    val scheduledAt: String,
    // 필요시 latitude, longitude 등 추가
    val latitude: Double?,
    val longitude: Double?
)