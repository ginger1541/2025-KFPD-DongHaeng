// src/services/match.service.ts
import prisma from '../config/database';
import * as matchRepo from '../repositories/match.repository';
import { generateQRData, generateQRCode, verifyQRData, parseQRData } from '../utils/qr.util';
import { AppError } from '../middlewares/error.middleware';
import { emitToMatch } from '../config/socket';

// 내 매칭 목록 조회 (도우미로서)
export const getMyMatchesAsHelper = async (userId: bigint) => {
  return matchRepo.findMyMatchesAsHelper(userId);
};

// 내 매칭 목록 조회 (요청자로서)
export const getMyMatchesAsRequester = async (userId: bigint) => {
  return matchRepo.findMyMatchesAsRequester(userId);
};

// 매칭 상세 조회
export const getMatchById = async (matchId: bigint, userId: bigint) => {
  const match = await matchRepo.findMatchById(matchId);

  if (!match) {
    throw new AppError('매칭을 찾을 수 없습니다', 404);
  }

  // 본인 확인 (요청자 또는 도우미만 조회 가능)
  if (match.requesterId !== userId && match.helperId !== userId) {
    throw new AppError('접근 권한이 없습니다', 403);
  }

  return match;
};

// 동행 시작 QR 코드 생성
export const generateStartQR = async (matchId: bigint, userId: bigint) => {
  const match = await matchRepo.findMatchById(matchId);

  if (!match) {
    throw new AppError('매칭을 찾을 수 없습니다', 404);
  }

  // 본인 확인
  if (match.requesterId !== userId && match.helperId !== userId) {
    throw new AppError('접근 권한이 없습니다', 403);
  }

  // 상태 확인
  if (match.status !== 'pending') {
    throw new AppError('이미 시작된 동행입니다', 400);
  }

  // QR 데이터 생성
  const qrData = generateQRData(matchId, userId, 'start');
  const qrCodeImage = await generateQRCode(qrData);

  // QR 데이터에서 nonce 추출
  const parsed = parseQRData(qrData);
  if (!parsed) {
    throw new AppError('QR 데이터 생성 실패', 500);
  }

  // DB에 QR 인증 레코드 저장
  await prisma.qrAuthentication.create({
    data: {
      matchId: match.matchId,
      authType: 'start',
      qrCode: parsed.nonce,
    },
  });

  return {
    matchId: match.matchId,
    qrCode: qrCodeImage,
    qrData,
    authType: 'start',
    expiresIn: 300, // 5분
    scanned: false, // QR 생성 시점에는 아직 스캔 안 됨
  };
};

// 동행 시작 (QR 스캔)
export const startCompanion = async (
  matchId: bigint,
  scannerId: bigint,
  qrData: string
) => {
  const match = await matchRepo.findMatchById(matchId);

  if (!match) {
    throw new AppError('매칭을 찾을 수 없습니다', 404);
  }

  // 스캐너가 매칭의 상대방인지 확인
  const otherUserId = match.requesterId === scannerId ? match.helperId : match.requesterId;
  
  if (match.requesterId !== scannerId && match.helperId !== scannerId) {
    throw new AppError('접근 권한이 없습니다', 403);
  }

  // QR 데이터 검증
  const parsed = parseQRData(qrData);
  
  if (!parsed) {
    throw new AppError('잘못된 QR 코드입니다', 400);
  }

  const isValid = verifyQRData(qrData, matchId, otherUserId, 'start');

  if (!isValid) {
    throw new AppError('유효하지 않거나 만료된 QR 코드입니다', 400);
  }

  // 상태 확인
  if (match.status !== 'pending') {
    throw new AppError('이미 시작된 동행입니다', 400);
  }

  // 트랜잭션: 매칭 시작 처리
  const updatedMatch = await prisma.$transaction(async (tx) => {
    // QR 인증 레코드 업데이트 (스캔 정보 기록)
    await tx.qrAuthentication.updateMany({
      where: {
        matchId,
        authType: 'start',
        qrCode: parsed.nonce,
        scannedAt: null, // 아직 스캔되지 않은 QR
      },
      data: {
        scannedByUserId: scannerId,
        scannedAt: new Date(),
      },
    });

    // 매칭 상태 변경
    const updated = await tx.match.update({
      where: { matchId },
      data: {
        status: 'in_progress',
        startedAt: new Date(),
      },
      include: {
        request: true,
        requester: {
          select: {
            id: true,
            name: true,
            profileImageUrl: true,
          },
        },
        helper: {
          select: {
            id: true,
            name: true,
            profileImageUrl: true,
          },
        },
      },
    });

    // 요청 상태 변경
    await tx.companionRequest.update({
      where: { id: match.requestId },
      data: { status: 'in_progress' },
    });

    return updated;
  });

  // 실시간 알림: 동행 시작 알림
  emitToMatch(matchId.toString(), 'companion:started', {
    matchId: matchId.toString(),
    startedAt: updatedMatch.startedAt,
    message: '동행이 시작되었습니다',
  });

  return updatedMatch;
};

