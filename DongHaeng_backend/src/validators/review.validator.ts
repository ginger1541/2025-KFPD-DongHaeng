// src/validators/review.validator.ts
import Joi from 'joi';

export const createReviewSchema = Joi.object({
  match_id: Joi.number().integer().positive().required().messages({
    'any.required': '매칭 ID는 필수입니다',
    'number.base': '매칭 ID는 숫자여야 합니다',
    'number.positive': '매칭 ID는 양수여야 합니다',
  }),
  reviewee_id: Joi.number().integer().positive().required().messages({
    'any.required': '평가 대상 ID는 필수입니다',
    'number.base': '평가 대상 ID는 숫자여야 합니다',
    'number.positive': '평가 대상 ID는 양수여야 합니다',
  }),
  rating: Joi.number().integer().min(1).max(5).required().messages({
    'any.required': '평점은 필수입니다',
    'number.base': '평점은 숫자여야 합니다',
    'number.min': '평점은 1~5 사이의 값이어야 합니다',
    'number.max': '평점은 1~5 사이의 값이어야 합니다',
  }),
  comment: Joi.string().max(500).allow('', null).messages({
    'string.max': '후기는 500자 이내로 작성해주세요',
  }),
  selected_badges: Joi.array()
    .items(Joi.string())
    .max(5)
    .allow(null)
    .messages({
      'array.max': '배지는 최대 5개까지 선택할 수 있습니다',
    }),
});
