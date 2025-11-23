package com.kfpd_donghaeng_fe.data.remote.api

import com.kfpd_donghaeng_fe.data.remote.dto.ChatHistoryResponse
import com.kfpd_donghaeng_fe.data.remote.dto.ChatRoomDetailResponse
import com.kfpd_donghaeng_fe.data.remote.dto.ChatRoomListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Header

interface ChatApiService {
    // 채팅방 목록 조회 [cite: 502, 503]
    @GET("api/chat-rooms")
    suspend fun getChatRooms(
        @Header("Authorization") token: String,
        @Query("status") status: String = "active" // active, completed, all
    ): Response<ChatRoomListResponse>

    // 단일 채팅방 정보 조회 [cite: 539, 540]
    @GET("api/chat-rooms/{chatRoomId}")
    suspend fun getChatRoomDetail(
        @Header("Authorization") token: String,
        @Path("chatRoomId") chatRoomId: Long
    ): Response<ChatRoomDetailResponse>

    // 메시지 히스토리 조회 [cite: 567, 568]
    @GET("api/chat-rooms/{chatRoomId}/messages")
    suspend fun getMessageHistory(
        @Header("Authorization") token: String,
        @Path("chatRoomId") chatRoomId: Long,
        @Query("limit") limit: Int = 50,
        @Query("before_id") beforeId: Long? = null
    ): Response<ChatHistoryResponse>
}