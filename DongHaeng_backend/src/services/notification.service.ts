// src/services/notification.service.ts
import { PrismaClient, NotificationType } from '@prisma/client';
import { AppError } from '../middlewares/error.middleware';

const prisma = new PrismaClient();

interface GetNotificationsOptions {
  isRead?: boolean;
  page: number;
  perPage: number;
}

/**
 * 알림 목록 조회
 */
export const getNotifications = async (
  userId: bigint,
  options: GetNotificationsOptions
) => {
  const { isRead, page, perPage } = options;

  const where: any = { userId };
  if (isRead !== undefined) {
    where.isRead = isRead;
  }

  const [notifications, total] = await Promise.all([
    prisma.notification.findMany({
      where,
      orderBy: { createdAt: 'desc' },
      skip: (page - 1) * perPage,
      take: perPage,
    }),
    prisma.notification.count({ where }),
  ]);

  return {
    notifications,
    total,
  };
};

/**
 * 알림 읽음 처리
 */
export const markAsRead = async (notificationId: bigint, userId: bigint) => {
  // 1. 알림 존재 확인
  const notification = await prisma.notification.findUnique({
    where: { id: notificationId },
  });

  if (!notification) {
    throw new AppError(
      'NOTIFICATION_NOT_FOUND',
      '알림을 찾을 수 없습니다',
      404
    );
  }

  // 2. 권한 확인 (자신의 알림인지)
  if (notification.userId !== userId) {
    throw new AppError(
      'NOTIFICATION_FORBIDDEN',
      '해당 알림에 접근할 수 없습니다',
      403
    );
  }

  // 3. 읽음 처리
  const updatedNotification = await prisma.notification.update({
    where: { id: notificationId },
    data: {
      isRead: true,
      readAt: new Date(),
    },
  });

  return updatedNotification;
};

/**
 * 알림 생성 (내부 사용)
 */
export const createNotification = async (data: {
  userId: bigint;
  type: NotificationType;
  title: string;
  content: string;
  data?: any;
}) => {
  const notification = await prisma.notification.create({
    data: {
      userId: data.userId,
      notificationType: data.type,
      title: data.title,
      content: data.content,
      data: data.data ? JSON.parse(JSON.stringify(data.data)) : null,
    },
  });

  // TODO: Push 알림 전송 (FCM, APNs 등)

  return notification;
};
