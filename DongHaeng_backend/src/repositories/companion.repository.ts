import prisma from '../config/database';
import { Prisma } from '@prisma/client';

// 동행 요청 생성
export const createRequest = async (data: {
  requesterId: bigint;
  title: string;
  description: string | null;
  startLatitude: number;
  startLongitude: number;
  destinationLatitude: number;
  destinationLongitude: number;
  startAddress: string;
  destinationAddress: string;
  estimatedMinutes: number;
  scheduledAt: Date; // PDF 요구사항 1-1
  route: any | null; // PDF 요구사항 1-2
}) => {
  return prisma.companionRequest.create({
    data: {
      requesterId: data.requesterId,
      title: data.title,
      description: data.description,
      latitude: new Prisma.Decimal(data.startLatitude),
      longitude: new Prisma.Decimal(data.startLongitude),
      startAddress: data.startAddress,
      destinationAddress: data.destinationAddress,
      estimatedMinutes: data.estimatedMinutes,
      scheduledAt: data.scheduledAt,
      route: data.route,
      status: 'pending',
      expiresAt: new Date(Date.now() + 1024 * 60 * 60 * 1000), // 1024시간 후 만료 (약 43일, 개발용)
    },
    include: {
      requester: {
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
          companionScore: true,
        },
      },
    },
  });
};

// 요청 ID로 조회
export const findRequestById = async (requestId: bigint) => {
  return prisma.companionRequest.findUnique({
    where: { id: requestId },
    include: {
      requester: {
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
          companionScore: true,
          userType: true,
        },
      },
      match: {
        include: {
          helper: {
            select: {
              id: true,
              name: true,
              profileImageUrl: true,
              companionScore: true,
            },
          },
        },
      },
    },
  });
};

// 내 요청 목록 조회
export const findMyRequests = async (userId: bigint) => {
  return prisma.companionRequest.findMany({
    where: { requesterId: userId },
    orderBy: { requestedAt: 'desc' },
    include: {
      match: {
        include: {
          helper: {
            select: {
              id: true,
              name: true,
              profileImageUrl: true,
            },
          },
        },
      },
    },
  });
};

// 주변 요청 조회 (위치 기반 - latitude/longitude 사용)
export const findNearbyRequests = async (
  latitude: number,
  longitude: number,
  radiusKm: number,
  limit: number
) => {
  // 디버깅: 전체 pending 요청 수 확인
  const totalPending = await prisma.$queryRaw<any[]>`
    SELECT COUNT(*) as count
    FROM companion_requests cr
    WHERE cr.status = 'pending'
      AND (cr.expires_at IS NULL OR cr.expires_at > NOW())
  `;
  console.log('[DEBUG] Total pending requests:', totalPending);

  // 디버깅: 거리 계산 결과 확인 (HAVING 조건 없이)
  const allRequestsWithDistance = await prisma.$queryRaw<any[]>`
    SELECT
      cr.request_id,
      cr.title,
      cr.latitude,
      cr.longitude,
      cr.status,
      cr.expires_at,
      (6371 * acos(
        LEAST(1, GREATEST(-1,
          cos(radians(CAST(${latitude} AS DOUBLE))) *
          cos(radians(CAST(cr.latitude AS DOUBLE))) *
          cos(radians(CAST(cr.longitude AS DOUBLE)) - radians(CAST(${longitude} AS DOUBLE))) +
          sin(radians(CAST(${latitude} AS DOUBLE))) *
          sin(radians(CAST(cr.latitude AS DOUBLE)))
        ))
      )) AS distance
    FROM companion_requests cr
    WHERE cr.status = 'pending'
      AND (cr.expires_at IS NULL OR cr.expires_at > NOW())
    ORDER BY distance ASC
    LIMIT 10
  `;
  console.log('[DEBUG] All requests with distance (top 10):', JSON.stringify(allRequestsWithDistance, null, 2));
  console.log('[DEBUG] Search params:', { latitude, longitude, radiusKm, limit });

  // Haversine formula를 사용한 거리 계산
  // HAVING/ORDER BY 절 문제로 인해 모든 pending 요청을 가져온 후 JavaScript에서 처리
  // POINT 타입 컬럼 제외하고 명시적으로 선택
  const allRequests = await prisma.$queryRaw<any[]>`
    SELECT
      cr.request_id,
      cr.requester_id,
      cr.title,
      cr.description,
      cr.latitude,
      cr.longitude,
      cr.start_address,
      cr.destination_address,
      cr.estimated_minutes,
      cr.scheduled_at,
      cr.route,
      cr.status,
      cr.view_count,
      cr.requested_at,
      cr.expires_at,
      cr.created_at,
      cr.updated_at,
      (6371 * acos(
        LEAST(1, GREATEST(-1,
          cos(radians(CAST(${latitude} AS DOUBLE))) *
          cos(radians(CAST(cr.latitude AS DOUBLE))) *
          cos(radians(CAST(cr.longitude AS DOUBLE)) - radians(CAST(${longitude} AS DOUBLE))) +
          sin(radians(CAST(${latitude} AS DOUBLE))) *
          sin(radians(CAST(cr.latitude AS DOUBLE)))
        ))
      )) AS distance
    FROM companion_requests cr
    WHERE cr.status = 'pending'
      AND (cr.expires_at IS NULL OR cr.expires_at > NOW())
  `;

  console.log('[DEBUG] All requests fetched:', allRequests.length);

  // JavaScript에서 거리 필터링, 정렬 및 제한
  const requests = allRequests
    .filter(req => parseFloat(req.distance) <= radiusKm)
    .sort((a, b) => parseFloat(a.distance) - parseFloat(b.distance))
    .slice(0, limit);

  console.log('[DEBUG] Filtered requests count:', requests.length);

  // 각 요청의 requester 정보 가져오기
  const requestsWithRequester = await Promise.all(
    requests.map(async (req) => {
      // BigInt 변환 (MySQL에서 BIGINT가 number로 반환될 수 있음)
      const requesterId = typeof req.requester_id === 'bigint'
        ? req.requester_id
        : BigInt(req.requester_id);

      console.log('[DEBUG] Looking up requester:', requesterId, 'type:', typeof requesterId);

      const requester = await prisma.user.findUnique({
        where: { id: requesterId },
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
          companionScore: true,
        },
      });

      console.log('[DEBUG] Requester found:', !!requester);

      return {
        ...req,
        requester,
        distance: parseFloat(req.distance),
      };
    })
  );

  console.log('[DEBUG] Final requests with requester:', requestsWithRequester.length);

  return requestsWithRequester;
};

// 요청 수정
export const updateRequest = async (
  requestId: bigint,
  data: {
    title?: string;
    description?: string;
    estimatedMinutes?: number;
  }
) => {
  return prisma.companionRequest.update({
    where: { id: requestId },
    data,
    include: {
      requester: {
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
        },
      },
    },
  });
};

// 요청 상태 변경
export const updateRequestStatus = async (
  requestId: bigint,
  status: 'pending' | 'matching' | 'in_progress' | 'completed' | 'cancelled'
) => {
  return prisma.companionRequest.update({
    where: { id: requestId },
    data: { status },
  });
};

// 요청 삭제 (취소)
export const deleteRequest = async (requestId: bigint) => {
  return prisma.companionRequest.delete({
    where: { id: requestId },
  });
};

// 조회수 증가
export const incrementViewCount = async (requestId: bigint) => {
  return prisma.companionRequest.update({
    where: { id: requestId },
    data: {
      viewCount: {
        increment: 1,
      },
    },
  });
};
