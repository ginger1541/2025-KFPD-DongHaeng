package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.data.remote.api.RequestApiService
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateResponse
import com.kfpd_donghaeng_fe.domain.repository.RequestRepository
import javax.inject.Inject

class RequestRepositoryImpl @Inject constructor(
    private val apiService: RequestApiService
) : RequestRepository {
    override suspend fun getRequestList(): List<Request> {
        TODO("Not yet implemented")
    }

    override suspend fun getRequestById(id: Long): Request {
        TODO("Not yet implemented")
    }

    override suspend fun createRequest(requestDto: RequestCreateDto): Result<RequestCreateResponse> {
        return try {
            val response = apiService.createRequest(requestDto)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "요청 생성 실패"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 기존 함수들의 구현도 필요하다면 여기에...
}