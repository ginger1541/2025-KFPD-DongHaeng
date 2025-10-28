# 동행 (Companion)

<p align="center">
    <img src="docs/logo.png" alt="동행 로고" width="60%"/>
</p>

<p align="center">"지금, 여기서, 잠깐의 도움"으로 일상의 장벽을 함께 넘습니다</p>

> <p align="center">휠체어 사용자와 이웃을 실시간으로 연결하는 소셜 동행 매칭 플랫폼</p>

## Collaborators

<h3 align="center">Team</h3>

<div align="center">

|                          역할                          |                        역할                        |                        역할                         |                       역할                        |
| :----------------------------------------------------: | :-----------------------------------------------------: | :------------------------------------------------------: | :---------------------------------------------------: |
|          [이름](https://github.com/username)          |          [이름](https://github.com/username)          |          [이름](https://github.com/username)          |          [이름](https://github.com/username)          |
| <img src="https://github.com/username.png" width="100"> | <img src="https://github.com/username.png" width="100"> | <img src="https://github.com/username.png" width="100"> | <img src="https://github.com/username.png" width="100"> |

</div>

## Introduction

**기존의 이동 지원 서비스들**에서는 휠체어 사용자가 **즉흥적으로 외출**하기 위해서 사전 예약과 복잡한 절차를 거쳐야 하는 **번거로운 과정이 필요**합니다. 장애인 콜택시, 활동지원사 등은 계획된 일정에 따른 이동에는 효과적이지만, "지금 당장 5분만 도와주세요"와 같은 일상 속 즉흥적인 도움 요청에는 부적합합니다.

우리가 살아가는 도시에는 상점 입구의 작은 턱, 몇 칸의 계단, 좁은 골목길 등 **'마지막 10미터(Last 10-meters)'의 장벽**이 존재합니다.

저희 **동행**은 기존과 차별화된 **실시간 매칭**, 즉, **빠르고 가벼운 도움**을 목표로 하고 있습니다.

---

<details>
    <summary><h3>프로젝트 기획</h3></summary>

### 기획 배경

#### 왜 동행인가?

우리가 살아가는 도시에는 눈에 잘 보이지 않는 수많은 장벽이 존재합니다. 특히 휠체어를 이용하는 이동 약자에게, 상점 입구의 작은 턱 하나, 몇 칸의 계단, 좁은 골목길 등은 가고 싶은 곳을 포기하게 만드는 거대한 벽이 됩니다.

물론, '장애인 콜택시'나 '활동지원사'와 같은 훌륭한 공적 지원 제도가 존재합니다. 하지만 이러한 서비스는 계획된 일정에 따른 '목적지까지의 이동'을 돕는 것에 중점을 둡니다. "지금 당장, 저 카페에 들어가기 위해 5분만 도와주세요" 또는 "저기 언덕까지만 같이 가주세요"와 같은 **일상 속 즉흥적이고 단기적인 도움 요청**을 해결하기에는 너무 무겁고 경직된 방식입니다.

#### 핵심 가치

1. **즉시성 및 유연성 확보**  
   도움이 필요한 바로 그 순간, 그 장소에서 실시간으로 도움을 요청하고 받을 수 있는 유연한 시스템

2. **사회적 관계망 형성**  
   '돌봄'이라는 일방적인 관계를 넘어, '산책'과 '만남'이라는 키워드를 통해 수평적 관계에서 자연스럽게 교류

3. **지속가능한 참여 유도**  
   봉사 시간 인정, 지역 상점 연계 포인트 등 의미 있는 보상 시스템과 게이미피케이션을 통한 지속가능한 생태계

### 차별화 포인트

| 구분 | 기존 서비스 | 동행 |
|------|------------|------|
| **시간** | 계획 기반, 사전 예약 | 즉흥적, 실시간 매칭 |
| **규모** | 장거리 이동 중심 | 짧은 거리, 작은 도움 |
| **관계** | 서비스 제공자 ↔ 수혜자 | 동행자 ↔ 동행자 |
| **분위기** | 공식적, 절차적 | 캐주얼, 따뜻함 |
| **보상** | 의무/업무 | 재미 + 의미 + 보상 |

</details>

## System Structure

### 전체 시스템 구성도

<img src="docs/system-architecture.png" alt="시스템 구성도" width="100%"/>

### 주요 컴포넌트

**Frontend (Mobile App)**
- React Native / Flutter를 활용한 크로스 플랫폼 앱
- 실시간 위치 기반 지도 UI
- 즉시 매칭 및 알림 시스템

**Backend (API Server)**
- RESTful API 설계
- 실시간 위치 추적 및 매칭 알고리즘
- 사용자 인증 및 평가 시스템

**Database**
- 사용자 프로필 및 동행 기록
- 평가 및 신뢰도 데이터
- 지역별 통계 데이터

**External Services**
- GPS/지도 API (Google Maps, Kakao Map)
- 푸시 알림 (FCM)
- 본인 인증 API (PASS)
- 이미지 스토리지 (AWS S3)

### 사용자 플로우

<img src="docs/user-flow.png" alt="사용자 플로우" width="100%"/>

**요청자 (휠체어 사용자) 플로우:**
1. 로그인 → 프로필 설정
2. 메인 지도에서 '동행 요청하기'
3. 목적지 및 예상 시간 입력
4. 주변 도우미 매칭 대기
5. 매칭 완료 → 실시간 위치 확인
6. QR 인증 후 동행 시작
7. 동행 완료 → 상호 평가

**도우미 (이웃) 플로우:**
1. 로그인 → 프로필 설정
2. 주변 요청 알림 수신
3. 요청 확인 및 수락
4. 실시간 위치로 요청자 만남
5. QR 인증 후 동행 시작
6. 동행 완료 → 상호 평가 및 보상 획득

## Tech Stack

<div align="center">

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Node.js](https://img.shields.io/badge/Node.js-339933?style=for-the-badge&logo=node.js&logoColor=white)
![Express](https://img.shields.io/badge/Express-000000?style=for-the-badge&logo=express&logoColor=white)

![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![AWS S3](https://img.shields.io/badge/AWS%20S3-569A31?style=for-the-badge&logo=amazons3&logoColor=white)

![Google Maps](https://img.shields.io/badge/Google%20Maps-4285F4?style=for-the-badge&logo=google-maps&logoColor=white)
![Kakao Maps](https://img.shields.io/badge/Kakao%20Maps-FFCD00?style=for-the-badge&logo=kakao&logoColor=black)

</div>

## Features

앱의 핵심 기능들을 소개합니다.

### 실시간 위치 기반 매칭

**GPS 기반 주변 매칭**
- 사용자의 현재 위치를 중심으로 주변 도우미 실시간 표시
- 거리 기반 알림 우선순위 설정
- 지도 위에서 직관적인 위치 확인

**즉시 동행 요청**
- 목적지와 예상 소요 시간 입력
- 주변 도우미에게 푸시 알림 전송
- 빠른 매칭 완료 (평균 3분 이내)

### 신뢰 기반 커뮤니티

**본인 인증 시스템**
- PASS 앱 또는 신분증 OCR 인증
- 휴대폰 본인 인증 필수
- 프로필 사진 등록 의무화

**동행 지수**
- 동행 횟수와 후기 기반 신뢰도 점수
- 상호 별점 평가 시스템
- 활동 배지 획득 (예: '첫걸음 천사', '동네 지킴이')

**안전 장치**
- 실시간 동행 경로 공유 ('안심 트래킹')
- SOS 긴급 신고 버튼
- 사용자 신고 및 차단 기능
- QR 코드 기반 동행 시작/종료 인증

### 보상 및 게이미피케이션

**공식 봉사 시간 인정**
- 동행 시간 자동 기록
- 1365 자원봉사포털 연동 (예정)
- 학생 및 직장인 봉사 시간 활용

**지역 파트너십**
- '착한 가게' 포인트 적립
- 지역 상점에서 할인 혜택
- 커뮤니티 기여 보상

**재미 요소**
- 동행 랭킹 시스템
- 월별 배지 수집
- 이달의 동행왕 선정

### 커뮤니티 및 소통

**1:1 채팅**
- 매칭 후 실시간 채팅
- 위치 공유 및 만남 조율
- 감사 메시지 및 피드백 교환

**동행 기록**
- 나의 동행 히스토리
- 받은 감사 카드 모음
- 동행 사진 및 추억 공유

## How to Start

### 환경 설정

1. 프로젝트를 클론합니다.

   ```bash
   git clone https://github.com/your-org/companion-app.git
   cd companion-app
   ```

2. 환경 변수를 설정합니다.

   ```bash
   # .env.example 파일을 .env로 복사
   cp .env.example .env
   
   # .env 파일 수정
   vi .env
   ```

   환경 변수 예시:
   ```
   # 데이터베이스
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=companion
   DB_USER=your_username
   DB_PASSWORD=your_password
   
   # Firebase
   FIREBASE_API_KEY=your_firebase_api_key
   FIREBASE_PROJECT_ID=your_project_id
   
   # 지도 API
   GOOGLE_MAPS_API_KEY=your_google_maps_key
   KAKAO_MAPS_API_KEY=your_kakao_maps_key
   
   # AWS S3
   AWS_ACCESS_KEY_ID=your_access_key
   AWS_SECRET_ACCESS_KEY=your_secret_key
   AWS_REGION=ap-northeast-2
   
   # 본인인증 API
   PASS_API_KEY=your_pass_api_key
   ```

3. 의존성을 설치합니다.

   **Backend:**
   ```bash
   cd backend
   npm install
   ```

   **Android App:**
   ```bash
   cd android
   ./gradlew build
   ```

4. 데이터베이스를 초기화합니다.

   ```bash
   # MySQL 컨테이너 실행
   docker-compose up -d mysql
   
   # 마이그레이션 실행
   npm run migrate
   ```

5. 서버를 실행합니다.

   **개발 환경:**
   ```bash
   npm run dev
   ```

   **프로덕션 환경:**
   ```bash
   npm run build
   npm start
   ```

6. 안드로이드 앱을 실행합니다.

   ```bash
   cd android
   ./gradlew installDebug
   ```

## API Documentation

개발한 API들의 상세 명세를 확인할 수 있습니다.

> 각 API의 상세한 명세는 [Swagger 문서](https://api.companion-app.com/docs)를 확인해주세요.

### 주요 엔드포인트

#### 인증 API
- `POST /api/auth/signup` - 회원가입
- `POST /api/auth/login` - 로그인
- `POST /api/auth/verify` - 본인인증
- `POST /api/auth/refresh` - 토큰 갱신

#### 동행 요청 API
- `POST /api/companion/request` - 동행 요청 생성
- `GET /api/companion/nearby` - 주변 요청 조회
- `POST /api/companion/accept` - 동행 요청 수락
- `POST /api/companion/start` - 동행 시작 (QR 인증)
- `POST /api/companion/complete` - 동행 완료

#### 사용자 API
- `GET /api/users/profile` - 프로필 조회
- `PUT /api/users/profile` - 프로필 수정
- `GET /api/users/history` - 동행 기록 조회
- `GET /api/users/badges` - 배지 조회

#### 평가 API
- `POST /api/reviews` - 평가 작성
- `GET /api/reviews/{userId}` - 사용자 평가 조회

### 인증 방식

**JWT 토큰 사용**
- ACCESS TOKEN과 REFRESH TOKEN 구현
- REFRESH TOKEN은 Redis에 저장
- RTR(Refresh Token Rotation) 전략 적용
- ACCESS 토큰은 Authorization 헤더로 전달
- REFRESH 토큰은 HTTP-Only 쿠키로 전달

## Database Schema

### ERD (Entity Relationship Diagram)

<img src="docs/erd.png" alt="ERD" width="100%"/>

### 주요 테이블

**users (사용자)**
```
- id (PK)
- email
- username
- phone
- profile_image
- user_type (요청자/도우미)
- trust_score (동행 지수)
- created_at
```

**companion_requests (동행 요청)**
```
- id (PK)
- requester_id (FK → users)
- start_location (출발지 좌표)
- destination (목적지 좌표)
- estimated_time (예상 소요 시간)
- status (대기/매칭완료/진행중/완료)
- created_at
```

**companion_sessions (동행 세션)**
```
- id (PK)
- request_id (FK → companion_requests)
- helper_id (FK → users)
- start_time
- end_time
- actual_time (실제 소요 시간)
- route_data (경로 데이터)
```

**reviews (평가)**
```
- id (PK)
- session_id (FK → companion_sessions)
- reviewer_id (FK → users)
- reviewee_id (FK → users)
- rating (1-5)
- comment
- created_at
```

**badges (배지)**
```
- id (PK)
- user_id (FK → users)
- badge_type (배지 종류)
- earned_at
```

## Development Roadmap

### Phase 1: MVP 출시 (3개월)
- [x] 기본 UI/UX 디자인
- [x] 실시간 위치 기반 매칭 시스템
- [x] 본인 인증 및 프로필 관리
- [x] QR 코드 인증 기능
- [ ] 상호 평가 시스템
- [ ] 안드로이드 앱 베타 출시

### Phase 2: 기능 확장 (3개월)
- [ ] 1365 자원봉사포털 연동
- [ ] 지역 상점 파트너십 구축
- [ ] 게이미피케이션 강화 (배지, 랭킹)
- [ ] iOS 앱 출시
- [ ] 사용자 피드백 반영

### Phase 3: 지역 확장 (6개월)
- [ ] 광주 → 서울/부산 확장
- [ ] 배리어프리 지도 데이터 수집
- [ ] 지자체 협력 체계 구축
- [ ] 데이터 기반 정책 제안

### Phase 4: 고도화 (장기)
- [ ] AI 기반 매칭 최적화
- [ ] 음성 명령 지원
- [ ] 다국어 지원
- [ ] 글로벌 확장

## Contributing

'동행' 프로젝트는 더 많은 분들의 참여로 완성됩니다.

### 참여 방법

**개발자**
- 코드 기여 (Pull Request)
- 버그 리포트 (Issue)
- 문서 개선

**디자이너**
- UI/UX 개선 제안
- 아이콘 및 일러스트 제작

**휠체어 사용자**
- 사용성 피드백
- 기능 제안
- 베타 테스트 참여

**지역 상점**
- 파트너십 문의
- 지역 커뮤니티 연결

### 개발 가이드라인

1. 이슈를 먼저 작성하여 논의합니다
2. `develop` 브랜치에서 feature 브랜치를 생성합니다
3. 커밋 메시지는 [Conventional Commits](https://www.conventionalcommits.org/) 규칙을 따릅니다
4. Pull Request 시 테스트 코드를 포함합니다
5. 코드 리뷰 후 merge 됩니다

## License

이 프로젝트는 MIT 라이선스를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참고하세요.

## Contact

- **프로젝트 홈페이지**: https://companion-app.com
- **이메일**: contact@companion-app.com
- **이슈 트래커**: https://github.com/your-org/companion-app/issues

## Acknowledgments

이 프로젝트는 다음 분들의 도움으로 완성되었습니다:

- 광주광역시 장애인복지관 자문
- 휠체어 사용자 자문단
- 카카오 테크 캠퍼스 지원
- 오픈소스 커뮤니티

---

<div align="center">

**"기술이 만드는 따뜻한 연결, 동행"**

*Made with ❤️ for a barrier-free world*

[![GitHub stars](https://img.shields.io/github/stars/your-org/companion-app?style=social)](https://github.com/your-org/companion-app)
[![GitHub forks](https://img.shields.io/github/forks/your-org/companion-app?style=social)](https://github.com/your-org/companion-app/fork)
[![GitHub issues](https://img.shields.io/github/issues/your-org/companion-app)](https://github.com/your-org/companion-app/issues)

</div>
