package com.kfpd_donghaeng_fe.data

// 1. 채팅방 목록 아이템 모델
data class ChatRoomUiModel(
    val id: Long,
    val partnerName: String,
    val partnerProfileRes: Int?, // 프로필 이미지 리소스 (임시)
    val lastMessage: String,
    val unreadCount: Int,
    val timestamp: String
)

// 2. 메시지 타입 (매칭카드, 시스템, 내 메시지, 상대 메시지)
enum class MessageType {
    MATCHING_CARD, // 상단 매칭 정보 카드
    SYSTEM,        // "매칭되었습니다" 알림
    TALK_ME,       // 내가 보낸 메시지
    TALK_OTHER     // 상대방이 보낸 메시지
}

// 3. 매칭 카드 정보 (날짜, 장소, 시간, 금액 등)
data class MatchingInfoUiModel(
    val date: String,      // "8월 13일"
    val startPlace: String, // "서강대학교..."
    val startTime: String,  // "17:10 출발"
    val endPlace: String,   // "루프 홍대점"
    val endTime: String,    // "17:30 도착"
    val cost: Int           // 6000
)

// 4. 개별 메시지 아이템 모델
data class ChatMessageUiModel(
    val id: Long,
    val type: MessageType,
    val text: String? = null,          // 일반 메시지일 때 내용
    val timestamp: String? = null,     // 시간
    val matchingInfo: MatchingInfoUiModel? = null // 매칭 카드일 때 정보
)