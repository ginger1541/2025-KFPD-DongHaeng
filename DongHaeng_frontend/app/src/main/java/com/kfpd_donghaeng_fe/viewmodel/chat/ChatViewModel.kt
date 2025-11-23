package com.kfpd_donghaeng_fe.viewmodel.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.ChatMessageUiModel
import com.kfpd_donghaeng_fe.data.ChatRoomUiModel
import com.kfpd_donghaeng_fe.data.MatchingInfoUiModel
import com.kfpd_donghaeng_fe.data.MessageType
import com.kfpd_donghaeng_fe.data.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository
) : ViewModel() {

    // 채팅방 목록 상태
    private val _chatRooms = MutableStateFlow<List<ChatRoomUiModel>>(emptyList())
    val chatRooms: StateFlow<List<ChatRoomUiModel>> = _chatRooms.asStateFlow()

    // 현재 열린 채팅방의 메시지들
    private val _currentMessages = MutableStateFlow<List<ChatMessageUiModel>>(emptyList())
    val currentMessages: StateFlow<List<ChatMessageUiModel>> = _currentMessages.asStateFlow()

    // 내 사용자 ID (메시지 구분용, 로그인 시 저장된 값 필요)
    private var myUserId: Long = -1

    // 1. 초기화: 소켓 연결 및 목록 로드
    fun initialize(token: String, userId: Long) {
        myUserId = userId
        repository.initialize(token)
        loadChatRooms()

        // 실시간 메시지 수신 대기
        viewModelScope.launch {
            repository.observeNewMessages().collect { data ->
                val senderId = data.optLong("senderId")
                val message = data.optString("message")
                val newMessage = ChatMessageUiModel(
                    id = System.currentTimeMillis(),
                    type = if (senderId == myUserId) MessageType.TALK_ME else MessageType.TALK_OTHER,
                    text = message,
                    timestamp = "방금"
                )
                _currentMessages.update { it + newMessage }
            }
        }
    }

    // 2. 채팅방 목록 가져오기
    fun loadChatRooms() {
        viewModelScope.launch {
            repository.getChatRooms().onSuccess { rooms ->
                val uiList = rooms.map { dto ->
                    ChatRoomUiModel(
                        id = dto.chatRoomId,
                        partnerName = dto.partner.nickname,
                        partnerProfileRes = null, // 이미지 URL 처리 필요
                        lastMessage = dto.lastMessage?.message ?: "대화를 시작해보세요",
                        unreadCount = dto.unreadCount,
                        timestamp = dto.lastMessage?.createdAt ?: "" // 포맷팅 필요
                    )
                }
                _chatRooms.value = uiList
            }
        }
    }

    // 3. 채팅방 입장 (상세 조회 + 히스토리 로드 + 소켓 조인)
    fun enterChatRoom(chatRoomId: Long) {
        viewModelScope.launch {
            // 소켓 룸 입장
            repository.joinRoom(chatRoomId) // matchId == chatRoomId [cite: 515]

            // 상세 정보(매칭 정보) 로드
            var matchingInfoMsg: ChatMessageUiModel? = null
            repository.getChatRoomDetail(chatRoomId).onSuccess { detail ->
                detail.request?.let { req ->
                    // 매칭 카드 메시지 생성
                    matchingInfoMsg = ChatMessageUiModel(
                        id = 0,
                        type = MessageType.MATCHING_CARD,
                        matchingInfo = MatchingInfoUiModel(
                            date = req.scheduledAt ?: "날짜 미정",
                            startPlace = req.startAddress,
                            startTime = "출발",
                            endPlace = req.endAddress,
                            endTime = "도착",
                            cost = 0 // 가격 정보는 API detail에 없으면 추가 필요
                        )
                    )
                }
            }

            // 히스토리 로드
            repository.getMessageHistory(chatRoomId).onSuccess { history ->
                val uiMessages = history.map { msg ->
                    ChatMessageUiModel(
                        id = msg.messageId,
                        type = if (msg.senderId == myUserId) MessageType.TALK_ME else MessageType.TALK_OTHER,
                        text = msg.message,
                        timestamp = msg.createdAt // 포맷팅 필요
                    )
                }.reversed() // 최신순 -> 과거순 정렬 변경 필요 시 조정

                // 매칭 카드를 맨 앞에 추가
                val finalMessages = if(matchingInfoMsg != null) listOf(matchingInfoMsg!!) + uiMessages else uiMessages

                _currentMessages.value = finalMessages

                // 읽음 처리: 로컬에서 unreadCount 0으로 갱신
                _chatRooms.update { list ->
                    list.map { if (it.id == chatRoomId) it.copy(unreadCount = 0) else it }
                }
            }
        }
    }

    // 4. 메시지 전송
    fun sendMessage(chatRoomId: Long, text: String) {
        if (text.isBlank()) return

        // 소켓 전송
        repository.sendMessage(chatRoomId, text)

        // 낙관적 업데이트 (내 화면에 바로 표시)
        val myMsg = ChatMessageUiModel(
            id = System.currentTimeMillis(),
            type = MessageType.TALK_ME,
            text = text,
            timestamp = "방금"
        )
        _currentMessages.update { it + myMsg }
    }

    override fun onCleared() {
        super.onCleared()
        repository.disconnectSocket()
    }
}