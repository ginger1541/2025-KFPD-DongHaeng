/**
 * BigInt Serializer
 *
 * BigInt 타입을 JSON으로 직렬화할 수 있도록
 * BigInt.prototype.toJSON 메서드를 확장합니다.
 *
 * 이 파일은 애플리케이션 시작 시점에 import되어야 합니다.
 */

declare global {
  interface BigInt {
    toJSON(): string;
  }
}

// BigInt.prototype.toJSON 메서드 정의
// JSON.stringify()가 BigInt를 만나면 자동으로 문자열로 변환합니다
BigInt.prototype.toJSON = function (): string {
  return this.toString();
};

export {};
