// src/services/review.service.ts
import { PrismaClient } from '@prisma/client';
import { AppError } from '../middlewares/error.middleware';

const prisma = new PrismaClient();

interface CreateReviewData {
  matchId: bigint;
  reviewerId: bigint;
  revieweeId: bigint;
  rating: number;
  comment?: string;
  selectedBadges?: string[];
}

/**
 * 후기 작성
 */
export const createReview = async (data: CreateReviewData) => {
  const { matchId, reviewerId, revieweeId, rating, comment, selectedBadges } = data;

  // 1. 매칭 존재 확인
  const match = await prisma.match.findUnique({
    where: { matchId },
  });

  if (!match) {
    throw new AppError('MATCH_NOT_FOUND', '매칭 정보를 찾을 수 없습니다', 404);
  }

  // 2. 매칭 완료 확인
  if (match.status !== 'completed') {
    throw new AppError(
      'MATCH_NOT_COMPLETED',
      '완료된 동행만 평가할 수 있습니다',
      400
    );
  }

  // 3. 평가 권한 확인 (매칭의 참여자인지)
  if (match.requesterId !== reviewerId && match.helperId !== reviewerId) {
    throw new AppError(
      'REVIEW_FORBIDDEN',
      '해당 매칭의 참여자가 아닙니다',
      403
    );
  }

  // 4. 피평가자 확인 (상대방이 맞는지)
  const isRevieweeValid =
    (match.requesterId === reviewerId && match.helperId === revieweeId) ||
    (match.helperId === reviewerId && match.requesterId === revieweeId);

  if (!isRevieweeValid) {
    throw new AppError(
      'REVIEWEE_INVALID',
      '평가 대상이 올바르지 않습니다',
      400
    );
  }

  // 5. 중복 평가 확인
  const existingReview = await prisma.review.findFirst({
    where: {
      matchId,
      reviewerId,
    },
  });

  if (existingReview) {
    throw new AppError(
      'REVIEW_ALREADY_EXISTS',
      '이미 평가를 완료했습니다',
      400
    );
  }

  // 6. 평점 유효성 검사
  if (rating < 1 || rating > 5) {
    throw new AppError(
      'INVALID_RATING',
      '평점은 1~5 사이의 값이어야 합니다',
      400
    );
  }

  // 7. 후기 작성
  const review = await prisma.review.create({
    data: {
      matchId,
      reviewerId,
      revieweeId,
      rating,
      comment: comment || null,
      selectedBadges: selectedBadges ? JSON.parse(JSON.stringify(selectedBadges)) : null,
    },
  });

  // 8. 피평가자의 동행지수 업데이트
  await updateCompanionScore(revieweeId);

  return review;
};

/**
 * 사용자가 받은 후기 조회
 */
export const getUserReviews = async (
  userId: bigint,
  page: number,
  perPage: number
) => {
  // 1. 사용자 존재 확인
  const user = await prisma.user.findUnique({
    where: { id: userId },
  });

  if (!user) {
    throw new AppError('USER_NOT_FOUND', '사용자를 찾을 수 없습니다', 404);
  }

  // 2. 받은 후기 조회
  const [reviews, totalReviews, ratingStats] = await Promise.all([
    prisma.review.findMany({
      where: { revieweeId: userId },
      include: {
        reviewer: {
          select: {
            id: true,
            name: true,
            profileImageUrl: true,
          },
        },
      },
      orderBy: { createdAt: 'desc' },
      skip: (page - 1) * perPage,
      take: perPage,
    }),
    prisma.review.count({
      where: { revieweeId: userId },
    }),
    prisma.review.aggregate({
      where: { revieweeId: userId },
      _avg: {
        rating: true,
      },
    }),
  ]);

  // 3. 평균 평점 계산
  const averageRating = ratingStats._avg.rating || 0;

  return {
    reviews,
    totalReviews,
    averageRating: Number(averageRating.toFixed(1)),
  };
};

/**
 * 동행지수 업데이트
 * 평균 평점을 기반으로 동행지수 계산 (0~100 스케일)
 */
const updateCompanionScore = async (userId: bigint) => {
  const ratingStats = await prisma.review.aggregate({
    where: { revieweeId: userId },
    _avg: {
      rating: true,
    },
    _count: {
      rating: true,
    },
  });

  if (!ratingStats._count.rating || !ratingStats._avg.rating) {
    return;
  }

  // 평균 평점 (1~5)를 0~100 스케일로 변환
  // 예: 평점 4.5 -> 동행지수 90
  const avgRating = ratingStats._avg.rating;
  const companionScore = (avgRating / 5) * 100;

  await prisma.user.update({
    where: { id: userId },
    data: {
      companionScore: Number(companionScore.toFixed(2)),
    },
  });
};
