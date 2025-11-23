import { Router } from 'express';
import * as companionController from '../controllers/companion.controller';
import { authenticate } from '../middlewares/auth.middleware';
import { validate, validateQuery } from '../middlewares/validation.middleware';
import {
  createRequestSchema,
  updateRequestSchema,
  nearbyRequestsSchema,
} from '../validators/companion.validator';

const router = Router();

// 모든 companion 라우트는 인증 필요
router.use(authenticate);

// 주변 요청 조회 (쿼리 파라미터)
router.get(
  '/requests/nearby',
  validateQuery(nearbyRequestsSchema),
  companionController.getNearbyRequests
);

// 내 요청 목록
router.get('/requests', companionController.getMyRequests);

// 요청 생성
router.post('/requests', validate(createRequestSchema), companionController.createRequest);

// 요청 수락 (매칭 생성)
router.post('/requests/:id/accept', companionController.acceptRequest);

// 요청 상세 조회
router.get('/requests/:id', companionController.getRequestById);

// 요청 수정
router.put('/requests/:id', validate(updateRequestSchema), companionController.updateRequest);

// 요청 취소 (삭제)
router.delete('/requests/:id', companionController.cancelRequest);

export default router;
