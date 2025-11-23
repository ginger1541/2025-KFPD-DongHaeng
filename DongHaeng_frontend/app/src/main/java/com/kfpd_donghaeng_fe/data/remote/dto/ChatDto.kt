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
    @SerializedName("chatRoomId") val chatRoomId: Long,
    @SerializedName("matchId") val matchId: Long,
    @SerializedName("partner") val partner: ChatPartnerDto,
    @SerializedName("lastMessage") val lastMessage: LastMessageDto?,
    @SerializedName("unreadCount") val unreadCount: Int,
    @SerializedName("request") val request: ChatRequestDto? // 매칭 카드 정보용
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
    val scheduledAt: String?,
    val startAddress: String,
    val endAddress: String
)

// 메시지 히스토리 응답
data class ChatHistoryResponse(
    val success: Boolean,
    val data: List<ChatMessageDto>,
    val pagination: ChatPaginationDto
)

data class ChatMessageDto(
    val messageId: Long,
    val senderId: Long,
    val message: String,
    val createdAt: String
)

data class ChatPaginationDto(
    val hasMore: Boolean,
    val nextBeforeId: Long?
)