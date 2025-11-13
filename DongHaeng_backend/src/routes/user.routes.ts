import { Router } from 'express';
import * as userController from '../controllers/user.controller';
import * as reviewController from '../controllers/review.controller';
import { authenticate } from '../middlewares/auth.middleware';
import { upload, handleMulterError } from '../middlewares/upload.middleware';

const router = Router();

// 모든 user 라우트는 인증 필요
router.use(authenticate);

// 내 프로필 조회
router.get('/me', userController.getMyProfile);

// 내 프로필 수정
router.put('/me', userController.updateMyProfile);

// 프로필 이미지 업로드
router.post(
  '/:userId/profile-image',
  upload.single('image'),
  handleMulterError,
  userController.uploadProfileImage
);

// 사용자 유형 설정
router.put('/:userId/user-type', userController.updateUserType);

// 약관 동의
router.post('/:userId/consents', userController.updateConsents);

// 특정 사용자 프로필 조회
router.get('/:userId', userController.getUserProfile);

// 특정 사용자가 받은 후기 조회
router.get('/:userId/reviews', reviewController.getUserReviews);

export default router;
