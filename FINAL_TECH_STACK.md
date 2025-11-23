# 동행 프로젝트 최종 기술 스택

## 📋 전체 스택 한눈에 보기

```
┌─────────────────────────────────────────┐
│         프론트엔드 (모바일 앱)           │
├─────────────────────────────────────────┤
│  Android/iOS 앱                         │
│  - 카카오맵 API (지도 표시)              │
│  - SK T map API (경로 탐색)             │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│         백엔드 서버 (Node.js)            │
├─────────────────────────────────────────┤
│  Node.js 20 LTS                         │
│  Express.js 4.18                        │
│  TypeScript 5.x                         │
│  Socket.io 4.x                          │
│  Prisma ORM 5.x                         │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│      데이터베이스 & 캐시                 │
├─────────────────────────────────────────┤
│  Cloud SQL (MySQL 8.0)                  │
│  Memorystore (Redis 7.0)                │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│      Google Cloud Platform              │
├─────────────────────────────────────────┤
│  Compute Engine (서버 호스팅)           │
│  Cloud SQL (데이터베이스)               │
│  Memorystore (Redis)                    │
│  Cloud Storage (이미지 저장)            │
│  Cloud Load Balancing                   │
└─────────────────────────────────────────┘
                  ↓
┌─────────────────────────────────────────┐
│      외부 서비스                         │
├─────────────────────────────────────────┤
│  Firebase FCM (푸시 알림)               │
│  PASS 본인인증                          │
│  카카오맵 API (프론트에서 사용)         │
│  SK T map API (프론트에서 사용)         │
└─────────────────────────────────────────┘
```

---

## 🔧 백엔드 기술 스택

### 1. 런타임 & 프레임워크

**Node.js 20 LTS**
```
- JavaScript 런타임
- 버전: 20.x (Long Term Support)
- 이유: 안정적, 비동기 I/O, 실시간 처리에 최적
```

**Express.js 4.18**
```
- 웹 프레임워크
- 버전: 4.18.x
- 이유: Node.js 표준 프레임워크, 가볍고 유연
```

**TypeScript 5.x**
```
- JavaScript의 타입 안전 버전
- 버전: 5.x
- 이유: 타입 체크로 버그 방지, 유지보수 용이
```

**Socket.io 4.x**
```
- 실시간 양방향 통신
- 버전: 4.x
- 이유: 실시간 위치 공유, 즉시 알림에 필수
```

---

### 2. 데이터베이스 & ORM

**Cloud SQL (MySQL 8.0)**
```
- 관계형 데이터베이스
- GCP의 관리형 MySQL
- 이유: 안정적, 트랜잭션 지원, 위치 데이터 처리 가능
```

**Prisma ORM 5.x**
```
- TypeScript 기반 ORM
- 버전: 5.x
- 이유: 타입 안전, 자동 마이그레이션, 직관적인 쿼리
```

**Memorystore (Redis 7.0)**
```
- 인메모리 캐시/데이터베이스
- GCP의 관리형 Redis
- 이유: 
  - JWT 토큰 저장
  - 실시간 위치 캐싱
  - Redis Geo로 주변 검색
```

---

## ☁️ 인프라 (Google Cloud Platform)

### 1. 컴퓨팅

**Compute Engine**
```
- 가상 머신 (VM) 인스턴스
- 용도: Node.js 서버 호스팅
- 인스턴스 타입: 
  - 개발: e2-micro (무료 티어)
  - 프로덕션: e2-medium
```

**Cloud Load Balancing**
```
- 로드 밸런서
- 용도: 트래픽 분산, SSL/TLS 종료
- 이유: 고가용성, 자동 확장
```

### 2. 데이터베이스

**Cloud SQL**
```
- 관리형 MySQL
- 인스턴스: db-f1-micro (개발), db-n1-standard-1 (프로덕션)
- 특징:
  - 자동 백업
  - 고가용성 (Multi-Zone)
  - 자동 패치
```

**Memorystore for Redis**
```
- 관리형 Redis
- 용도: 캐싱, 세션, 실시간 위치
- 특징:
  - 고가용성
  - 자동 페일오버
```

### 3. 스토리지

