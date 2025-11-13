# 🤝 동행(Companion) 백엔드 서버

> 이동 약자를 위한 실시간 동행 매칭 플랫폼

## 🚀 빠른 시작

### 1. 패키지 설치

```bash
npm install
```

### 2. 환경변수 설정

`.env` 파일이 이미 생성되어 있습니다. 필요한 값들을 수정하세요:

```env
DATABASE_URL="mysql://root:password@localhost:3306/dongheng_db"
JWT_SECRET="your-secret-key"
```

### 3. 데이터베이스 생성

```bash
# MySQL 접속
mysql -u root -p

# 데이터베이스 생성
CREATE DATABASE dongheng_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

### 4. Prisma 마이그레이션

```bash
# Prisma Client 생성
npm run prisma:generate

# 마이그레이션 실행
npm run prisma:migrate

# SPATIAL INDEX 수동 추가
mysql -u root -p dongheng_db
ALTER TABLE user_locations ADD SPATIAL INDEX idx_coordinates (coordinates);
ALTER TABLE companion_requests ADD SPATIAL INDEX idx_start_location (start_location);
exit;
```

### 5. 개발 서버 실행

```bash
npm run dev
```

서버가 http://localhost:3000 에서 실행됩니다.

---

## 📁 프로젝트 구조

```
companion-backend/
├── prisma/
│   └── schema.prisma         # 데이터베이스 스키마
├── src/
│   ├── index.ts              # 서버 진입점
│   ├── app.ts                # Express 앱 설정
│   ├── config/               # 설정 파일
│   │   ├── database.ts       # Prisma Client
│   │   ├── redis.ts          # Redis 설정
│   │   └── logger.ts         # Winston 로거
│   ├── routes/               # API 라우트
│   ├── controllers/          # 컨트롤러
│   ├── services/             # 비즈니스 로직
│   ├── repositories/         # 데이터 액세스
│   ├── middlewares/          # 미들웨어
│   ├── socket/               # Socket.io
│   ├── validators/           # 요청 검증
│   ├── utils/                # 유틸리티
│   └── types/                # TypeScript 타입
├── tests/                    # 테스트
├── .env                      # 환경변수
├── tsconfig.json             # TypeScript 설정
└── package.json
```

---

## 📝 사용 가능한 명령어

```bash
# 개발 서버 (hot reload)
npm run dev

# 빌드
npm run build

# 프로덕션 서버
npm start

# Prisma
npm run prisma:generate      # Client 생성
npm run prisma:migrate       # 마이그레이션
npm run prisma:studio        # GUI 실행

# 코드 품질
npm run lint                 # ESLint
npm run lint:fix             # ESLint 자동 수정
npm run format               # Prettier 포맷팅

# 테스트
npm test                     # 테스트 실행
npm run test:watch           # Watch 모드
npm run test:coverage        # 커버리지
```

---

## 🔧 다음 단계

1. ✅ 프로젝트 초기 설정 완료
2. 📝 API 구현 시작
   - 회원가입/로그인
   - 프로필 관리
   - 동행 요청/매칭
3. 🔌 Socket.io 실시간 통신
4. 🧪 테스트 코드 작성
5. 🚀 GCP 배포

---

## 🌐 API 엔드포인트

현재 활성화된 엔드포인트:

- `GET /health` - 서버 상태 확인
- `GET /api` - API 정보

추후 추가 예정:
- `/api/auth/*` - 인증 관련
- `/api/users/*` - 사용자 관련
- `/api/companions/*` - 동행 요청
- `/api/matches/*` - 매칭 관련
- `/api/reviews/*` - 리뷰 관련

---

## 📚 참고 문서

- [Prisma 가이드](./docs/PRISMA_GUIDE.md)
- [API 명세서](./docs/dongheng_api_specification.md)
- [데이터베이스 스키마](./docs/dongheng_database_schema.md)

---

**Made with ❤️ by Companion Team**
