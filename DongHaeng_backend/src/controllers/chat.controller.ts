import { Request, Response } from 'express';
import * as chatService from '../services/chat.service';
import { asyncHandler } from '../middlewares/error.middleware';

// PDF 요구사항 2-2: 단일 채팅방 조회
export const getChatRoomById = asyncHandler(async (req: Request, res: Response) => {
  const chatRoomId = BigInt(req.params.id);
  const chatRoom = await chatService.getChatRoomById(chatRoomId, req.user!.userId);

  res.status(200).json({
    success: true,
    data: chatRoom,
  });
});

// PDF 요구사항 2-3: 채팅 메시지 히스토리 조회
export const getChatMessages = asyncHandler(async (req: Request, res: Response) => {
  const chatRoomId = BigInt(req.params.id);
  const { before_id, limit } = req.query;

  const result = await chatService.getChatMessages(
    chatRoomId,
    req.user!.userId,
    before_id ? BigInt(before_id as string) : undefined,
    limit ? parseInt(limit as string, 10) : 50
  );

  res.status(200).json({
    success: true,
    data: result.messages,
    pagination: result.pagination,
  });
});

// PDF 요구사항 2-4: 참여 중 채팅방 목록 조회
export const getMyChatRooms = asyncHandler(async (req: Request, res: Response) => {
  const { status } = req.query;

  const chatRooms = await chatService.getMyChatRooms(
    req.user!.userId,
    status as 'active' | 'completed' | 'all' | undefined
  );

  res.status(200).json({
    success: true,
    data: chatRooms,
  });
});
