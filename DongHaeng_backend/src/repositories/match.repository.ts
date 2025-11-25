import prisma from '../config/database';

// 매칭 생성
export const createMatch = async (data: {
  requestId: bigint;
  requesterId: bigint;
  helperId: bigint;
}) => {
  return prisma.match.create({
    data: {
      requestId: data.requestId,
      requesterId: data.requesterId,
      helperId: data.helperId,
      status: 'pending',
    },
    include: {
      request: {
        include: {
          requester: {
            select: {
              id: true,
              name: true,
              phone: true,
              profileImageUrl: true,
            },
          },
        },
      },
      helper: {
        select: {
          id: true,
          name: true,
          phone: true,
          profileImageUrl: true,
          companionScore: true,
        },
      },
    },
  });
};

// 매칭 조회
export const findMatchById = async (matchId: bigint) => {
  return prisma.match.findUnique({
    where: { matchId },
    include: {
      request: {
        select: {
          id: true,
          title: true,
          description: true,
          latitude: true,
          longitude: true,
          startAddress: true,
          destinationAddress: true,
          estimatedMinutes: true,
          scheduledAt: true,
          route: true,
          status: true,
        },
      },
      requester: {
        select: {
          id: true,
          name: true,
          phone: true,
          profileImageUrl: true,
        },
      },
      helper: {
        select: {
          id: true,
          name: true,
          phone: true,
          profileImageUrl: true,
        },
      },
    },
  });
};

// 요청 ID로 매칭 조회
export const findMatchByRequestId = async (requestId: bigint) => {
  return prisma.match.findUnique({
    where: { requestId },
    include: {
      helper: {
        select: {
          id: true,
          name: true,
          profileImageUrl: true,
        },
      },
    },
  });
};

// 내 매칭 목록 (도우미로서)
export const findMyMatchesAsHelper = async (userId: bigint) => {
  return prisma.match.findMany({
    where: { helperId: userId },
    orderBy: { matchedAt: 'desc' },
    include: {
      request: {
        select: {
          id: true,
          title: true,
          description: true,
          latitude: true,
          longitude: true,
          startAddress: true,
          destinationAddress: true,
          estimatedMinutes: true,
          scheduledAt: true,
          route: true,
          status: true,
        },
      },
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

// 내 매칭 목록 (요청자로서)
export const findMyMatchesAsRequester = async (userId: bigint) => {
  return prisma.match.findMany({
    where: { requesterId: userId },
    orderBy: { matchedAt: 'desc' },
    include: {
      request: {
        select: {
          id: true,
          title: true,
          description: true,
          latitude: true,
          longitude: true,
          startAddress: true,
          destinationAddress: true,
          estimatedMinutes: true,
          scheduledAt: true,
          route: true,
          status: true,
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
};
