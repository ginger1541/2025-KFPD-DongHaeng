// src/routes/review.routes.ts
import { Router } from 'express';
import * as reviewController from '../controllers/review.controller';
import { authenticate } from '../middlewares/auth.middleware';
import { validate } from '../middlewares/validation.middleware';
import { createReviewSchema } from '../validators/review.validator';

const router = Router();

// 모든 review 라우트는 인증 필요
router.use(authenticate);

// 후기 작성
router.post('/', validate(createReviewSchema), reviewController.createReview);

export default router;
