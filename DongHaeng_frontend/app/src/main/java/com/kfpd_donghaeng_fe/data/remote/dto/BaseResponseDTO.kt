package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

data class BaseResponseDto<T>(
    // API 성공 여부
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T?= null,
    @SerializedName("message") val message: String? = null,
    @SerializedName("error") val error: ErrorDto? = null
)



/**
 * 📡 API 목록 조회 응답을 위한 공통 DTO
 * (data + pagination 구조)
 */
data class BasePaginatedResponseDto<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: T,
    @SerializedName("pagination") val pagination: PaginationDto
)

