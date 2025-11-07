import Redis from 'ioredis';
import { logger } from './logger';

const REDIS_ENABLED = process.env.REDIS_ENABLED === 'true';

let redis: Redis | null = null;

if (REDIS_ENABLED) {
  const redisConfig = {
    host: process.env.REDIS_HOST || 'localhost',
    port: parseInt(process.env.REDIS_PORT || '6379', 10),
    password: process.env.REDIS_PASSWORD || undefined,
    retryStrategy: (times: number) => {
      const delay = Math.min(times * 50, 2000);
      return delay;
    },
    maxRetriesPerRequest: 3,
    lazyConnect: true, // 초기 연결 실패 시 앱 시작 차단 방지
  };

  redis = new Redis(redisConfig);

  // 연결 이벤트
  redis.on('connect', () => {
    logger.info('✅ Redis connected');
  });

  redis.on('ready', () => {
    logger.info('✅ Redis ready');
  });

  redis.on('error', (error) => {
    logger.error('❌ Redis error:', error);
  });

  redis.on('close', () => {
    logger.warn('⚠️  Redis connection closed');
  });

  redis.on('reconnecting', () => {
    logger.info('🔄 Redis reconnecting...');
  });

  // Graceful shutdown
  process.on('beforeExit', async () => {
    if (redis) {
      await redis.quit();
      logger.info('✅ Redis disconnected');
    }
  });

  // 비동기 연결 시작
  redis.connect().catch((error) => {
    logger.error('❌ Failed to connect to Redis:', error);
    logger.warn('⚠️  Running without Redis - some features may be limited');
  });
} else {
  logger.info('ℹ️  Redis is disabled - running in local development mode');
}

export default redis;

// Redis 유틸리티 함수들 (Redis가 비활성화되면 no-op)
export const redisUtils = {
  // JWT Refresh Token 저장
  async setRefreshToken(userId: string, token: string, expiresIn: number) {
    if (!redis) {
      logger.debug('Redis disabled - skipping setRefreshToken');
      return;
    }
    const key = `refresh_token:${userId}`;
    await redis.setex(key, expiresIn, token);
  },

  // JWT Refresh Token 조회
  async getRefreshToken(userId: string): Promise<string | null> {
    if (!redis) {
      logger.debug('Redis disabled - skipping getRefreshToken');
      return null;
    }
    const key = `refresh_token:${userId}`;
    return redis.get(key);
  },

  // JWT Refresh Token 삭제
  async deleteRefreshToken(userId: string) {
    if (!redis) {
      logger.debug('Redis disabled - skipping deleteRefreshToken');
      return;
    }
    const key = `refresh_token:${userId}`;
    await redis.del(key);
  },

  // 실시간 위치 저장 (Geo)
  async setLocation(userId: string, longitude: number, latitude: number) {
    if (!redis) {
      logger.debug('Redis disabled - skipping setLocation');
      return;
    }
    await redis.geoadd('user_locations', longitude, latitude, userId);
  },

  // 주변 사용자 검색 (반경 5km)
  async getNearbyUsers(longitude: number, latitude: number, radiusKm: number = 5) {
    if (!redis) {
      logger.debug('Redis disabled - skipping getNearbyUsers');
      return [];
    }
    const result = await redis.georadius(
      'user_locations',
      longitude,
      latitude,
      radiusKm,
      'km',
      'WITHDIST',
      'ASC'
    );
    return result;
  },

  // 위치 삭제
  async deleteLocation(userId: string) {
    if (!redis) {
      logger.debug('Redis disabled - skipping deleteLocation');
      return;
    }
    await redis.zrem('user_locations', userId);
  },

  // 캐시 저장
  async setCache(key: string, value: any, expiresIn: number = 3600) {
    if (!redis) {
      logger.debug('Redis disabled - skipping setCache');
      return;
    }
    await redis.setex(key, expiresIn, JSON.stringify(value));
  },

  // 캐시 조회
  async getCache<T>(key: string): Promise<T | null> {
    if (!redis) {
      logger.debug('Redis disabled - skipping getCache');
      return null;
    }
    const data = await redis.get(key);
    return data ? JSON.parse(data) : null;
  },

  // 캐시 삭제
  async deleteCache(key: string) {
    if (!redis) {
      logger.debug('Redis disabled - skipping deleteCache');
      return;
    }
    await redis.del(key);
  },
};
