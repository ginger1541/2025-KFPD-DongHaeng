// src/middlewares/upload.middleware.ts
import multer from 'multer';
import path from 'path';
import { Request } from 'express';
import { AppError } from './error.middleware';

// 파일 저장 설정
const storage = multer.diskStorage({
  destination: (_req, _file, cb) => {
    // 실제 프로덕션에서는 클라우드 스토리지 (S3, GCS 등) 사용 권장
    cb(null, 'uploads/profiles');
  },
  filename: (req, file, cb) => {
    // 파일명: userId_timestamp.확장자
    const userId = (req as any).user?.userId || 'unknown';
    const timestamp = Date.now();
    const ext = path.extname(file.originalname);
    cb(null, `${userId}_${timestamp}${ext}`);
  },
});

// 파일 필터 (이미지만 허용)
const fileFilter = (
  _req: Request,
  file: Express.Multer.File,
  cb: multer.FileFilterCallback
) => {
  const allowedMimes = ['image/jpeg', 'image/jpg', 'image/png'];

  if (allowedMimes.includes(file.mimetype)) {
    cb(null, true);
  } else {
    const error = new AppError(
      'INVALID_FILE_TYPE',
      '이미지 파일만 업로드 가능합니다 (jpg, png)',
      400
    ) as any;
    cb(error, false);
  }
};

// Multer 인스턴스
export const upload = multer({
  storage,
  fileFilter,
  limits: {
    fileSize: 5 * 1024 * 1024, // 5MB
  },
});

// 에러 처리 미들웨어
export const handleMulterError = (err: any, _req: Request, res: any, next: any) => {
  if (err instanceof multer.MulterError) {
    if (err.code === 'LIMIT_FILE_SIZE') {
      return res.status(413).json({
        success: false,
        error: {
          code: 'FILE_TOO_LARGE',
          message: '파일 크기는 5MB 이하여야 합니다',
        },
      });
    }
  }
  next(err);
};
