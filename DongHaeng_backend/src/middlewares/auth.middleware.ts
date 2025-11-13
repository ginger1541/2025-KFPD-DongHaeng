import { Request, Response, NextFunction } from 'express';
import { verifyAccessToken } from '../utils/jwt.util';
import { AppError } from './error.middleware';

// JWT 인증 미들웨어
export const authenticate = (req: Request, _res: Response, next: NextFunction) => {
  try {
    // Authorization 헤더에서 토큰 추출
    const authHeader = req.headers.authorization;

    if (!authHeader || !authHeader.startsWith('Bearer ')) {
      throw new AppError('No token provided', 401);
    }

    const token = authHeader.substring(7); // "Bearer " 이후의 토큰

    // 토큰 검증
    const decoded = verifyAccessToken(token);

    // req.user에 사용자 정보 저장
    req.user = {
      userId: decoded.userId,
      email: decoded.email,
      userType: decoded.userType,
    };

    next();
  } catch (error) {
    if (error instanceof AppError) {
      next(error);
    } else {
      next(new AppError('Invalid token', 401));
    }
  }
};