**Cloud Storage**
```
- 객체 스토리지 (S3와 유사)
- 용도: 
  - 프로필 이미지
  - 동행 인증 사진
  - 정적 파일
- 특징:
  - 높은 내구성
  - CDN 통합
```

### 4. 네트워크

**Nginx**
```
- 리버스 프록시 서버
- 용도:
  - HTTP → Node.js 서버 라우팅
  - SSL/TLS 처리
  - 정적 파일 제공
```

---

## 🔥 Firebase 서비스

**Firebase Cloud Messaging (FCM)**
```
- 푸시 알림 서비스
- 용도:
  - 새 동행 요청 알림
  - 매칭 완료 알림
  - 채팅 메시지 알림
- 이유: 
  - 무료
  - 안정적
  - Android/iOS 모두 지원
  - GCP와 완벽 통합
```

**Firebase Admin SDK**
```
- 백엔드에서 Firebase 제어
- 용도:
  - FCM 메시지 전송
  - 사용자 토큰 관리
```

---

## 🗺️ 지도 & 경로 API (프론트엔드 사용)

**카카오맵 API**
```
- 용도:
  - 지도 표시
  - 주소 검색
  - 좌표 ↔ 주소 변환
  - 마커 표시
- 처리: 프론트엔드에서 직접 호출
```

**SK Open API (T map)**
```
- 용도:
  - 보행자 경로 탐색
  - 소요 시간 계산
  - 이동 거리 계산
  - 계단 회피 옵션 (휠체어)
- 처리: 프론트엔드에서 직접 호출
```

---

## 🔐 인증 & 보안

**JWT (JSON Web Token)**
```
- 사용자 인증 토큰
- Access Token + Refresh Token
- Refresh Token은 Redis에 저장
```

**PASS 본인인증 API**
```
- 휴대폰 본인인증
- 신원 확인
- 계정 활성화
```

---

## 🛠️ 개발 도구

### 코드 품질

**ESLint**
```
- JavaScript/TypeScript 린터
- 코드 스타일 검사
```

**Prettier**
```
- 코드 포매터
- 일관된 코드 스타일
```

**Husky + lint-staged**
```
- Git Hook
- 커밋 전 자동 린팅/포맷팅
```

### 테스팅

**Jest**
```
- 유닛 테스트 프레임워크
- 코드 커버리지 측정
```

**Supertest**
```
- API 통합 테스트
- HTTP 요청/응답 검증
```

### 모니터링

**Winston**
```
- 로깅 라이브러리
- 구조화된 로그
```

**PM2**
```
- Node.js 프로세스 관리
- 자동 재시작
- 클러스터 모드
```

**Google Cloud Monitoring**
```
- GCP 통합 모니터링
- 메트릭, 로그, 알림
```

---

## 🚀 배포 & CI/CD

**Docker**
```
- 컨테이너화
- 개발 환경 표준화
```

**GitHub Actions**
```
- CI/CD 파이프라인
- 자동 테스트
- 자동 배포
```

**Nginx**
```
- 리버스 프록시
- 로드 밸런싱
- SSL/TLS 종료
```

---

## 📦 주요 npm 패키지

### 필수 패키지

```json
{
  "dependencies": {
    "express": "^4.18.0",
    "socket.io": "^4.0.0",
    "@prisma/client": "^5.0.0",
    "ioredis": "^5.3.0",
    "firebase-admin": "^12.0.0",
    "bcrypt": "^5.1.0",
    "jsonwebtoken": "^9.0.0",
    "cors": "^2.8.5",
    "helmet": "^7.0.0",
    "dotenv": "^16.0.0"
  },
  "devDependencies": {
    "typescript": "^5.0.0",
    "@types/node": "^20.0.0",
    "@types/express": "^4.17.0",
    "prisma": "^5.0.0",
    "eslint": "^8.0.0",
    "prettier": "^3.0.0",
    "jest": "^29.0.0",
    "ts-node": "^10.0.0",
    "nodemon": "^3.0.0"
  }
}
```

---

## 🏗️ 프로젝트 구조

