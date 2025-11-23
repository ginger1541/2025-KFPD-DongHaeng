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
                // 🔍 [디버깅] 실패 원인 상세 분석
                val code = response.code()
                val errorBody = response.errorBody()?.string() // 서버가 보낸 에러 메시지 원본
                val message = response.body()?.message

                android.util.Log.e("API_ERROR", "요청 실패 - Code: $code, Msg: $message")
                android.util.Log.e("API_ERROR", "ErrorBody: $errorBody")

                Result.failure(Exception("API 오류($code): $message"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("API_ERROR", "네트워크 예외 발생: ${e.message}")
            Result.failure(e)
        }
    }

    // 기존 함수들의 구현도 필요하다면 여기에...
}