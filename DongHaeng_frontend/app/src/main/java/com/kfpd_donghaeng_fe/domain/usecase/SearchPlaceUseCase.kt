package com.kfpd_donghaeng_fe.domain.usecase

import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.domain.repository.PlaceRepository
import javax.inject.Inject

/**
 * 장소 검색 UseCase
 * 비즈니스 로직: 빈 검색어 필터링
 */
class SearchPlaceUseCase @Inject constructor(
    private val repository: PlaceRepository
) {
    suspend operator fun invoke(
        query: String,
        longitude: String? = null,
        latitude: String? = null,
        radius: Int? = null
    ): Result<List<PlaceSearchResult>> {
        // 비즈니스 로직: 검색어가 비어있으면 빈 리스트 반환
        if (query.isBlank()) {
            return Result.success(emptyList())
        }

        // Repository를 통해 데이터 가져오기
        return repository.searchPlaces(query, longitude, latitude, radius)
    }
}