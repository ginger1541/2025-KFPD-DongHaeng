package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.data.remote.api.MatchApiService
import com.kfpd_donghaeng_fe.data.remote.dto.MatchDetailDTO
import com.kfpd_donghaeng_fe.data.remote.dto.MatchListResponse
import com.kfpd_donghaeng_fe.data.remote.dto.ReviewRequestDto
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

    suspend fun writeReview(request: ReviewRequestDto): Result<String> {
        return try {
            val response = apiService.writeReview(request)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()?.message ?: "후기 작성 성공")
            } else {
                Result.failure(Exception("후기 작성 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}