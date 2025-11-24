package com.kfpd_donghaeng_fe.data.repository

import android.util.Log
import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
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
    private val socketManager: SocketManager,
    private val tokenDataSource: TokenLocalDataSource
) {
    // 토큰 저장 변수
    private suspend fun getAuthToken(): String {
        val token = tokenDataSource.getToken() ?: throw Exception("인증 토큰이 없습니다.")
        return "Bearer $token"
    }

    // 💡 [추가] User ID 로드 함수 (메시지 구분 시 사용)
    suspend fun getMyUserId(): Long {
        return tokenDataSource.getUserId() ?: -1L
    }

    // 💡 [추가] 소켓 연결 함수 (토큰을 스스로 가져오도록 수정)
    suspend fun connectSocket() {
        val rawToken = tokenDataSource.getToken() ?: return // 토큰 없으면 연결 시도 안 함
        socketManager.connect(rawToken)
    }

    // --- API Calls ---
    suspend fun getChatRooms(): Result<List<ChatRoomDto>> {
        return try {
            val token = getAuthToken()
            val response = apiService.getChatRooms(token)

            // 👇 [로그 1] API 응답 코드 로깅
            Log.d("ChatRepo", "API 응답 코드: ${response.code()}")

            if (response.isSuccessful) {
                val body = response.body()

                if (body?.success == true) {
                    val rooms = body.data

                    // 👇 [로그 2] 가져온 방 목록의 ID와 개수 로깅
                    Log.d("ChatRepo", "✅ API 성공, 채팅방 개수: ${rooms.size}")
                    rooms.forEach {
                        Log.d("ChatRepo", "채팅방 ID: ${it.chatRoomId}, 파트너: ${it.partner.nickname}")
                    }

                    Result.success(rooms)
                } else {
                    // 서버 응답은 받았으나 success: false인 경우
                    Log.e("ChatRepo", "❌ 서버 응답: success: false (HTTP: ${response.message()})")
                    Result.failure(Exception("Failed to fetch chat rooms: Server response failed."))
                }
            } else {
                // 🔥 [로그 3] HTTP 에러 (4xx, 5xx)인 경우
                val errorBody = response.errorBody()?.string() ?: response.message()
                Log.e("ChatRepo", "🔥 HTTP 오류: ${response.code()}, 상세: $errorBody")
                Result.failure(Exception("HTTP Error ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e("ChatRepo", "💥 네트워크 예외 발생: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun getChatRoomDetail(chatRoomId: Long): Result<ChatRoomDto> {
        return try {
            val token = getAuthToken() // 💡 토큰 로드
            val response = apiService.getChatRoomDetail(token, chatRoomId)
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
            val token = getAuthToken()
            val response = apiService.getMessageHistory(token, chatRoomId, beforeId = beforeId)
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