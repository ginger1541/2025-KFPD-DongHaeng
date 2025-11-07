import { Router } from 'express';
import authRoutes from './auth.routes';
import userRoutes from './user.routes';
import companionRoutes from './companion.routes';
import matchRoutes from './match.routes';
import reviewRoutes from './review.routes';
import notificationRoutes from './notification.routes';

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

export default router;