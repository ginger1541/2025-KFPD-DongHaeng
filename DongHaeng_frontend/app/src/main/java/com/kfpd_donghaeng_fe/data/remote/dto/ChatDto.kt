package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName

// 채팅방 목록 응답
data class ChatRoomListResponse(
    val success: Boolean,
    val data: List<ChatRoomDto>
)

// 채팅방 상세/단일 응답
data class ChatRoomDetailResponse(
    val success: Boolean,
    val data: ChatRoomDto
)

// 채팅방 정보 DTO
data class ChatRoomDto(
    @SerializedName("chatRoomId", alternate = ["chat_room_id", "chatroomid", "ChatRoomId"])
    val chatRoomId: Long,

    @SerializedName("matchId", alternate = ["match_id", "matchid", "MatchId"])
    val matchId: Long,

    @SerializedName("partner", alternate = ["Partner", "PARTNER"])
    val partner: ChatPartnerDto,

    @SerializedName("lastMessage", alternate = ["last_message", "lastmessage", "LastMessage"])
    val lastMessage: LastMessageDto?,

    @SerializedName("unreadCount", alternate = ["unread_count", "unreadcount", "UnreadCount"])
    val unreadCount: Int,

    @SerializedName("request", alternate = ["Request", "REQUEST"])
    val request: ChatRequestDto?
)

data class ChatPartnerDto(
    val userId: Long,
    val nickname: String,
    val profileImageUrl: String?
)

data class LastMessageDto(
    val messageId: Long,
    val message: String,
    val createdAt: String
)

data class ChatRequestDto(
    @SerializedName("scheduledAt", alternate = ["scheduled_at", "scheduledat", "ScheduledAt"])
    val scheduledAt: String?,

    @SerializedName("startAddress", alternate = ["start_address", "startaddress", "StartAddress"])
    val startAddress: String?,

    @SerializedName("endAddress", alternate = ["end_address", "endaddress", "EndAddress"])
    val endAddress: String?
)

// 메시지 히스토리 응답
data class ChatHistoryResponse(
    val success: Boolean,
    val data: List<ChatMessageDto>,
    val pagination: ChatPaginationDto
)

data class ChatMessageDto(
    @SerializedName("messageId", alternate = ["message_id", "messageid", "MessageId"])
    val messageId: Long,

    @SerializedName("senderId", alternate = ["sender_id", "senderid", "SenderId"])
    val senderId: Long,

    @SerializedName("message", alternate = ["Message", "MESSAGE"])
    val message: String,

    @SerializedName("createdAt", alternate = ["created_at", "createdat", "CreatedAt"])
    val createdAt: String
)

data class ChatPaginationDto(
    val hasMore: Boolean,
    val nextBeforeId: Long?
)