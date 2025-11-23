import { Request, Response } from 'express';
import * as userService from '../services/user.service';
import { asyncHandler } from '../middlewares/error.middleware';

// 내 프로필 조회
export const getMyProfile = asyncHandler(async (req: Request, res: Response) => {
  const user = await userService.getMyProfile(req.user!.userId);

  res.status(200).json({
    success: true,
    data: user,
  });
});

// 내 프로필 수정
export const updateMyProfile = asyncHandler(async (req: Request, res: Response) => {
  const user = await userService.updateMyProfile(req.user!.userId, req.body);

  res.status(200).json({
    success: true,
    message: '프로필이 수정되었습니다',
    data: user,
  });
});

// 특정 사용자 프로필 조회
export const getUserProfile = asyncHandler(async (req: Request, res: Response) => {
  const userId = BigInt(req.params.userId);
  const user = await userService.getUserProfile(userId);

  res.status(200).json({
    success: true,
    data: user,
  });
});

// 프로필 이미지 업로드
export const uploadProfileImage = asyncHandler(async (req: Request, res: Response): Promise<any> => {
  const userId = BigInt(req.params.userId);

  // 권한 확인 (자신의 프로필만 수정 가능)
  if (userId !== req.user!.userId) {
    return res.status(403).json({
      success: false,
      error: {
        code: 'FORBIDDEN',
        message: '자신의 프로필 사진만 변경할 수 있습니다',
      },
    });
  }

  if (!req.file) {
    return res.status(400).json({
      success: false,
      error: {
        code: 'FILE_REQUIRED',
        message: '이미지 파일이 필요합니다',
      },
    });
  }

  const result = await userService.uploadProfileImage(userId, req.file);

  return res.status(200).json({
    success: true,
    data: result,
  });
});

// 사용자 유형 설정
export const updateUserType = asyncHandler(async (req: Request, res: Response): Promise<any> => {
  const userId = BigInt(req.params.userId);
  const { user_type } = req.body;

  // 권한 확인 (자신의 유형만 변경 가능)
  if (userId !== req.user!.userId) {
    return res.status(403).json({
      success: false,
      error: {
        code: 'FORBIDDEN',
        message: '자신의 사용자 유형만 변경할 수 있습니다',
      },
    });
  }

  const user = await userService.updateUserType(userId, user_type);

  return res.status(200).json({
    success: true,
    data: {
      user_id: user.id.toString(),
      user_type: user.userType,
      updated_at: user.updatedAt,
    },
  });
});

// 약관 동의
export const updateConsents = asyncHandler(async (req: Request, res: Response): Promise<any> => {
  const userId = BigInt(req.params.userId);
  const { service_terms, privacy_policy, location_terms, marketing } = req.body;

  // 권한 확인 (자신의 약관만 동의 가능)
  if (userId !== req.user!.userId) {
    return res.status(403).json({
      success: false,
      error: {
        code: 'FORBIDDEN',
        message: '자신의 약관만 동의할 수 있습니다',
      },
    });
  }

  const consents = await userService.updateConsents(userId, {
    serviceTerms: service_terms,
    privacyPolicy: privacy_policy,
    locationTerms: location_terms,
    marketing: marketing || false,
  });

  return res.status(200).json({
    success: true,
    data: {
      consents: [
        {
          consent_type: 'service_terms',
          agreed: consents.serviceTerms,
          agreed_at: new Date(),
        },
        {
          consent_type: 'privacy_policy',
          agreed: consents.privacyPolicy,
          agreed_at: new Date(),
        },
        {
          consent_type: 'location_terms',
          agreed: consents.locationTerms,
          agreed_at: new Date(),
        },
        {
          consent_type: 'marketing',
          agreed: consents.marketing,
          agreed_at: new Date(),
        },
      ],
    },
  });
});
