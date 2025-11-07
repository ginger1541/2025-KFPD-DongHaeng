import * as companionRepo from '../repositories/companion.repository';
import * as matchRepo from '../repositories/match.repository';
import { AppError } from '../middlewares/error.middleware';
import { emitToUser } from '../config/socket';

// 동행 요청 생성
export const createRequest = async (
  userId: bigint,
  data: {
    title: string;
    description?: string;
    startLatitude: number;
    startLongitude: number;
    destinationLatitude: number;
    destinationLongitude: number;
    startAddress: string;
    destinationAddress: string;
    estimatedMinutes: number;
  }
) => {
  const request = await companionRepo.createRequest({
    requesterId: userId,
    title: data.title,
    description: data.description || null,
    startLatitude: data.startLatitude,
    startLongitude: data.startLongitude,
    destinationLatitude: data.destinationLatitude,
    destinationLongitude: data.destinationLongitude,
    startAddress: data.startAddress,
    destinationAddress: data.destinationAddress,
    estimatedMinutes: data.estimatedMinutes,
  });

  return request;
};

// 주변 요청 조회
export const getNearbyRequests = async (
  latitude: number,
  longitude: number,
  radiusKm: number = 5,
  limit: number = 20
) => {
  const requests = await companionRepo.findNearbyRequests(latitude, longitude, radiusKm, limit);
  return requests;
};

// 내 요청 목록 조회
export const getMyRequests = async (userId: bigint) => {
  const requests = await companionRepo.findMyRequests(userId);
  return requests;
};

// 요청 상세 조회
export const getRequestById = async (requestId: bigint, viewerId?: bigint) => {
  const request = await companionRepo.findRequestById(requestId);

  if (!request) {
    throw new AppError('요청을 찾을 수 없습니다', 404);
  }

  // 조회수 증가 (본인이 아닐 때만)
  if (viewerId && viewerId !== request.requesterId) {
    await companionRepo.incrementViewCount(requestId);
  }

  return request;
};

// 요청 수정
export const updateRequest = async (
  requestId: bigint,
  userId: bigint,
  data: {
    title?: string;
    description?: string;
    estimatedMinutes?: number;
  }
) => {
  // 요청 조회
  const request = await companionRepo.findRequestById(requestId);

  if (!request) {
    throw new AppError('요청을 찾을 수 없습니다', 404);
  }

  // 본인 확인
  if (request.requesterId !== userId) {
    throw new AppError('본인의 요청만 수정할 수 있습니다', 403);
  }

  // 대기 중인 요청만 수정 가능
  if (request.status !== 'pending') {
    throw new AppError('대기 중인 요청만 수정할 수 있습니다', 400);
  }

  const updatedRequest = await companionRepo.updateRequest(requestId, data);
  return updatedRequest;
};

// 요청 취소
export const cancelRequest = async (requestId: bigint, userId: bigint) => {
  // 요청 조회
  const request = await companionRepo.findRequestById(requestId);

  if (!request) {
    throw new AppError('요청을 찾을 수 없습니다', 404);
  }

  // 본인 확인
  if (request.requesterId !== userId) {
    throw new AppError('본인의 요청만 취소할 수 있습니다', 403);
  }

  // 이미 매칭된 요청은 취소 불가
  if (request.match) {
    throw new AppError('이미 매칭된 요청은 취소할 수 없습니다', 400);
  }

  await companionRepo.deleteRequest(requestId);
};

// 요청 수락 (매칭 생성)
export const acceptRequest = async (requestId: bigint, helperId: bigint) => {
  // 요청 조회
  const request = await companionRepo.findRequestById(requestId);

  if (!request) {
    throw new AppError('요청을 찾을 수 없습니다', 404);
  }

  // 본인의 요청은 수락 불가
  if (request.requesterId === helperId) {
    throw new AppError('본인의 요청은 수락할 수 없습니다', 400);
  }

  // 대기 중인 요청만 수락 가능
  if (request.status !== 'pending') {
    throw new AppError('대기 중인 요청만 수락할 수 있습니다', 400);
  }

  // 이미 매칭된 요청인지 확인
  const existingMatch = await matchRepo.findMatchByRequestId(requestId);
  if (existingMatch) {
    throw new AppError('이미 매칭된 요청입니다', 400);
  }

  // 만료 시간 확인
  if (request.expiresAt && new Date() > request.expiresAt) {
    throw new AppError('만료된 요청입니다', 400);
  }

  // 트랜잭션으로 매칭 생성 및 요청 상태 변경
  const match = await matchRepo.createMatch({
    requestId,
    requesterId: request.requesterId,
    helperId,
  });

  // 요청 상태를 'matching'으로 변경
  await companionRepo.updateRequestStatus(requestId, 'matching');

  // 실시간 알림: 요청자에게 매칭 성공 알림
  emitToUser(request.requesterId.toString(), 'match:created', {
    matchId: match.matchId.toString(),
    helperId: helperId.toString(),
    helperName: match.helper.name,
    message: '도우미가 요청을 수락했습니다',
  });

  return match;
};