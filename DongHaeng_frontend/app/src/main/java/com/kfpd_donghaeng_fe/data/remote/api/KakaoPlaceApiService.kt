package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.BuildConfig
import com.kfpd_donghaeng_fe.data.remote.dto.KakaoPlaceResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface KakaoPlaceApiService {

    @GET("v2/local/search/keyword.json")
    suspend fun searchPlaces(
        @Query("query") query: String,
        @Query("x") longitude: String? = null,
        @Query("y") latitude: String? = null,
        @Query("radius") radius: Int? = null,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 15
    ): Response<KakaoPlaceResponse> // DTO 이름은 이미 정의되어 있다고 가정
}