// src/utils/qr.util.ts
import QRCode from 'qrcode';
import { v4 as uuidv4 } from 'uuid';

// QR 코드 데이터 생성
export const generateQRData = (matchId: bigint, userId: bigint, type: 'start' | 'end'): string => {
  const data = {
    matchId: matchId.toString(),
    userId: userId.toString(),
    type,
    timestamp: Date.now(),
    nonce: uuidv4(), // 재사용 방지
  };
  
  return JSON.stringify(data);
};

// QR 코드 이미지 생성 (Base64)
export const generateQRCode = async (data: string): Promise<string> => {
  try {
    const qrCodeDataURL = await QRCode.toDataURL(data, {
      errorCorrectionLevel: 'H',
      type: 'image/png',
      margin: 1,
      width: 300,
    });
    
    return qrCodeDataURL; // data:image/png;base64,iVBORw0KG...
  } catch (error) {
    throw new Error('Failed to generate QR code');
  }
};

// QR 코드 데이터 검증
export const verifyQRData = (
  data: string,
  expectedMatchId: bigint,
  expectedUserId: bigint,
  expectedType: 'start' | 'end'
): boolean => {
  try {
    const parsed = JSON.parse(data);
    
    // 매칭 ID 확인
    if (parsed.matchId !== expectedMatchId.toString()) {
      return false;
    }
    
    // 사용자 ID 확인
    if (parsed.userId !== expectedUserId.toString()) {
      return false;
    }
    
    // 타입 확인
    if (parsed.type !== expectedType) {
      return false;
    }
    
    // 타임스탬프 확인 (5분 이내)
    const now = Date.now();
    const timestamp = parsed.timestamp;
    const fiveMinutes = 5 * 60 * 1000;
    
    if (now - timestamp > fiveMinutes) {
      return false; // 만료됨
    }
    
    return true;
  } catch (error) {
    return false;
  }
};

// QR 데이터 파싱
export const parseQRData = (data: string): {
  matchId: string;
  userId: string;
  type: 'start' | 'end';
  timestamp: number;
  nonce: string;
} | null => {
  try {
    return JSON.parse(data);
  } catch (error) {
    return null;
  }
};