```
companion-backend/
├── src/
│   ├── config/              # 설정 파일
│   │   ├── database.ts      # Prisma 설정
│   │   ├── redis.ts         # Redis 설정
│   │   └── firebase.ts      # Firebase 설정
│   │
│   ├── routes/              # API 라우트
│   │   ├── auth.routes.ts
│   │   ├── user.routes.ts
│   │   ├── companion.routes.ts
│   │   └── review.routes.ts
│   │
│   ├── controllers/         # 요청 처리
│   ├── services/            # 비즈니스 로직
│   ├── repositories/        # 데이터 액세스
│   ├── middlewares/         # 미들웨어
│   ├── socket/              # Socket.io 핸들러
│   ├── utils/               # 유틸리티
│   └── types/               # TypeScript 타입
│
├── prisma/
│   ├── schema.prisma        # DB 스키마
│   └── migrations/          # 마이그레이션
│
├── tests/                   # 테스트
├── .env.example             # 환경변수 예시
├── Dockerfile               # Docker 설정
├── docker-compose.yml       # 로컬 개발 환경
└── package.json
```

---

## 💰 예상 비용 (월)

### 개발 단계 (무료 티어)
```
Compute Engine (e2-micro):        $0 (무료 티어)
Cloud SQL (db-f1-micro):          $0 (무료 티어)
Cloud Storage (5GB):              $0 (무료 한도)
Firebase FCM:                     $0 (무료)
Memorystore (1GB):                $25
─────────────────────────────────────
Total:                            ~$25/월
```

### 프로덕션 (소규모)
```
Compute Engine (e2-medium):       $25
Cloud SQL (db-n1-standard-1):     $50
Memorystore (5GB):                $50
Cloud Storage (20GB):             $1
Cloud Load Balancing:             $20
Firebase FCM:                     $0 (무료 한도 내)
Data Transfer:                    $10
─────────────────────────────────────
Total:                            ~$156/월
```

---

## 🎯 각 기술을 선택한 이유

| 기술 | 선택 이유 |
|------|----------|
| **Node.js** | 비동기 I/O, 실시간 처리에 최적, JavaScript 생태계 |
| **TypeScript** | 타입 안전성, 버그 감소, 유지보수 용이 |
| **Express** | 가볍고 유연, 풍부한 미들웨어 |
| **Socket.io** | 실시간 양방향 통신, 자동 재연결 |
| **MySQL** | 안정적, 트랜잭션 지원, 위치 데이터 처리 |
| **Prisma** | 타입 안전 ORM, 자동 마이그레이션 |
| **Redis** | 초고속 캐싱, Geo 검색, 세션 관리 |
| **GCP** | Firebase와 완벽 통합, 통합 관리, 비용 효율 |
| **Firebase FCM** | 무료, 안정적, GCP 통합 |

---

## ✅ 최종 체크리스트

**백엔드 코어:**
- ✅ Node.js 20 LTS
- ✅ Express.js 4.18
- ✅ TypeScript 5.x
- ✅ Socket.io 4.x

**데이터:**
- ✅ Cloud SQL (MySQL 8.0)
- ✅ Prisma ORM 5.x
- ✅ Memorystore (Redis 7.0)

**인프라 (GCP):**
- ✅ Compute Engine
- ✅ Cloud Load Balancing
- ✅ Cloud Storage
- ✅ Cloud Monitoring

**외부 서비스:**
- ✅ Firebase FCM (푸시 알림)
- ✅ 카카오맵 API (프론트)
- ✅ SK T map API (프론트)
- ✅ PASS 본인인증

**개발 도구:**
- ✅ ESLint + Prettier
- ✅ Jest (테스팅)
- ✅ Docker
- ✅ GitHub Actions

---

## 🚀 시작하기

### 필수 설치
```bash
# Node.js 20 설치
nvm install 20
nvm use 20

# 프로젝트 생성
mkdir companion-backend
cd companion-backend
npm init -y

# 핵심 패키지 설치
npm install express socket.io @prisma/client ioredis firebase-admin
npm install -D typescript @types/node @types/express prisma ts-node nodemon

# TypeScript 초기화
npx tsc --init

# Prisma 초기화
npx prisma init
```

### GCP 설정
```bash
# GCP CLI 설치
# https://cloud.google.com/sdk/docs/install

# GCP 로그인
gcloud auth login

# 프로젝트 생성
gcloud projects create companion-app-12345

# 프로젝트 설정
gcloud config set project companion-app-12345
```

이게 우리 프로젝트의 **최종 확정 기술 스택**입니다! 🎉
