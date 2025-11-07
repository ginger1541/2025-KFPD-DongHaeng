// src/controllers/notification.controller.ts
import { Request, Response } from 'express';
import { asyncHandler } from '../middlewares/error.middleware';
import * as notificationService from '../services/notification.service';

/**
 * 알림 목록 조회
 * GET /api/notifications
 */
export const getNotifications = asyncHandler(async (req: Request, res: Response) => {
  const userId = req.user!.userId;
  const isRead = req.query.is_read === 'true' ? true : req.query.is_read === 'false' ? false : undefined;
  const page = parseInt(req.query.page as string) || 1;
  const perPage = parseInt(req.query.per_page as string) || 20;

  const result = await notificationService.getNotifications(userId, {
    isRead,
    page,
    perPage,
  });

  res.status(200).json({
    success: true,
    data: result.notifications.map((notif) => ({
      notification_id: notif.id.toString(),
      type: notif.notificationType,
      title: notif.title,
      content: notif.content,
      data: notif.data,
      is_read: notif.isRead,
      created_at: notif.createdAt,
    })),
    pagination: {
      page,
      per_page: perPage,
      total: result.total,
      total_pages: Math.ceil(result.total / perPage),
    },
  });
});

/**
 * 알림 읽음 처리
 * PUT /api/notifications/:notificationId/read
 */
export const markAsRead = asyncHandler(async (req: Request, res: Response) => {
  const notificationId = BigInt(req.params.notificationId);
  const userId = req.user!.userId;

  const notification = await notificationService.markAsRead(notificationId, userId);

  res.status(200).json({
    success: true,
    data: {
      notification_id: notification.id.toString(),
      is_read: notification.isRead,
      read_at: notification.readAt,
    },
  });
});
