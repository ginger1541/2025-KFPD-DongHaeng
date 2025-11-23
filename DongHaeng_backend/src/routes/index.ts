import { Router } from 'express';
import authRoutes from './auth.routes';
import userRoutes from './user.routes';
import companionRoutes from './companion.routes';
import matchRoutes from './match.routes';
import reviewRoutes from './review.routes';
import notificationRoutes from './notification.routes';
import chatRoutes from './chat.routes'; // PDF 요구사항: 채팅 라우트

const router = Router();

// ============================================
// API 라우트
// ============================================

// Health check at root
router.get('/', (_req, res) => {
  res.json({
    message: 'Companion API Server',
    version: '1.0.0',
    status: 'running',
  });
});

// Auth 라우트
router.use('/auth', authRoutes);

// User 라우트
router.use('/users', userRoutes);

// Companion 라우트
router.use('/companions', companionRoutes);

// Match 라우트
router.use('/matches', matchRoutes);

// Review 라우트
router.use('/reviews', reviewRoutes);

// Notification 라우트
router.use('/notifications', notificationRoutes);

// Chat 라우트 (PDF 요구사항: 채팅 API)
router.use('/chat-rooms', chatRoutes);

export default router;