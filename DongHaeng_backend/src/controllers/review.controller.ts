// src/controllers/review.controller.ts
import { Request, Response } from 'express';
import { asyncHandler } from '../middlewares/error.middleware';
import * as reviewService from '../services/review.service';

/**
 * 후기 작성
 * POST /api/reviews
 */
export const createReview = asyncHandler(async (req: Request, res: Response) => {
  const { match_id, reviewee_id, rating, comment, selected_badges } = req.body;
  const reviewerId = req.user!.userId;

  const review = await reviewService.createReview({
    matchId: BigInt(match_id),
    reviewerId,
    revieweeId: BigInt(reviewee_id),
    rating,
    comment,
    selectedBadges: selected_badges,
  });

  res.status(201).json({
    success: true,
    data: {
      review_id: review.id.toString(),
      match_id: review.matchId.toString(),
      reviewer_id: review.reviewerId.toString(),
      reviewee_id: review.revieweeId.toString(),
      rating: review.rating,
      comment: review.comment,
      selected_badges: review.selectedBadges,
      created_at: review.createdAt,
    },
    message: '평가가 완료되었습니다',
  });
});

/**
 * 받은 후기 조회
 * GET /api/users/:userId/reviews
 */
export const getUserReviews = asyncHandler(async (req: Request, res: Response) => {
  const userId = BigInt(req.params.userId);
  const page = parseInt(req.query.page as string) || 1;
  const perPage = parseInt(req.query.per_page as string) || 20;

  const result = await reviewService.getUserReviews(userId, page, perPage);

  res.status(200).json({
    success: true,
    data: {
      average_rating: result.averageRating,
      total_reviews: result.totalReviews,
      reviews: result.reviews.map((review) => ({
        review_id: review.id.toString(),
        reviewer: {
          user_id: review.reviewer.id.toString(),
          name: review.reviewer.name,
          profile_image_url: review.reviewer.profileImageUrl,
        },
        rating: review.rating,
        comment: review.comment,
        selected_badges: review.selectedBadges,
        created_at: review.createdAt,
      })),
    },
    pagination: {
      page,
      per_page: perPage,
      total: result.totalReviews,
      total_pages: Math.ceil(result.totalReviews / perPage),
    },
  });
});
