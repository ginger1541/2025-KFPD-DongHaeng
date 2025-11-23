package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.data.remote.api.KakaoPlaceApiService
import com.kfpd_donghaeng_fe.data.remote.dto.toDomain
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.domain.repository.PlaceRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton


/**
 * PlaceRepository 구현체
 * Kakao API를 통해 장소 검색
 */

@Singleton
class PlaceRepositoryImpl @Inject constructor(
    private val apiService: KakaoPlaceApiService
) : PlaceRepository {
    override suspend fun searchPlaces(
        query: String,
        longitude: String?,
        latitude: String?,
        radius: Int?
    ): Result<List<PlaceSearchResult>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.searchPlaces(
                query = query,
                longitude = longitude,
                latitude = latitude,
                radius = radius
            )

            if (response.isSuccessful && response.body() != null) {
                // DTO -> Domain Entity 변환
                val places = response.body()!!.documents.map { it.toDomain() }
                Result.success(places)  // ✅ Result.success로 감싸기
            } else {
                Result.failure(Exception("검색 실패: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}