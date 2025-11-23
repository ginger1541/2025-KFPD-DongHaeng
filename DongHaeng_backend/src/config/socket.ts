// src/config/socket.ts
import { Server as HttpServer } from 'http';
import { Server as SocketServer, Socket } from 'socket.io';
import { verifyAccessToken } from '../utils/jwt.util';
import logger from './logger';

// Socket 사용자 정보 타입
interface SocketUser {
  userId: bigint;
  email: string;
  userType: 'requester' | 'helper' | 'both';
}

// Socket with user 정보
interface AuthenticatedSocket extends Socket {
  user?: SocketUser;
}

// 온라인 사용자 관리 (userId -> socketId)
const onlineUsers = new Map<string, string>();

// 매칭별 룸 관리 (matchId -> [requesterSocketId, helperSocketId])
const matchRooms = new Map<string, Set<string>>();

let io: SocketServer;

// Socket.io 서버 초기화
export const initializeSocket = (httpServer: HttpServer): SocketServer => {
  io = new SocketServer(httpServer, {
    cors: {
      origin: process.env.CORS_ORIGIN || '*',
      methods: ['GET', 'POST'],
      credentials: true,
    },
    pingTimeout: 60000,
    pingInterval: 25000,
  });

  // 인증 미들웨어
  io.use(async (socket: AuthenticatedSocket, next) => {
    try {
      const token = socket.handshake.auth.token || socket.handshake.headers.authorization?.replace('Bearer ', '');

      if (!token) {
        return next(new Error('Authentication token missing'));
      }

      const decoded = verifyAccessToken(token);
      socket.user = decoded;

      logger.info('Socket authenticated', {
        socketId: socket.id,
        userId: decoded.userId.toString(),
      });

      next();
    } catch (error) {
      logger.error('Socket authentication failed', { error });
      next(new Error('Authentication failed'));
    }
  });

  // 연결 이벤트
  io.on('connection', (socket: AuthenticatedSocket) => {
    const userId = socket.user!.userId.toString();

    logger.info('Client connected', {
      socketId: socket.id,
      userId,
    });

    // 온라인 사용자 등록
    onlineUsers.set(userId, socket.id);

    // 온라인 상태 브로드캐스트
    socket.broadcast.emit('user:online', { userId });

    // =========================================
    // 매칭 룸 입장
    // =========================================
    socket.on('match:join', (data: { matchId: string }) => {
      const { matchId } = data;
      const roomName = `match:${matchId}`;

      socket.join(roomName);

      // 매칭 룸에 사용자 추가
      if (!matchRooms.has(matchId)) {
        matchRooms.set(matchId, new Set());
      }
      matchRooms.get(matchId)!.add(socket.id);

      logger.info('User joined match room', {
        userId,
        matchId,
        roomName,
      });

      // 상대방에게 입장 알림
      socket.to(roomName).emit('match:user-joined', {
        userId,
        userType: socket.user!.userType,
      });
    });

    // =========================================
    // 매칭 룸 퇴장
    // =========================================
    socket.on('match:leave', (data: { matchId: string }) => {
      const { matchId } = data;
      const roomName = `match:${matchId}`;

      socket.leave(roomName);

      // 매칭 룸에서 사용자 제거
      if (matchRooms.has(matchId)) {
        matchRooms.get(matchId)!.delete(socket.id);
        if (matchRooms.get(matchId)!.size === 0) {
          matchRooms.delete(matchId);
        }
      }

      logger.info('User left match room', {
        userId,
        matchId,
      });

      // 상대방에게 퇴장 알림
      socket.to(roomName).emit('match:user-left', {
        userId,
      });
    });

    // =========================================
    // 실시간 위치 공유
    // =========================================
    socket.on('location:update', (data: {
      matchId: string;
      latitude: number;
      longitude: number;
    }) => {
      const { matchId, latitude, longitude } = data;
      const roomName = `match:${matchId}`;

      // 같은 매칭 룸의 상대방에게만 전송
      socket.to(roomName).emit('location:updated', {
        userId,
        latitude,
        longitude,
        timestamp: Date.now(),
      });

      logger.debug('Location updated', {
        userId,
        matchId,
        latitude,
        longitude,
      });
    });

    // =========================================
    // 실시간 채팅 (PDF 요구사항: DB 저장 추가)
    // =========================================
    socket.on('chat:send', async (data: {
      matchId: string;
      message: string;
      messageId?: string;
    }) => {
      const { matchId, message } = data;
      const roomName = `match:${matchId}`;

      try {
        // PDF 요구사항: DB에 메시지 저장
        const { saveMessage } = await import('../services/chat.service');
        const savedMessage = await saveMessage({
          matchId: BigInt(matchId),
          senderId: socket.user!.userId,
          message,
        });

        const chatMessage = {
          messageId: savedMessage.message_id.toString(),
          matchId,
          senderId: userId,
          senderName: socket.user!.email.split('@')[0], // 임시 이름
          message: savedMessage.message,
          timestamp: savedMessage.created_at.getTime(),
        };

        // 상대방에게 메시지 전송
        socket.to(roomName).emit('chat:message', chatMessage);

        // 발신자에게 전송 확인
        socket.emit('chat:sent', {
          messageId: chatMessage.messageId,
          timestamp: chatMessage.timestamp,
        });

        logger.info('Chat message saved and sent', {
          userId,
          matchId,
          messageId: savedMessage.message_id.toString(),
          messageLength: message.length,
        });
      } catch (error) {
        logger.error('Failed to save chat message', {
          userId,
          matchId,
          error,
        });

        // 에러를 발신자에게 알림
        socket.emit('chat:error', {
          message: '메시지 전송에 실패했습니다',
          error: String(error),
        });
      }
    });

    // =========================================
    // 메시지 읽음 확인
    // =========================================
    socket.on('chat:read', (data: {
      matchId: string;
      messageId: string;
    }) => {
      const { matchId, messageId } = data;
      const roomName = `match:${matchId}`;

      socket.to(roomName).emit('chat:read-receipt', {
        messageId,
        readBy: userId,
        readAt: Date.now(),
      });
    });

    // =========================================
    // 타이핑 표시
    // =========================================
    socket.on('chat:typing', (data: {
      matchId: string;
      isTyping: boolean;
    }) => {
      const { matchId, isTyping } = data;
      const roomName = `match:${matchId}`;

      socket.to(roomName).emit('chat:typing-status', {
        userId,
        isTyping,
      });
    });

    // =========================================
    // 연결 해제
    // =========================================
    socket.on('disconnect', () => {
      logger.info('Client disconnected', {
        socketId: socket.id,
        userId,
      });

      // 온라인 사용자에서 제거
      onlineUsers.delete(userId);

      // 모든 매칭 룸에서 제거
      matchRooms.forEach((sockets, matchId) => {
        if (sockets.has(socket.id)) {
          sockets.delete(socket.id);
          if (sockets.size === 0) {
            matchRooms.delete(matchId);
          }
        }
      });

      // 오프라인 상태 브로드캐스트
      socket.broadcast.emit('user:offline', { userId });
    });

    // =========================================
    // 에러 핸들링
    // =========================================
    socket.on('error', (error) => {
      logger.error('Socket error', {
        socketId: socket.id,
        userId,
        error,
      });
    });
  });

  logger.info('✅ Socket.io server initialized');

  return io;
};

// Socket.io 인스턴스 가져오기
export const getIO = (): SocketServer => {
  if (!io) {
    throw new Error('Socket.io not initialized');
  }
  return io;
};

// 특정 사용자에게 이벤트 전송
export const emitToUser = (userId: string, event: string, data: any) => {
  const socketId = onlineUsers.get(userId);
  if (socketId && io) {
    io.to(socketId).emit(event, data);
    logger.debug('Event emitted to user', {
      userId,
      event,
    });
  }
};

// 매칭 룸에 이벤트 전송
export const emitToMatch = (matchId: string, event: string, data: any) => {
  const roomName = `match:${matchId}`;
  if (io) {
    io.to(roomName).emit(event, data);
    logger.debug('Event emitted to match room', {
      matchId,
      event,
    });
  }
};

// 온라인 사용자 확인
export const isUserOnline = (userId: string): boolean => {
  return onlineUsers.has(userId);
};

// 온라인 사용자 목록
export const getOnlineUsers = (): string[] => {
  return Array.from(onlineUsers.keys());
};