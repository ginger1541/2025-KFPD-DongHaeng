// src/routes/notification.routes.ts
import { Router } from 'express';
import * as notificationController from '../controllers/notification.controller';
import { authenticate } from '../middlewares/auth.middleware';

const router = Router();

// 모든 notification 라우트는 인증 필요
router.use(authenticate);

// 알림 목록 조회
router.get('/', notificationController.getNotifications);

// 알림 읽음 처리
router.put('/:notificationId/read', notificationController.markAsRead);

export default router;
