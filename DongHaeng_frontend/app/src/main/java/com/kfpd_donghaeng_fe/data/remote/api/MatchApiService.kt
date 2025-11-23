package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.MatchDetailResponseWrapper
import com.kfpd_donghaeng_fe.data.remote.dto.MatchListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface MatchApiService {
    // 목록 조회
    @GET("api/matches")
    suspend fun getMatches(): Response<MatchListResponse>

    // 상세 조회
    @GET("api/matches/{matchId}")
    suspend fun getMatchDetail(
        @Path("matchId") matchId: Long
    ): Response<MatchDetailResponseWrapper>
}