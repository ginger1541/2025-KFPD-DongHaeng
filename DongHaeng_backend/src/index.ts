import dotenv from 'dotenv';
import http from 'http';
import app from './app';
import { logger } from './config/logger';
import { initializeSocket } from './config/socket';

// 환경변수 로드
dotenv.config();

const PORT = process.env.PORT || 3000;

// HTTP 서버 생성 (Socket.io를 위해)
const httpServer = http.createServer(app);

// Socket.io 초기화
initializeSocket(httpServer);

// 서버 시작
const server = httpServer.listen(PORT, () => {
  logger.info(`🚀 Server is running on port ${PORT}`);
  logger.info(`📝 Environment: ${process.env.NODE_ENV || 'development'}`);
  logger.info(`🔌 Socket.io enabled`);
});

// Graceful shutdown
const gracefulShutdown = (signal: string) => {
  logger.info(`\n${signal} received. Shutting down gracefully...`);
  
  server.close(() => {
    logger.info('✅ HTTP server closed');
    process.exit(0);
  });

  // 10초 후 강제 종료
  setTimeout(() => {
    logger.error('❌ Forced shutdown after timeout');
    process.exit(1);
  }, 10000);
};

process.on('SIGTERM', () => gracefulShutdown('SIGTERM'));
process.on('SIGINT', () => gracefulShutdown('SIGINT'));

// Unhandled rejection 처리
process.on('unhandledRejection', (reason: Error) => {
  logger.error('❌ Unhandled Rejection:', reason);
  throw reason;
});

process.on('uncaughtException', (error: Error) => {
  logger.error('❌ Uncaught Exception:', error);
  process.exit(1);
});

export default server;