import prisma from '../config/database';
import { MatchStatus } from '@prisma/client';
import { AppError } from '../middlewares/error.middleware';

// PDF 요구사항 2-2: 단일 채팅방 조회
export const getChatRoomById = async (chatRoomId: bigint, userId: bigint) => {
  // chat_room_id = match_id
  const match = await prisma.match.findUnique({
    where: { matchId: chatRoomId },
    include: {
      request: {
        select: {
          id: true,
          scheduledAt: true,
          startAddress: true,
          destinationAddress: true,
          route: true,
        },
      },
      requester: {
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
        },
      },
      helper: {
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
        },
      },
      chatMessages: {
        orderBy: {
          createdAt: 'desc',
        },
        take: 1, // 마지막 메시지만
      },
    },
  });

  if (!match) {
    throw new AppError('채팅방을 찾을 수 없습니다', 404);
  }

  // 권한 확인: 요청자 또는 도우미만 접근 가능
  if (match.requesterId !== userId && match.helperId !== userId) {
    throw new AppError('채팅방에 접근할 권한이 없습니다', 403);
  }

  // 상대방 정보 결정
  const partner = match.requesterId === userId ? match.helper : match.requester;

  // 안 읽은 메시지 수 계산
  const unreadCount = await prisma.chatMessage.count({
    where: {
      matchId: chatRoomId,
      senderId: { not: userId },
      isRead: false,
    },
  });

  return {
    chat_room_id: match.matchId,
    match_id: match.matchId,
    request_id: match.requestId,
    partner: {
      user_id: partner.id,
      nickname: partner.name,
      profile_image_url: partner.profileImageUrl,
    },
    request: {
      scheduled_at: match.request.scheduledAt,
      start_address: match.request.startAddress,
      end_address: match.request.destinationAddress,
    },
    last_message: match.chatMessages[0]
      ? {
          message_id: match.chatMessages[0].id,
          sender_id: match.chatMessages[0].senderId,
          message: match.chatMessages[0].messageContent,
          created_at: match.chatMessages[0].createdAt,
        }
      : null,
    unread_count: unreadCount,
  };
};

// PDF 요구사항 2-3: 채팅 메시지 히스토리 조회
export const getChatMessages = async (
  chatRoomId: bigint,
  userId: bigint,
  beforeId?: bigint,
  limit: number = 50
) => {
  // 권한 확인
  const match = await prisma.match.findUnique({
    where: { matchId: chatRoomId },
    select: {
      requesterId: true,
      helperId: true,
    },
  });

  if (!match) {
    throw new AppError('채팅방을 찾을 수 없습니다', 404);
  }

  if (match.requesterId !== userId && match.helperId !== userId) {
    throw new AppError('채팅방에 접근할 권한이 없습니다', 403);
  }

  // 메시지 조회 (페이징)
  const messages = await prisma.chatMessage.findMany({
    where: {
      matchId: chatRoomId,
      ...(beforeId && { id: { lt: beforeId } }),
    },
    orderBy: {
      createdAt: 'desc',
    },
    take: limit,
  });

  // 다음 페이지 여부 확인
  const hasMore = messages.length === limit;
  const nextBeforeId = hasMore ? messages[messages.length - 1].id : null;

  return {
    messages: messages.map((msg) => ({
      message_id: msg.id,
      sender_id: msg.senderId,
      message: msg.messageContent,
      created_at: msg.createdAt,
    })),
    pagination: {
      has_more: hasMore,
      next_before_id: nextBeforeId,
    },
  };
};

// PDF 요구사항 2-4: 참여 중 채팅방 목록 조회
export const getMyChatRooms = async (
  userId: bigint,
  status?: 'active' | 'completed' | 'all'
) => {
  const statusFilter =
    status === 'active'
      ? { in: [MatchStatus.pending, MatchStatus.in_progress] }
      : status === 'completed'
      ? MatchStatus.completed
      : undefined;

  const matches = await prisma.match.findMany({
    where: {
      OR: [{ requesterId: userId }, { helperId: userId }],
      ...(statusFilter && { status: statusFilter }),
    },
    include: {
      request: {
        select: {
          id: true,
          scheduledAt: true,
          startAddress: true,
          destinationAddress: true,
          route: true,
        },
      },
      requester: {
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
        },
      },
      helper: {
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
        },
      },
      chatMessages: {
        orderBy: {
          createdAt: 'desc',
        },
        take: 1,
      },
    },
    orderBy: {
      matchedAt: 'desc',
    },
  });

  // 각 채팅방의 안 읽은 메시지 수 계산
  const chatRooms = await Promise.all(
    matches.map(async (match) => {
      const unreadCount = await prisma.chatMessage.count({
        where: {
          matchId: match.matchId,
          senderId: { not: userId },
          isRead: false,
        },
      });

      const partner = match.requesterId === userId ? match.helper : match.requester;

      return {
        chat_room_id: match.matchId,
        match_id: match.matchId,
        request_id: match.requestId,
        partner: {
          user_id: partner.id,
          nickname: partner.name,
          profile_image_url: partner.profileImageUrl,
        },
        request: {
          scheduled_at: match.request.scheduledAt,
          start_address: match.request.startAddress,
          end_address: match.request.destinationAddress,
        },
        last_message: match.chatMessages[0]
          ? {
              message_id: match.chatMessages[0].id,
              message: match.chatMessages[0].messageContent,
              created_at: match.chatMessages[0].createdAt,
            }
          : null,
        unread_count: unreadCount,
      };
    })
  );

  return chatRooms;
};

// Socket.io에서 사용: 메시지 저장
export const saveMessage = async (data: {
  matchId: bigint;
  senderId: bigint;
  message: string;
}) => {
  const savedMessage = await prisma.chatMessage.create({
    data: {
      matchId: data.matchId,
      senderId: data.senderId,
      messageContent: data.message,
    },
  });

  return {
    message_id: savedMessage.id,
    sender_id: savedMessage.senderId,
    message: savedMessage.messageContent,
    created_at: savedMessage.createdAt,
  };
};
