// src/types/common.types.ts

// 인증된 사용자 정보를 담을 인터페이스
export interface AuthUser {
  userId: bigint;
  email: string;
  userType: 'requester' | 'helper' | 'both';
}

// Express Request 타입 확장
declare global {
  namespace Express {
    interface Request {
      user?: AuthUser;
    }
  }
}

// API 응답 타입
export interface ApiResponse<T = any> {
  success: boolean;
  message?: string;
  data?: T;
  error?: string;
}

// 페이지네이션 타입
export interface PaginationParams {
  page: number;
  limit: number;
}

export interface PaginatedResponse<T> {
  success: boolean;
  data: T[];
  pagination: {
    page: number;
    limit: number;
    total: number;
    totalPages: number;
  };
}

// 위치 정보 타입
export interface LocationData {
  latitude: number;
  longitude: number;
}

export interface GeoSearchParams extends LocationData {
  radiusKm?: number;
}

// JWT 페이로드 타입 (jwt.util.ts에서 사용)
export interface JwtPayload {
  userId: bigint;
  email: string;
  userType: 'requester' | 'helper' | 'both';
  iat?: number;
  exp?: number;
}

// Redis Geo 결과 타입
export interface RedisGeoResult {
  member: string;
  distance: number;
}

// TypeScript에게 이게 모듈임을 알림
export {};