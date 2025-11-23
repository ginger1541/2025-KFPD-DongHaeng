package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.data.remote.api.CompanionApiService
import com.kfpd_donghaeng_fe.data.remote.dto.CompanionRequestDetailDto
import com.kfpd_donghaeng_fe.data.remote.dto.MatchResponseDto
import javax.inject.Inject

class CompanionRepository @Inject constructor(
    private val apiService: CompanionApiService
) {
    suspend fun getRequestDetail(requestId: Long): Result<CompanionRequestDetailDto> {
        return try {
            val response = apiService.getRequestDetail(requestId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "요청 상세 조회 실패"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun acceptRequest(requestId: Long): Result<MatchResponseDto> {
        return try {
            val response = apiService.acceptRequest(requestId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data!!)
            } else {
                Result.failure(Exception(response.body()?.message ?: "요청 수락 실패"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}