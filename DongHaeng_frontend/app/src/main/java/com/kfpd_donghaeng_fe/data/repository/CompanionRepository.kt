package com.kfpd_donghaeng_fe.data.repository

import android.util.Log
import com.kfpd_donghaeng_fe.data.remote.api.CompanionApiService
import com.kfpd_donghaeng_fe.data.remote.dto.CompanionRequestDetailDto
import com.kfpd_donghaeng_fe.data.remote.dto.MatchResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.NearbyRequestDto
import javax.inject.Inject

class CompanionRepository @Inject constructor(
    private val apiService: CompanionApiService
) {

    suspend fun getNearbyRequests(lat: Double, lng: Double): Result<List<NearbyRequestDto>> {
        return try {
            // 🔍 [로그 1] 요청 시작 시 좌표 확인
            Log.d("CompanionRepo", "📡 [요청] 주변 요청 조회 시작 - 위도: $lat, 경도: $lng")

            val response = apiService.getNearbyRequests(
                latitude = lat,
                longitude = lng,
                radiusKm = 5
            )

            // 🔍 [로그 2] 응답 코드 확인
            Log.d("CompanionRepo", "📩 [응답] HTTP 상태 코드: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                // 🔍 [로그 3] 성공 응답 바디 확인
                Log.d("CompanionRepo", "✅ [성공] 응답 본문: $body")

                if (body?.success == true) {
                    val requests = body.data?.requests ?: emptyList()
                    Log.d("CompanionRepo", "📦 [데이터] 가져온 요청 개수: ${requests.size}")
                    Result.success(requests)
                } else {
                    // 🔍 [로그 4] API 호출은 성공했으나, 서버 로직상 실패 (success: false)
                    Log.e("CompanionRepo", "❌ [실패] success가 false입니다. 메시지: ${body?.message}")
                    Result.failure(Exception(body?.message ?: "주변 요청 조회 실패"))
                }
            } else {
                // 🔍 [로그 5] HTTP 에러 (4xx, 5xx) 발생 시 에러 바디 확인
                val errorBody = response.errorBody()?.string()
                Log.e("CompanionRepo", "🔥 [API 오류] 에러 내용: $errorBody")
                Result.failure(Exception(response.message() ?: "API 호출 실패"))
            }
        } catch (e: Exception) {
            // 🔍 [로그 6] 네트워크 예외 발생
            Log.e("CompanionRepo", "💥 [예외 발생] 네트워크 오류: ${e.message}", e)
            Result.failure(e)
        }
    }

    // 상세 조회
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

    // 수락
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