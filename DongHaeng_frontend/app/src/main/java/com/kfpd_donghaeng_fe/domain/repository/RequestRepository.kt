package com.kfpd_donghaeng_fe.domain.repository

import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateResponse

interface RequestRepository {
    suspend fun getRequestList(): List<Request> // 모든 요청 목록
    suspend fun getRequestById(id: Long): Request // 특정 ID의 요청
    suspend fun createRequest(requestDto: RequestCreateDto): Result<RequestCreateResponse> //요청 생성
}