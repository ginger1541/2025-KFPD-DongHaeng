package com.kfpd_donghaeng_fe.domain.repository

import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult

interface PlaceRepository {
    /**
     * 장소 검색
     * @param query 검색어
     * @param longitude 현재 위치 경도 (옵션)
     * @param latitude 현재 위치 위도 (옵션)
     * @param radius 검색 반경(m) (옵션)
     * @return 검색 결과 리스트
     */
    suspend fun searchPlaces(
        query: String,
        longitude: String? = null,
        latitude: String? = null,
        radius: Int? = null
    ): Result<List<PlaceSearchResult>>
}