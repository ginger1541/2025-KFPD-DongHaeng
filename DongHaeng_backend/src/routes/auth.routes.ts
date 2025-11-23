import { Router } from 'express';
import * as authController from '../controllers/auth.controller';
import { validate } from '../middlewares/validation.middleware';
import { authenticate } from '../middlewares/auth.middleware';
import { signupSchema, loginSchema, refreshTokenSchema } from '../validators/auth.validator';

const router = Router();

// 회원가입
router.post('/signup', validate(signupSchema), authController.signup);

// 로그인
router.post('/login', validate(loginSchema), authController.login);

// 토큰 갱신
router.post('/refresh', validate(refreshTokenSchema), authController.refresh);

// 로그아웃 (인증 필요)
router.post('/logout', authenticate, authController.logout);

export default router;
