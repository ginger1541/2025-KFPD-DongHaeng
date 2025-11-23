package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.data.remote.api.ChatApiService
import com.kfpd_donghaeng_fe.data.remote.dto.ChatMessageDto
import com.kfpd_donghaeng_fe.data.remote.dto.ChatRoomDto
import com.kfpd_donghaeng_fe.data.remote.socket.SocketManager
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val apiService: ChatApiService,
    private val socketManager: SocketManager
) {
    // 토큰 저장 변수
    private var jwtToken: String = ""

    // 초기화 시 토큰 저장 및 소켓 연결
    fun initialize(token: String) {
        this.jwtToken = "Bearer $token" // "Bearer " 접두어 미리 붙임
        socketManager.connect(token)
    }

    // --- API Calls ---
    suspend fun getChatRooms(): Result<List<ChatRoomDto>> {
        return try {
            val response = apiService.getChatRooms(jwtToken)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch chat rooms"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getChatRoomDetail(chatRoomId: Long): Result<ChatRoomDto> {
        return try {
            val response = apiService.getChatRoomDetail(jwtToken, chatRoomId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch chat room detail"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMessageHistory(chatRoomId: Long, beforeId: Long? = null): Result<List<ChatMessageDto>> {
        return try {
            val response = apiService.getMessageHistory(jwtToken, chatRoomId, beforeId = beforeId)
            if (response.isSuccessful && response.body()?.success == true) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Failed to fetch messages"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // --- Socket Operations ---
    fun connectSocket(token: String) = socketManager.connect(token)
    fun disconnectSocket() = socketManager.disconnect()
    fun joinRoom(matchId: Long) = socketManager.joinRoom(matchId)
    fun sendMessage(matchId: Long, message: String) = socketManager.sendMessage(matchId, message)
    fun observeNewMessages(): Flow<JSONObject> = socketManager.observeMessages()
}