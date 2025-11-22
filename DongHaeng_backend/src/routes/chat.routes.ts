import { Router } from 'express';
import * as chatController from '../controllers/chat.controller';
import { authenticate } from '../middlewares/auth.middleware';

const router = Router();

// 모든 채팅 라우트는 인증 필요
router.use(authenticate);

// PDF 요구사항 2-2: 단일 채팅방 조회
// GET /api/v1/chat-rooms/:id
router.get('/:id', chatController.getChatRoomById);

// PDF 요구사항 2-3: 채팅 메시지 히스토리 조회
// GET /api/v1/chat-rooms/:id/messages?before_id=&limit=
router.get('/:id/messages', chatController.getChatMessages);

// PDF 요구사항 2-4: 참여 중 채팅방 목록 조회
// GET /api/v1/chat-rooms?status=active
router.get('/', chatController.getMyChatRooms);

export default router;
