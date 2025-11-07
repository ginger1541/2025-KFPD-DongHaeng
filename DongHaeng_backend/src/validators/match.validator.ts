// src/validators/match.validator.ts
import Joi from 'joi';

// QR 스캔 검증
export const scanQRSchema = Joi.object({
  qrData: Joi.string().required().messages({
    'any.required': 'QR 데이터는 필수입니다',
    'string.base': 'QR 데이터는 문자열이어야 합니다',
  }),
});

// 동행 종료 검증
export const endCompanionSchema = Joi.object({
  qrData: Joi.string().required().messages({
    'any.required': 'QR 데이터는 필수입니다',
  }),
  rating: Joi.number().integer().min(1).max(5).optional().messages({
    'number.min': '평점은 1-5 사이여야 합니다',
    'number.max': '평점은 1-5 사이여야 합니다',
  }),
  review: Joi.string().max(500).optional().messages({
    'string.max': '리뷰는 최대 500자까지 가능합니다',
  }),
});