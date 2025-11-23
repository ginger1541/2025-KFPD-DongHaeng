package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.data.remote.api.MatchApiService
import com.kfpd_donghaeng_fe.data.remote.dto.MatchListResponse
import javax.inject.Inject

class MatchRepositoryImpl @Inject constructor(
    private val apiService: MatchApiService
) {
    suspend fun getMyMatches(): Result<MatchListResponse> {
        return try {
            val response = apiService.getMatches()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}