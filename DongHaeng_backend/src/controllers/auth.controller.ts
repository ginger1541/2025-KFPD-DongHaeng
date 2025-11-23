import { Request, Response } from 'express';
import * as authService from '../services/auth.service';
import { asyncHandler } from '../middlewares/error.middleware';
import '../types/common.types';

// 회원가입
export const signup = asyncHandler(async (req: Request, res: Response) => {
  const result = await authService.signup(req.body);

  res.status(201).json({
    success: true,
    message: '회원가입이 완료되었습니다',
    data: result,
  });
});

// 로그인
export const login = asyncHandler(async (req: Request, res: Response) => {
  const { email, password } = req.body;
  const result = await authService.login(email, password);

  res.status(200).json({
    success: true,
    message: '로그인 성공',
    data: result,
  });
});

// Refresh Token으로 Access Token 재발급
export const refresh = asyncHandler(async (req: Request, res: Response) => {
  const { refreshToken } = req.body;
  const result = await authService.refresh(refreshToken);

  res.status(200).json({
    success: true,
    message: '토큰이 갱신되었습니다',
    data: result,
  });
});

// 로그아웃
export const logout = asyncHandler(async (req: Request, res: Response) => {
  await authService.logout(req.user!.userId);

  res.status(200).json({
    success: true,
    message: '로그아웃 되었습니다',
  });
});
