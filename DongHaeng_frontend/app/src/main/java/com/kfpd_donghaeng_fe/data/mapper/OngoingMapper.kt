package com.kfpd_donghaeng_fe.data.mapper

import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.OngoingRequestDto
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingRequestEntity

///OngoingRequestEntity(
fun BaseResponseDto<OngoingRequestDto>.toDomainOngoing() : OngoingRequestEntity{


    val Data = data ?: throw IllegalStateException("서버 응답 데이터(data)가 null입니다.")
    val Request = Data.request ?: throw IllegalStateException("요청 정보(request)가 null입니다.")

    return OngoingRequestEntity(
        requestId=Data.requestId,
        startAddress = Data.startAddress, // null 처리
        destinationAddress = Data.destinationAddress, // null 처리
        status = Data.status , // null 처리
        Name = Request.Name ,
        ProfileImageUrl = Request.ProfileImageUrl,
        DHScore = Request.DHScore // DHScore가 Int라면 0으로 대체
    )

}





