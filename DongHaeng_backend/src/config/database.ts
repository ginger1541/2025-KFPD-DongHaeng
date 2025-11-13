import { PrismaClient } from '@prisma/client';
import { logger } from './logger';

const prisma = new PrismaClient({
  log: [
    {
      emit: 'event',
      level: 'query',
    },
    {
      emit: 'event',
      level: 'error',
    },
    {
      emit: 'event',
      level: 'warn',
    },
  ],
});

// 쿼리 로깅 (개발 환경에서만)
if (process.env.NODE_ENV === 'development') {
  prisma.$on('query', (e) => {
    logger.debug('Query: ' + e.query);
    logger.debug('Duration: ' + e.duration + 'ms');
  });
}

// 에러 로깅
prisma.$on('error', (e) => {
  logger.error('Prisma Error:', e);
});

// 경고 로깅
prisma.$on('warn', (e) => {
  logger.warn('Prisma Warning:', e);
});

// Graceful shutdown
process.on('beforeExit', async () => {
  await prisma.$disconnect();
  logger.info('✅ Prisma disconnected');
});

export default prisma;
