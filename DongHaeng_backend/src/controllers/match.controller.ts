// src/controllers/match.controller.ts
import { Request, Response } from 'express';
import * as matchService from '../services/match.service';
import { asyncHandler } from '../middlewares/error.middleware';

// 내 매칭 목록 조회
export const getMyMatches = asyncHandler(async (req: Request, res: Response) => {
  const role = req.query.role as 'helper' | 'requester' | undefined;

  let matches;
  
  if (role === 'helper') {
    matches = await matchService.getMyMatchesAsHelper(req.user!.userId);
  } else if (role === 'requester') {
    matches = await matchService.getMyMatchesAsRequester(req.user!.userId);
  } else {
    // 둘 다 조회
    const [asHelper, asRequester] = await Promise.all([
      matchService.getMyMatchesAsHelper(req.user!.userId),
      matchService.getMyMatchesAsRequester(req.user!.userId),
    ]);
    
    matches = {
      asHelper,
      asRequester,
    };
  }

  res.status(200).json({
    success: true,
    data: matches,
  });
});

// 매칭 상세 조회
export const getMatchById = asyncHandler(async (req: Request, res: Response) => {
  const matchId = BigInt(req.params.id);
  const match = await matchService.getMatchById(matchId, req.user!.userId);

  res.status(200).json({
    success: true,
    data: match,
  });
});

// 동행 시작 QR 코드 생성
export const generateStartQR = asyncHandler(async (req: Request, res: Response) => {
  const matchId = BigInt(req.params.id);
  const qrData = await matchService.generateStartQR(matchId, req.user!.userId);

  res.status(200).json({
    success: true,
    message: '동행 시작 QR 코드가 생성되었습니다',
    data: qrData,
  });
});

// 동행 시작 (QR 스캔)
export const startCompanion = asyncHandler(async (req: Request, res: Response) => {
  const matchId = BigInt(req.params.id);
  const { qrData } = req.body;

  const match = await matchService.startCompanion(matchId, req.user!.userId, qrData);

  res.status(200).json({
    success: true,
    message: '동행이 시작되었습니다',
    data: match,
  });
});

// 동행 종료 QR 코드 생성
export const generateEndQR = asyncHandler(async (req: Request, res: Response) => {
  const matchId = BigInt(req.params.id);
  const qrData = await matchService.generateEndQR(matchId, req.user!.userId);

  res.status(200).json({
    success: true,
    message: '동행 종료 QR 코드가 생성되었습니다',
    data: qrData,
  });
});

// 동행 종료 (QR 스캔)
export const endCompanion = asyncHandler(async (req: Request, res: Response) => {
  const matchId = BigInt(req.params.id);
  const { qrData, rating, review } = req.body;

  const result = await matchService.endCompanion(
    matchId,
    req.user!.userId,
    qrData,
    rating,
    review
  );

  res.status(200).json({
    success: true,
    message: '동행이 완료되었습니다',
    data: result,
  });
});