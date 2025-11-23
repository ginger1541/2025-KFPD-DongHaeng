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
  // PDF 요구사항 1-1: 동행 예약 일시
  scheduledAt: Joi.date().iso().greater('now').required().messages({
    'date.base': '예약 일시는 올바른 날짜 형식이어야 합니다',
    'date.format': '예약 일시는 ISO 8601 형식이어야 합니다 (예: 2025-11-21T15:30:00+09:00)',
    'date.greater': '예약 일시는 현재 시간 이후여야 합니다',
    'any.required': '예약 일시는 필수입니다',
  }),
  // PDF 요구사항 1-2: 경로 정보
  route: Joi.object({
    coord_type: Joi.string().valid('WGS84').required().messages({
      'any.only': '좌표 타입은 WGS84만 허용됩니다',
      'any.required': '좌표 타입은 필수입니다',
    }),
    total_distance_meters: Joi.number().positive().required().messages({
      'number.positive': '총 거리는 0보다 커야 합니다',
      'any.required': '총 거리는 필수입니다',
    }),
    total_duration_seconds: Joi.number().positive().required().messages({
      'number.positive': '총 소요 시간은 0보다 커야 합니다',
      'any.required': '총 소요 시간은 필수입니다',
    }),
    estimated_price: Joi.number().min(0).required().messages({
      'number.min': '예상 금액은 0 이상이어야 합니다',
      'any.required': '예상 금액은 필수입니다',
    }),
    points: Joi.array()
      .items(
        Joi.object({
          lat: Joi.number().min(-90).max(90).required().messages({
            'number.min': '위도는 -90 ~ 90 사이여야 합니다',
            'number.max': '위도는 -90 ~ 90 사이여야 합니다',
            'any.required': '위도는 필수입니다',
          }),
          lng: Joi.number().min(-180).max(180).required().messages({
            'number.min': '경도는 -180 ~ 180 사이여야 합니다',
            'number.max': '경도는 -180 ~ 180 사이여야 합니다',
            'any.required': '경도는 필수입니다',
          }),
        })
      )
      .min(2)
      .required()
      .messages({
        'array.min': '경로는 최소 2개 이상의 좌표가 필요합니다',
        'any.required': '경로 좌표는 필수입니다',
      }),
  })
    .optional()
    .messages({
      'object.base': '경로 정보는 올바른 객체 형식이어야 합니다',
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
