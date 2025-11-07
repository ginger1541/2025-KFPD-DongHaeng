import { Request, Response, NextFunction } from 'express';
import { logger } from '../config/logger';

// 커스텀 에러 클래스
export class AppError extends Error {
  code: string;
  statusCode: number;
  isOperational: boolean;

  constructor(messageOrCode: string, statusCodeOrMessage: number | string, statusCode?: number) {
    // 시그니처 1: (message, statusCode) - 기존 방식
    if (typeof statusCodeOrMessage === 'number') {
      super(messageOrCode);
      this.code = 'ERROR';
      this.statusCode = statusCodeOrMessage;
    }
    // 시그니처 2: (code, message, statusCode) - 새로운 방식
    else {
      super(statusCodeOrMessage);
      this.code = messageOrCode;
      this.statusCode = statusCode || 500;
    }

    this.isOperational = true;
    Error.captureStackTrace(this, this.constructor);
  }
}

// 에러 핸들러 미들웨어
export const errorHandler = (
  err: Error | AppError,
  req: Request,
  res: Response,
  _next: NextFunction
) => {
  // AppError 인스턴스인 경우
  if (err instanceof AppError) {
    logger.error(`[${err.statusCode}] ${err.code}: ${err.message}`, {
      path: req.path,
      method: req.method,
      stack: err.stack,
    });

    return res.status(err.statusCode).json({
      success: false,
      error: {
        code: err.code,
        message: err.message,
        ...(process.env.NODE_ENV === 'development' && { details: err.stack }),
      },
    });
  }

  // Prisma 에러 처리
  if (err.name === 'PrismaClientKnownRequestError') {
    logger.error('Prisma Error:', err);
    return res.status(400).json({
      success: false,
      message: 'Database error occurred',
      ...(process.env.NODE_ENV === 'development' && { error: err.message }),
    });
  }

  // JWT 에러 처리
  if (err.name === 'JsonWebTokenError') {
    return res.status(401).json({
      success: false,
      message: 'Invalid token',
    });
  }

  if (err.name === 'TokenExpiredError') {
    return res.status(401).json({
      success: false,
      message: 'Token expired',
    });
  }

  // Validation 에러 처리 (Joi)
  if (err.name === 'ValidationError') {
    return res.status(400).json({
      success: false,
      message: 'Validation error',
      errors: err.message,
    });
  }

  // 기타 예상치 못한 에러
  logger.error('Unexpected Error:', {
    message: err.message,
    stack: err.stack,
    path: req.path,
    method: req.method,
  });

  return res.status(500).json({
    success: false,
    message: 'Internal server error',
    ...(process.env.NODE_ENV === 'development' && {
      error: err.message,
      stack: err.stack,
    }),
  });
};

// Async 에러 핸들러 래퍼
export const asyncHandler = (fn: Function) => {
  return (req: Request, res: Response, next: NextFunction) => {
    Promise.resolve(fn(req, res, next)).catch(next);
  };
};