// 동행 종료 QR 코드 생성
export const generateEndQR = async (matchId: bigint, userId: bigint) => {
  const match = await matchRepo.findMatchById(matchId);

  if (!match) {
    throw new AppError('매칭을 찾을 수 없습니다', 404);
  }

  // 본인 확인
  if (match.requesterId !== userId && match.helperId !== userId) {
    throw new AppError('접근 권한이 없습니다', 403);
  }

  // 상태 확인
  if (match.status !== 'in_progress') {
    throw new AppError('진행 중인 동행이 아닙니다', 400);
  }

  // QR 데이터 생성
  const qrData = generateQRData(matchId, userId, 'end');
  const qrCodeImage = await generateQRCode(qrData);

  // QR 데이터에서 nonce 추출
  const parsed = parseQRData(qrData);
  if (!parsed) {
    throw new AppError('QR 데이터 생성 실패', 500);
  }

  // DB에 QR 인증 레코드 저장
  await prisma.qrAuthentication.create({
    data: {
      matchId: match.matchId,
      authType: 'end',
      qrCode: parsed.nonce,
    },
  });

  return {
    matchId: match.matchId,
    qrCode: qrCodeImage,
    qrData,
    authType: 'end',
    expiresIn: 300, // 5분
    scanned: false, // QR 생성 시점에는 아직 스캔 안 됨
  };
};

// 동행 종료 (QR 스캔)
export const endCompanion = async (
  matchId: bigint,
  scannerId: bigint,
  qrData: string,
  _rating?: number,
  _review?: string
) => {
  const match = await matchRepo.findMatchById(matchId);

  if (!match) {
    throw new AppError('매칭을 찾을 수 없습니다', 404);
  }

  // 스캐너 확인
  const otherUserId = match.requesterId === scannerId ? match.helperId : match.requesterId;
  
  if (match.requesterId !== scannerId && match.helperId !== scannerId) {
    throw new AppError('접근 권한이 없습니다', 403);
  }

  // QR 데이터 검증
  const parsed = parseQRData(qrData);
  
  if (!parsed) {
    throw new AppError('잘못된 QR 코드입니다', 400);
  }

  const isValid = verifyQRData(qrData, matchId, otherUserId, 'end');

  if (!isValid) {
    throw new AppError('유효하지 않거나 만료된 QR 코드입니다', 400);
  }

  // 상태 확인
  if (match.status !== 'in_progress') {
    throw new AppError('진행 중인 동행이 아닙니다', 400);
  }

  // 소요 시간 계산 (분)
  const startTime = match.startedAt!.getTime();
  const endTime = Date.now();
  const durationMinutes = Math.round((endTime - startTime) / (1000 * 60));

  // 트랜잭션: 동행 종료 및 보상 지급
  const result = await prisma.$transaction(async (tx) => {
    // QR 인증 레코드 업데이트 (스캔 정보 기록)
    await tx.qrAuthentication.updateMany({
      where: {
        matchId,
        authType: 'end',
        qrCode: parsed.nonce,
        scannedAt: null, // 아직 스캔되지 않은 QR
      },
      data: {
        scannedByUserId: scannerId,
        scannedAt: new Date(),
      },
    });

    // 매칭 상태 변경
    await tx.match.update({
      where: { matchId },
      data: {
        status: 'completed',
        completedAt: new Date(),
        actualMinutes: durationMinutes,
      },
    });

    // 요청 상태 변경
    await tx.companionRequest.update({
      where: { id: match.requestId },
      data: { status: 'completed' },
    });

    // 도우미에게 포인트 및 봉사시간 지급
    const points = Math.max(10, durationMinutes * 2); // 분당 2포인트 (최소 10포인트)

    await tx.user.update({
      where: { id: match.helperId },
      data: {
        totalPoints: { increment: points },
        totalVolunteerMinutes: { increment: durationMinutes },
        totalCompanions: { increment: 1 },
      },
    });

    // 요청자 동행 횟수 증가
    await tx.user.update({
      where: { id: match.requesterId },
      data: {
        totalCompanions: { increment: 1 },
      },
    });

    // API 가이드라인에 맞는 응답 형식 반환
    return {
      match_id: Number(matchId),
      auth_type: 'end',
      scanned_at: new Date().toISOString(),
      status: 'completed',
      actual_duration_minutes: durationMinutes,
      earned_points: points,
      earned_volunteer_minutes: durationMinutes,
    };
  });

  // 실시간 알림: 동행 완료 알림
  emitToMatch(matchId.toString(), 'companion:completed', {
    match_id: result.match_id,
    completed_at: result.scanned_at,
    duration: result.actual_duration_minutes,
    earned_points: result.earned_points,
    earned_volunteer_minutes: result.earned_volunteer_minutes,
    message: '동행이 완료되었습니다',
  });

  return result;
};