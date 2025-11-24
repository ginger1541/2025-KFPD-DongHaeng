package com.kfpd_donghaeng_fe.data.repository

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.data.remote.api.RequestApiService
import com.kfpd_donghaeng_fe.data.remote.dto.MyRequestItemDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateDto
import com.kfpd_donghaeng_fe.data.remote.dto.RequestCreateResponse
import com.kfpd_donghaeng_fe.domain.repository.RequestRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

class RequestRepositoryImpl @Inject constructor(
    private val apiService: RequestApiService
) : RequestRepository {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRequestList(): List<Request> {
        Log.d("RequestRepo", "📡 [요청] 내 요청 목록 조회 시작 (GET /api/companions/requests)")

        return try {
            val response = apiService.getMyRequests()

            Log.d("RequestRepo", "📩 [응답] HTTP 상태 코드: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()
                Log.d("RequestRepo", "✅ [성공] 응답 본문: $body")

                if (body?.success == true) {
                    val dtoList = body.data?.requests

                    if (dtoList == null) {
                        Log.e("RequestRepo", "⚠️ data.requests가 null입니다!")
                        return emptyList()
                    }

                    Log.d("RequestRepo", "📦 [데이터] 파싱 전 개수: ${dtoList.size}")

                    val resultList = dtoList.mapNotNull { dto ->
                        try {
                            convertDtoToDomain(dto)
                        } catch (e: Exception) {
                            Log.e("RequestRepo", "⚠️ [매핑 오류] ID(${dto.id}) 변환 실패: ${e.message}")
                            null // 변환 실패한 항목은 제외
                        }
                    }

                    Log.d("RequestRepo", "✨ [완료] 최종 반환 개수: ${resultList.size}")
                    resultList
                } else {
                    Log.e("RequestRepo", "❌ [실패] success가 false입니다. 메시지: ${body?.message}")
                    emptyList()
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Log.e("RequestRepo", "🔥 [API 오류] 에러 내용: $errorBody")
                emptyList()
            }
        } catch (e: Exception) {
            Log.e("RequestRepo", "💥 [예외 발생] 네트워크 오류: ${e.message}", e)
            emptyList()
        }
    }

    // 💡 복잡한 변환 로직을 함수로 분리했습니다.
    @RequiresApi(Build.VERSION_CODES.O)
    private fun convertDtoToDomain(dto: MyRequestItemDto): Request {
        // 날짜/시간 포맷팅
        val zdt = try {
            ZonedDateTime.parse(dto.scheduledAt)
        } catch (e: Exception) {
            Log.w("RequestRepo", "날짜 파싱 실패 (${dto.scheduledAt}), 현재 시간 사용")
            ZonedDateTime.now()
        }

        val dateStr = zdt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
        val timeStr = zdt.format(DateTimeFormatter.ofPattern("a h시 m분 출발", Locale.KOREA))
        val arriveTimeStr = zdt.plusMinutes(dto.estimatedMinutes.toLong())
            .format(DateTimeFormatter.ofPattern("a h시 m분 도착", Locale.KOREA))

        // 거리 포맷팅
        val distanceStr = dto.route?.totalDistanceMeters?.let { meters ->
            if (meters < 1000) "${meters}m" else String.format("%.1fkm", meters / 1000.0)
        } ?: "거리 정보 없음"

        return Request(
            id = dto.id,
            date = dateStr,
            departure = dto.startAddress,
            arrival = dto.destinationAddress,
            departureTime = timeStr,
            arrivalTime = arriveTimeStr,
            distance = distanceStr,
            duration = "${dto.estimatedMinutes}분",
            pricePoints = 0,

            // DTO에 있는 latitude, longitude를 start 좌표로 사용
            startLatitude = dto.latitude ?: 0.0,
            startLongitude = dto.longitude ?: 0.0,

            // 도착지 좌표는 목록 API 응답에 없으므로 0.0으로 처리 (상세화면이나 지도에서 다시 로드)
            endLatitude = 0.0,
            endLongitude = 0.0
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun getRequestById(id: Long): Request {
        return try {
            val response = apiService.getRequestDetail(id)

            if (response.isSuccessful && response.body()?.success == true) {
                val dto = response.body()!!.data!!

                // 1. 날짜 변환
                val zdt = try {
                    ZonedDateTime.parse(dto.scheduledAt)
                } catch (e: Exception) {
                    ZonedDateTime.now()
                }
                val dateStr = zdt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
                val timeStr = zdt.format(DateTimeFormatter.ofPattern("a h시 m분 출발", Locale.KOREA))

                // 2. 도착 시간 계산
                val arriveTimeStr = zdt.plusMinutes(dto.estimatedMinutes.toLong())
                    .format(DateTimeFormatter.ofPattern("a h시 m분 도착", Locale.KOREA))

                // 3. 거리 정보
                val distanceStr = dto.route?.totalDistanceMeters?.let { meters ->
                    if (meters < 1000) "${meters}m" else String.format("%.1fkm", meters / 1000.0)
                } ?: "거리 정보 없음"

                // 4. 좌표 정보 추출 (없으면 0.0 처리)
                // 출발지 좌표: DTO의 최상위 필드 사용
                val startLat = dto.latitude ?: 0.0
                val startLng = dto.longitude ?: 0.0

                // 도착지 좌표: 목록 API에는 없으므로 0.0 처리
                // (만약 상세 조회 API가 목적지 좌표를 준다면 DTO에 필드 추가 후 여기서 매핑하면 됨)
                val endLat = 0.0
                val endLng = 0.0

                // 5. 객체 생성 및 반환
                Request(
                    id = dto.id,
                    date = dateStr,
                    departure = dto.startAddress,
                    arrival = dto.destinationAddress,
                    departureTime = timeStr,
                    arrivalTime = arriveTimeStr,
                    distance = distanceStr,
                    duration = "${dto.estimatedMinutes}분",
                    pricePoints = 0, // (필요 시 dto.route?.estimatedPrice ?: 0)

                    // ✅ [핵심] 좌표 정보 매핑 (TODO 제거)
                    startLatitude = startLat,
                    startLongitude = startLng,
                    endLatitude = endLat,
                    endLongitude = endLng
                )
            } else {
                throw Exception("상세 조회 실패: ${response.message()}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
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

                Log.e("API_ERROR", "요청 실패 - Code: $code, Msg: $message")
                Log.e("API_ERROR", "ErrorBody: $errorBody")

                Result.failure(Exception("API 오류($code): $message"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("API_ERROR", "네트워크 예외 발생: ${e.message}")
            Result.failure(e)
        }
    }

    // 기존 함수들의 구현도 필요하다면 여기에...
}