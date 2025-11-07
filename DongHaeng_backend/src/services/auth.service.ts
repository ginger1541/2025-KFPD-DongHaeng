import prisma from '../config/database';
import { redisUtils } from '../config/redis';
import { hashPassword, comparePassword } from '../utils/bcrypt.util';
import {
  generateAccessToken,
  generateRefreshToken,
  verifyRefreshToken,
  getRefreshTokenExpiresIn,
} from '../utils/jwt.util';
import { AppError } from '../middlewares/error.middleware';

// 회원가입
export const signup = async (data: {
  email: string;
  password: string;
  name: string;
  phone: string;
  birthDate: Date;
  gender: 'male' | 'female' | 'prefer_not_to_say';
  userType: 'requester' | 'helper' | 'both';
  bio?: string;
}) => {
  // 이메일 중복 체크
  const existingUser = await prisma.user.findUnique({
    where: { email: data.email },
  });

  if (existingUser) {
    throw new AppError('이미 사용 중인 이메일입니다', 409);
  }

  // 휴대폰 번호 중복 체크
  const existingPhone = await prisma.user.findUnique({
    where: { phone: data.phone },
  });

  if (existingPhone) {
    throw new AppError('이미 사용 중인 휴대폰 번호입니다', 409);
  }

  // 비밀번호 해싱
  const passwordHash = await hashPassword(data.password);

  // 사용자 생성
  const user = await prisma.user.create({
    data: {
      email: data.email,
      passwordHash,
      name: data.name,
      phone: data.phone,
      birthDate: new Date(data.birthDate),
      gender: data.gender,
      userType: data.userType,
      bio: data.bio || null,
    },
    select: {
      id: true,
      email: true,
      name: true,
      phone: true,
      userType: true,
      createdAt: true,
    },
  });

  // JWT 토큰 생성
  const accessToken = generateAccessToken({
    userId: user.id,
    email: user.email,
    userType: user.userType,
  });

  const refreshToken = generateRefreshToken({
    userId: user.id,
    email: user.email,
    userType: user.userType,
  });

  // Refresh Token을 Redis에 저장 (Redis 비활성화 시 자동으로 스킵)
  await redisUtils.setRefreshToken(
    user.id.toString(),
    refreshToken,
    getRefreshTokenExpiresIn()
  );

  return {
    user,
    tokens: {
      accessToken,
      refreshToken,
    },
  };
};

// 로그인
export const login = async (email: string, password: string) => {
  // 사용자 조회
  const user = await prisma.user.findUnique({
    where: { email },
  });

  if (!user) {
    throw new AppError('이메일 또는 비밀번호가 올바르지 않습니다', 401);
  }

  // 비밀번호 검증
  const isPasswordValid = await comparePassword(password, user.passwordHash);

  if (!isPasswordValid) {
    throw new AppError('이메일 또는 비밀번호가 올바르지 않습니다', 401);
  }

  // 계정 활성화 여부 확인
  if (!user.isActive) {
    throw new AppError('비활성화된 계정입니다', 403);
  }

  // JWT 토큰 생성
  const accessToken = generateAccessToken({
    userId: user.id,
    email: user.email,
    userType: user.userType,
  });

  const refreshToken = generateRefreshToken({
    userId: user.id,
    email: user.email,
    userType: user.userType,
  });

  // Refresh Token을 Redis에 저장 (Redis 비활성화 시 자동으로 스킵)
  await redisUtils.setRefreshToken(
    user.id.toString(),
    refreshToken,
    getRefreshTokenExpiresIn()
  );

  // 마지막 로그인 시간 업데이트
  await prisma.user.update({
    where: { id: user.id },
    data: { lastLoginAt: new Date() },
  });

  return {
    user: {
      id: user.id,
      email: user.email,
      name: user.name,
      userType: user.userType,
      profileImageUrl: user.profileImageUrl,
      companionScore: user.companionScore,
    },
    tokens: {
      accessToken,
      refreshToken,
    },
  };
};

// Refresh Token으로 새 Access Token 발급
export const refresh = async (refreshToken: string) => {
  // Refresh Token 검증
  const decoded = verifyRefreshToken(refreshToken);

  // Redis에서 저장된 Refresh Token 조회 (Redis 활성화 시에만)
  const storedToken = await redisUtils.getRefreshToken(decoded.userId.toString());

  // Redis가 활성화되어 있을 때만 stored token 검증
  if (storedToken !== null && storedToken !== refreshToken) {
    throw new AppError('Invalid refresh token', 401);
  }

  // 사용자 조회
  const user = await prisma.user.findUnique({
    where: { id: decoded.userId },
  });

  if (!user || !user.isActive) {
    throw new AppError('User not found or inactive', 401);
  }

  // 새 Access Token 생성
  const newAccessToken = generateAccessToken({
    userId: user.id,
    email: user.email,
    userType: user.userType,
  });

  return {
    accessToken: newAccessToken,
  };
};

// 로그아웃
export const logout = async (userId: bigint) => {
  // Redis에서 Refresh Token 삭제
  await redisUtils.deleteRefreshToken(userId.toString());
};