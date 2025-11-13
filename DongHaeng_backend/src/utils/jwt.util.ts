// src/utils/jwt.util.ts
import jwt from 'jsonwebtoken';

const JWT_SECRET = process.env.JWT_SECRET || 'your-secret-key';
const JWT_EXPIRES_IN = process.env.JWT_EXPIRES_IN || '1h';
const JWT_REFRESH_SECRET = process.env.JWT_REFRESH_SECRET || 'your-refresh-secret-key';
const JWT_REFRESH_EXPIRES_IN = process.env.JWT_REFRESH_EXPIRES_IN || '7d';

// Payload 타입 정의
interface JwtPayload {
  userId: bigint;
  email: string;
  userType: 'requester' | 'helper' | 'both';
}

// Access Token 생성
export const generateAccessToken = (payload: JwtPayload): string => {
  // BigInt를 문자열로 변환
  const stringPayload = {
    userId: payload.userId.toString(),
    email: payload.email,
    userType: payload.userType,
  };

  return jwt.sign(stringPayload, JWT_SECRET, {
    expiresIn: JWT_EXPIRES_IN,
  } as jwt.SignOptions);  // ← as jwt.SignOptions 추가!
};

// Refresh Token 생성
export const generateRefreshToken = (payload: JwtPayload): string => {
  const stringPayload = {
    userId: payload.userId.toString(),
    email: payload.email,
    userType: payload.userType,
  };

  return jwt.sign(stringPayload, JWT_REFRESH_SECRET, {
    expiresIn: JWT_REFRESH_EXPIRES_IN,
  } as jwt.SignOptions);  // ← as jwt.SignOptions 추가!
};

// Access Token 검증
export const verifyAccessToken = (token: string): any => {
  try {
    const decoded = jwt.verify(token, JWT_SECRET) as any;
    // userId를 다시 BigInt로 변환
    return {
      ...decoded,
      userId: BigInt(decoded.userId),
    };
  } catch (error) {
    throw new Error('Invalid or expired access token');
  }
};

// Refresh Token 검증
export const verifyRefreshToken = (token: string): any => {
  try {
    const decoded = jwt.verify(token, JWT_REFRESH_SECRET) as any;
    return {
      ...decoded,
      userId: BigInt(decoded.userId),
    };
  } catch (error) {
    throw new Error('Invalid or expired refresh token');
  }
};

// Refresh Token 만료 시간 (초)
export const getRefreshTokenExpiresIn = (): number => {
  const match = JWT_REFRESH_EXPIRES_IN.match(/(\d+)([dhms])/);
  if (!match) return 7 * 24 * 60 * 60; // 기본 7일

  const value = parseInt(match[1], 10);
  const unit = match[2];

  switch (unit) {
    case 'd':
      return value * 24 * 60 * 60;
    case 'h':
      return value * 60 * 60;
    case 'm':
      return value * 60;
    case 's':
      return value;
    default:
      return 7 * 24 * 60 * 60;
  }
};