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
      expiresAt: new Date(Date.now() + 2 * 60 * 60 * 1000), // 2시간 후 만료
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
  // Haversine formula를 사용한 거리 계산
  // 정확한 SPATIAL 쿼리는 Raw SQL 사용
  const requests = await prisma.$queryRaw<any[]>`
    SELECT 
      cr.*,
      (6371 * acos(
        cos(radians(${latitude})) * 
        cos(radians(cr.latitude)) * 
        cos(radians(cr.longitude) - radians(${longitude})) + 
        sin(radians(${latitude})) * 
        sin(radians(cr.latitude))
      )) AS distance
    FROM companion_requests cr
    WHERE cr.status = 'pending'
      AND cr.expires_at > NOW()
    HAVING distance <= ${radiusKm}
    ORDER BY distance ASC
    LIMIT ${limit}
  `;

  // 각 요청의 requester 정보 가져오기
  const requestsWithRequester = await Promise.all(
    requests.map(async (req) => {
      const requester = await prisma.user.findUnique({
        where: { id: req.requester_id },
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
          companionScore: true,
        },
      });

      return {
        ...req,
        requester,
        distance: parseFloat(req.distance),
      };
    })
  );

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
