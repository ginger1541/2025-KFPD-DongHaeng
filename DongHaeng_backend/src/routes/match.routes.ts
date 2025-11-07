// src/routes/match.routes.ts
import { Router } from 'express';
import * as matchController from '../controllers/match.controller';
import { authenticate } from '../middlewares/auth.middleware';
import { validate } from '../middlewares/validation.middleware';
import { scanQRSchema, endCompanionSchema } from '../validators/match.validator';

const router = Router();

// 모든 match 라우트는 인증 필요
router.use(authenticate);

// 내 매칭 목록 조회
router.get('/', matchController.getMyMatches);

// 매칭 상세 조회
router.get('/:id', matchController.getMatchById);

// 동행 시작 QR 생성
router.get('/:id/qr/start', matchController.generateStartQR);

// 동행 시작 (QR 스캔)
router.post('/:id/start', validate(scanQRSchema), matchController.startCompanion);

// 동행 종료 QR 생성
router.get('/:id/qr/end', matchController.generateEndQR);

// 동행 종료 (QR 스캔)
router.post('/:id/end', validate(endCompanionSchema), matchController.endCompanion);

export default router;