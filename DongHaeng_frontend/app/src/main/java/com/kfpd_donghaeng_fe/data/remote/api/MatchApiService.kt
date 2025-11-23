package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.MatchListResponse
import retrofit2.Response
import retrofit2.http.GET

interface MatchApiService {
    @GET("api/matches")
    suspend fun getMatches(): Response<MatchListResponse>
}