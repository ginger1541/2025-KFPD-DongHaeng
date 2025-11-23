import prisma from '../config/database';
import { AppError } from '../middlewares/error.middleware';

// 내 프로필 조회
export const getMyProfile = async (userId: bigint) => {
  const user = await prisma.user.findUnique({
    where: { id: userId },
    select: {
      id: true,
      email: true,
      name: true,
      phone: true,
      profileImageUrl: true,
      bio: true,
      userType: true,
      birthDate: true,
      gender: true,
      isVerified: true,
      verificationMethod: true,
      companionScore: true,
      totalCompanions: true,
      totalVolunteerMinutes: true,
      totalPoints: true,
      createdAt: true,
      lastLoginAt: true,
    },
  });

  if (!user) {
    throw new AppError('사용자를 찾을 수 없습니다', 404);
  }

  return user;
};

// 내 프로필 수정
export const updateMyProfile = async (
  userId: bigint,
  data: {
    name?: string;
    bio?: string;
    profileImageUrl?: string;
  }
) => {
  const user = await prisma.user.update({
    where: { id: userId },
    data: {
      ...(data.name && { name: data.name }),
      ...(data.bio !== undefined && { bio: data.bio }),
      ...(data.profileImageUrl && { profileImageUrl: data.profileImageUrl }),
    },
    select: {
      id: true,
      email: true,
      name: true,
      phone: true,
      profileImageUrl: true,
      bio: true,
      userType: true,
      companionScore: true,
      updatedAt: true,
    },
  });

  return user;
};

// 특정 사용자 프로필 조회
export const getUserProfile = async (userId: bigint) => {
  const user = await prisma.user.findUnique({
    where: { id: userId },
    select: {
      id: true,
      name: true,
      email: true,
      phone: true,
      profileImageUrl: true,
      bio: true,
      userType: true,
      birthDate: true,
      gender: true,
      companionScore: true,
      totalCompanions: true,
      totalVolunteerMinutes: true,
      totalPoints: true,
      createdAt: true,
      badges: {
        include: {
          badgeType: {
            select: {
              id: true,
              badgeName: true,
              badgeIconUrl: true,
            },
          },
        },
        orderBy: {
          earnedAt: 'desc',
        },
        take: 10, // 최근 10개만
      },
    },
  });

  if (!user) {
    throw new AppError('USER_NOT_FOUND', '사용자를 찾을 수 없습니다', 404);
  }

  // 배지 데이터 포맷 변환
  const formattedBadges = user.badges.map((badge) => ({
    badge_id: badge.id.toString(),
    badge_name: badge.badgeType.badgeName,
    badge_icon_url: badge.badgeType.badgeIconUrl,
    earned_at: badge.earnedAt,
  }));

  return {
    user_id: user.id.toString(),
    name: user.name,
    email: user.email,
    phone: user.phone,
    profile_image_url: user.profileImageUrl,
    bio: user.bio,
    user_type: user.userType,
    birth_date: user.birthDate,
    gender: user.gender,
    companion_score: Number(user.companionScore),
    total_companions: user.totalCompanions,
    total_volunteer_minutes: user.totalVolunteerMinutes,
    total_points: user.totalPoints,
    badges: formattedBadges,
    created_at: user.createdAt,
  };
};

// 프로필 이미지 업로드
export const uploadProfileImage = async (
  userId: bigint,
  file: Express.Multer.File
) => {
  // 파일 URL 생성 (실제로는 CDN URL 사용)
  const baseUrl = process.env.BASE_URL || 'http://localhost:3000';
  const profileImageUrl = `${baseUrl}/uploads/profiles/${file.filename}`;

  // 사용자 프로필 이미지 URL 업데이트
  await prisma.user.update({
    where: { id: userId },
    data: { profileImageUrl },
  });

  return {
    profile_image_url: profileImageUrl,
    uploaded_at: new Date(),
  };
};

// 사용자 유형 설정
export const updateUserType = async (
  userId: bigint,
  userType: 'requester' | 'helper' | 'both'
) => {
  // 유효성 검사
  if (!['requester', 'helper', 'both'].includes(userType)) {
    throw new AppError(
      'INVALID_USER_TYPE',
      '유효하지 않은 사용자 유형입니다',
      400
    );
  }

  const user = await prisma.user.update({
    where: { id: userId },
    data: { userType },
    select: {
      id: true,
      userType: true,
      updatedAt: true,
    },
  });

  return user;
};

// 약관 동의 (실제로는 별도 테이블에 저장하는 것이 권장됨)
// 현재는 간단히 처리만 하고, 필요시 USER_CONSENTS 테이블 추가
export const updateConsents = async (
  userId: bigint,
  consents: {
    serviceTerms: boolean;
    privacyPolicy: boolean;
    locationTerms: boolean;
    marketing: boolean;
  }
) => {
  // 필수 약관 확인
  if (!consents.serviceTerms || !consents.privacyPolicy || !consents.locationTerms) {
    throw new AppError(
      'REQUIRED_CONSENTS_MISSING',
      '필수 약관에 모두 동의해야 합니다',
      400
    );
  }

  // 사용자 존재 확인
  const user = await prisma.user.findUnique({
    where: { id: userId },
  });

  if (!user) {
    throw new AppError('USER_NOT_FOUND', '사용자를 찾을 수 없습니다', 404);
  }

  // TODO: 실제로는 USER_CONSENTS 테이블에 저장
  // 현재는 반환만 함

  return consents;
};
