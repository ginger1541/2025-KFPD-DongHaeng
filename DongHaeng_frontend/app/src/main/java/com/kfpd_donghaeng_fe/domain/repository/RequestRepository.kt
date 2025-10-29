package com.kfpd_donghaeng_fe.domain.repository

import com.kfpd_donghaeng_fe.data.Request

interface RequestRepository {
    suspend fun getRequestList(): List<Request> // 모든 요청 목록
    suspend fun getRequestById(id: Long): Request // 특정 ID의 요청
}