package com.kfpd_donghaeng_fe.data.remote.socket

import android.util.Log
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor() {
    private var socket: Socket? = null
    private val BASE_URL = "http://34.64.76.147:3000" // [cite: 7, 610]

    // 소켓 연결 [cite: 610, 611]
    fun connect(token: String) {
        if (socket?.connected() == true) return

        try {
            val options = IO.Options().apply {
                auth = mapOf("token" to token) // JWT 토큰 인증
                transports = arrayOf("websocket")
            }
            socket = IO.socket(BASE_URL, options)

            socket?.on(Socket.EVENT_CONNECT) {
                Log.d("SocketManager", "Connected")
            }
            socket?.on(Socket.EVENT_CONNECT_ERROR) { args ->
                Log.e("SocketManager", "Connect Error: ${args[0]}")
            }

            socket?.connect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        socket?.disconnect()
        socket?.off()
        socket = null
    }

    // 채팅방 입장 (매칭 참여) [cite: 615]
    fun joinRoom(matchId: Long) {
        val data = JSONObject().put("matchId", matchId)
        // 👇 [추가된 로그] 채팅방 입장 시도 확인
        Log.d("SocketManager", "ATTEMPT: join:match for ID: $matchId")
        socket?.emit("join:match", data)
    }

    // 메시지 전송
    fun sendMessage(matchId: Long, message: String) {
        val data = JSONObject().apply {
            put("matchId", matchId)
            put("message", message)
        }
        // 👇 [추가된 로그] 메시지 전송 시도 확인
        Log.d("SocketManager", "ATTEMPT: chat:send to ID: $matchId. Message: $message")
        socket?.emit("chat:send", data)
    }

    // 메시지 수신 Flow [cite: 634, 636]
    fun observeMessages(): Flow<JSONObject> = callbackFlow {
        val listener = io.socket.emitter.Emitter.Listener { args ->
            if (args.isNotEmpty()) {
                val data = args[0] as JSONObject
                trySend(data)
            }
        }
        socket?.on("chat:message", listener)
        awaitClose { socket?.off("chat:message", listener) }
    }
}