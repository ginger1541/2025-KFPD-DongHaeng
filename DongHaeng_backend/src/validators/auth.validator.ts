import Joi from 'joi';

// 회원가입 검증
export const signupSchema = Joi.object({
  email: Joi.string().email().required().messages({
    'string.email': '유효한 이메일 주소를 입력해주세요',
    'any.required': '이메일은 필수입니다',
  }),
  password: Joi.string().min(8).required().messages({
    'string.min': '비밀번호는 최소 8자 이상이어야 합니다',
    'any.required': '비밀번호는 필수입니다',
  }),
  name: Joi.string().min(2).max(50).required().messages({
    'string.min': '이름은 최소 2자 이상이어야 합니다',
    'string.max': '이름은 최대 50자까지 가능합니다',
    'any.required': '이름은 필수입니다',
  }),
  phone: Joi.string()
    .pattern(/^010\d{8}$/)
    .required()
    .messages({
      'string.pattern.base': '올바른 휴대폰 번호를 입력해주세요 (예: 01012345678)',
      'any.required': '휴대폰 번호는 필수입니다',
    }),
  birthDate: Joi.date().max('now').required().messages({
    'date.max': '생년월일은 오늘 이전이어야 합니다',
    'any.required': '생년월일은 필수입니다',
  }),
  gender: Joi.string().valid('male', 'female', 'prefer_not_to_say').required().messages({
    'any.only': '성별은 male, female, prefer_not_to_say 중 하나여야 합니다',
    'any.required': '성별은 필수입니다',
  }),
  userType: Joi.string().valid('requester', 'helper', 'both').required().messages({
    'any.only': '사용자 유형은 requester, helper, both 중 하나여야 합니다',
    'any.required': '사용자 유형은 필수입니다',
  }),
  bio: Joi.string().max(200).optional().allow('').messages({
    'string.max': '자기소개는 최대 200자까지 가능합니다',
  }),
});

// 로그인 검증
export const loginSchema = Joi.object({
  email: Joi.string().email().required().messages({
    'string.email': '유효한 이메일 주소를 입력해주세요',
    'any.required': '이메일은 필수입니다',
  }),
  password: Joi.string().required().messages({
    'any.required': '비밀번호는 필수입니다',
  }),
});

// Refresh Token 검증
export const refreshTokenSchema = Joi.object({
  refreshToken: Joi.string().required().messages({
    'any.required': 'Refresh Token은 필수입니다',
  }),
});
