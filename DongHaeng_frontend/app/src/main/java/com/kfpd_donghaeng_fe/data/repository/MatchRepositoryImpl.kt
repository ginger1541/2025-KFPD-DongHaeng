package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.data.remote.api.MatchApiService
import com.kfpd_donghaeng_fe.data.remote.dto.MatchDetailDTO
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

    // 상세 조회 추가
    suspend fun getMatchDetail(matchId: Long): Result<MatchDetailDTO> {
        return try {
            val response = apiService.getMatchDetail(matchId)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Detail API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}