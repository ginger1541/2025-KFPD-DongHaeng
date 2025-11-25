import './utils/bigint-serializer'; // BigInt JSON 직렬화 지원
import express, { Application, Request, Response } from 'express';
import cors from 'cors';
import helmet from 'helmet';
import morgan from 'morgan';
import path from 'path';
import { logger } from './config/logger';
import routes from './routes';
import { errorHandler } from './middlewares/error.middleware';

const app: Application = express();

// ============================================
// 보안 미들웨어
// ============================================
app.use(helmet()); // HTTP 헤더 보안
app.use(cors({
  origin: process.env.CORS_ORIGIN?.split(',') || '*',
  credentials: true,
}));

// ============================================
// 요청 파싱 미들웨어
// ============================================
app.use(express.json({ limit: '10mb' }));
app.use(express.urlencoded({ extended: true, limit: '10mb' }));

// ============================================
// Static 파일 서빙 (QR 이미지)
// ============================================
app.use('/qr', express.static(path.join(__dirname, '../public/qr')));

// ============================================
// 로깅 미들웨어
// ============================================
if (process.env.NODE_ENV === 'development') {
  app.use(morgan('dev'));
} else {
  app.use(morgan('combined', {
    stream: {
      write: (message: string) => logger.info(message.trim()),
    },
  }));
}

// ============================================
// Health Check
// ============================================
app.get('/health', (_req: Request, res: Response) => {
  res.status(200).json({
    status: 'OK',
    timestamp: new Date().toISOString(),
    uptime: process.uptime(),
    environment: process.env.NODE_ENV || 'development',
  });
});

// ============================================
// API Routes
// ============================================
app.use('/api', routes);

// ============================================
// 404 핸들러
// ============================================
app.use((_req: Request, res: Response) => {
  res.status(404).json({
    success: false,
    message: 'Route not found',
    path: _req.path,
  });
});

// ============================================
// 에러 핸들러 (마지막에 위치)
// ============================================
app.use(errorHandler);

export default app;
