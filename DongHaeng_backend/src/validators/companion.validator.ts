import Joi from 'joi';

// 동행 요청 생성 검증
export const createRequestSchema = Joi.object({
  title: Joi.string().min(5).max(100).required().messages({
    'string.min': '제목은 최소 5자 이상이어야 합니다',
    'string.max': '제목은 최대 100자까지 가능합니다',
    'any.required': '제목은 필수입니다',
  }),
  description: Joi.string().max(500).optional().allow('').messages({
    'string.max': '설명은 최대 500자까지 가능합니다',
  }),
  startLatitude: Joi.number().min(-90).max(90).required().messages({
    'number.min': '위도는 -90 ~ 90 사이여야 합니다',
    'number.max': '위도는 -90 ~ 90 사이여야 합니다',
    'any.required': '출발지 위도는 필수입니다',
  }),
  startLongitude: Joi.number().min(-180).max(180).required().messages({
    'number.min': '경도는 -180 ~ 180 사이여야 합니다',
    'number.max': '경도는 -180 ~ 180 사이여야 합니다',
    'any.required': '출발지 경도는 필수입니다',
  }),
  destinationLatitude: Joi.number().min(-90).max(90).required().messages({
    'number.min': '위도는 -90 ~ 90 사이여야 합니다',
    'number.max': '위도는 -90 ~ 90 사이여야 합니다',
    'any.required': '목적지 위도는 필수입니다',
  }),
  destinationLongitude: Joi.number().min(-180).max(180).required().messages({
    'number.min': '경도는 -180 ~ 180 사이여야 합니다',
    'number.max': '경도는 -180 ~ 180 사이여야 합니다',
    'any.required': '목적지 경도는 필수입니다',
  }),
  startAddress: Joi.string().max(200).required().messages({
    'string.max': '출발지 주소는 최대 200자까지 가능합니다',
    'any.required': '출발지 주소는 필수입니다',
  }),
  destinationAddress: Joi.string().max(200).required().messages({
    'string.max': '목적지 주소는 최대 200자까지 가능합니다',
    'any.required': '목적지 주소는 필수입니다',
  }),
  estimatedMinutes: Joi.number().integer().min(1).max(300).required().messages({
    'number.min': '예상 소요 시간은 최소 1분 이상이어야 합니다',
    'number.max': '예상 소요 시간은 최대 300분(5시간)까지 가능합니다',
    'any.required': '예상 소요 시간은 필수입니다',
  }),
});

// 동행 요청 수정 검증
export const updateRequestSchema = Joi.object({
  title: Joi.string().min(5).max(100).optional().messages({
    'string.min': '제목은 최소 5자 이상이어야 합니다',
    'string.max': '제목은 최대 100자까지 가능합니다',
  }),
  description: Joi.string().max(500).optional().allow('').messages({
    'string.max': '설명은 최대 500자까지 가능합니다',
  }),
  estimatedMinutes: Joi.number().integer().min(1).max(300).optional().messages({
    'number.min': '예상 소요 시간은 최소 1분 이상이어야 합니다',
    'number.max': '예상 소요 시간은 최대 300분(5시간)까지 가능합니다',
  }),
});

// 주변 요청 조회 검증 (쿼리 파라미터)
export const nearbyRequestsSchema = Joi.object({
  latitude: Joi.number().min(-90).max(90).required().messages({
    'number.min': '위도는 -90 ~ 90 사이여야 합니다',
    'number.max': '위도는 -90 ~ 90 사이여야 합니다',
    'any.required': '현재 위치의 위도는 필수입니다',
  }),
  longitude: Joi.number().min(-180).max(180).required().messages({
    'number.min': '경도는 -180 ~ 180 사이여야 합니다',
    'number.max': '경도는 -180 ~ 180 사이여야 합니다',
    'any.required': '현재 위치의 경도는 필수입니다',
  }),
  radiusKm: Joi.number().min(1).max(50).optional().default(5).messages({
    'number.min': '검색 반경은 최소 1km 이상이어야 합니다',
    'number.max': '검색 반경은 최대 50km까지 가능합니다',
  }),
  limit: Joi.number().integer().min(1).max(50).optional().default(20).messages({
    'number.min': '최소 1개 이상 조회해야 합니다',
    'number.max': '최대 50개까지 조회 가능합니다',
  }),
});
