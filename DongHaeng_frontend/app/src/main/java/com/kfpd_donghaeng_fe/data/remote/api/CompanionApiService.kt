package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.BaseResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.CompanionRequestDetailDto
import com.kfpd_donghaeng_fe.data.remote.dto.MatchResponseDto
import com.kfpd_donghaeng_fe.data.remote.dto.NearbyResponseData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface CompanionApiService {

    @GET("/api/companions/requests/nearby")
    suspend fun getNearbyRequests(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radiusKm") radiusKm: Int = 5,
        @Query("limit") limit: Int = 20
    ): Response<BaseResponseDto<NearbyResponseData>>

    @GET("/api/companions/requests/{requestId}")
    suspend fun getRequestDetail(
        @Path("requestId") requestId: Long
    ): Response<BaseResponseDto<CompanionRequestDetailDto>>

    @POST("/api/companions/requests/{requestId}/accept")
    suspend fun acceptRequest(
        @Path("requestId") requestId: Long
    ): Response<BaseResponseDto<MatchResponseDto>>
}