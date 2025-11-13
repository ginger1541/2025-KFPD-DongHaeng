import { Request, Response } from 'express';
import * as companionService from '../services/companion.service';
import { asyncHandler } from '../middlewares/error.middleware';

// 동행 요청 생성
export const createRequest = asyncHandler(async (req: Request, res: Response) => {
  const request = await companionService.createRequest(req.user!.userId, req.body);

  res.status(201).json({
    success: true,
    message: '동행 요청이 생성되었습니다',
    data: request,
  });
});

// 주변 요청 조회
export const getNearbyRequests = asyncHandler(async (req: Request, res: Response) => {
  const { latitude, longitude, radiusKm, limit } = req.query as any;

  const requests = await companionService.getNearbyRequests(
    parseFloat(latitude),
    parseFloat(longitude),
    radiusKm ? parseFloat(radiusKm) : 5,
    limit ? parseInt(limit, 10) : 20
  );

  res.status(200).json({
    success: true,
    data: {
      requests,
      count: requests.length,
      searchArea: {
        latitude: parseFloat(latitude),
        longitude: parseFloat(longitude),
        radiusKm: radiusKm ? parseFloat(radiusKm) : 5,
      },
    },
  });
});

// 내 요청 목록 조회
export const getMyRequests = asyncHandler(async (req: Request, res: Response) => {
  const requests = await companionService.getMyRequests(req.user!.userId);

  res.status(200).json({
    success: true,
    data: {
      requests,
      count: requests.length,
    },
  });
});

// 요청 상세 조회
export const getRequestById = asyncHandler(async (req: Request, res: Response) => {
  const requestId = BigInt(req.params.id);
  const request = await companionService.getRequestById(requestId, req.user?.userId);

  res.status(200).json({
    success: true,
    data: request,
  });
});

// 요청 수정
export const updateRequest = asyncHandler(async (req: Request, res: Response) => {
  const requestId = BigInt(req.params.id);
  const request = await companionService.updateRequest(requestId, req.user!.userId, req.body);

  res.status(200).json({
    success: true,
    message: '요청이 수정되었습니다',
    data: request,
  });
});

// 요청 취소
export const cancelRequest = asyncHandler(async (req: Request, res: Response) => {
  const requestId = BigInt(req.params.id);
  await companionService.cancelRequest(requestId, req.user!.userId);

  res.status(200).json({
    success: true,
    message: '요청이 취소되었습니다',
  });
});

// 요청 수락 (매칭 생성)
export const acceptRequest = asyncHandler(async (req: Request, res: Response) => {
  const requestId = BigInt(req.params.id);
  const match = await companionService.acceptRequest(requestId, req.user!.userId);

  res.status(201).json({
    success: true,
    message: '요청을 수락했습니다',
    data: match,
  });
});
