# 동행(Dongheng) API 명세서

> 작성일: 2025-10-30  
> 버전: 1.0  
> 기반: 시퀀스 다이어그램 v1.0 + DB 스키마 v1.4

---

## 📋 목차

1. [개요](#개요)
2. [인증](#인증)
3. [공통 사항](#공통-사항)
4. [API 엔드포인트](#api-엔드포인트)
   - [인증 관련](#1-인증-관련)
   - [사용자 프로필](#2-사용자-프로필)
   - [동행 요청](#3-동행-요청)
   - [매칭](#4-매칭)
   - [QR 인증](#5-qr-인증)
   - [위치](#6-위치)
   - [채팅](#7-채팅)
   - [평가](#8-평가)
   - [알림](#9-알림)
   - [기타](#10-기타)
5. [WebSocket](#websocket)
6. [에러 코드](#에러-코드)

---

## 개요

### Base URL
```
Production: https://api.dongheng.app
Development: https://dev-api.dongheng.app
Local: http://localhost:8080
```

### API 버전
```
v1: /api/v1/*
```

### 지원 형식
- Request: `application/json`, `multipart/form-data` (이미지 업로드)
- Response: `application/json`

---

## 인증

### 인증 방식
**Bearer Token (JWT)**

```http
Authorization: Bearer {access_token}
```

### 토큰 구조
```json
{
  "user_id": 12345,
  "email": "user@example.com",
  "user_type": "helper",
  "exp": 1735689600
}
```

### 토큰 만료
- **Access Token**: 30일
- **Refresh Token**: 90일 (추후 구현)

---

## 공통 사항

### HTTP 상태 코드
| 코드 | 의미 | 사용 예시 |
|------|------|-----------|
| 200 | OK | 조회/수정 성공 |
| 201 | Created | 생성 성공 |
| 204 | No Content | 삭제 성공 |
| 400 | Bad Request | 잘못된 요청 |
| 401 | Unauthorized | 인증 실패 |
| 403 | Forbidden | 권한 없음 |
| 404 | Not Found | 리소스 없음 |
| 409 | Conflict | 중복/충돌 |
| 500 | Internal Server Error | 서버 오류 |

### 공통 Response 형식

#### 성공 응답
```json
{
  "success": true,
  "data": { ... },
  "message": "성공 메시지"
}
```

#### 에러 응답
```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "사용자에게 표시할 에러 메시지",
    "details": "개발자용 상세 정보 (선택)"
  }
}
```

### Pagination
```json
{
  "data": [ ... ],
  "pagination": {
    "page": 1,
    "per_page": 20,
    "total": 150,
    "total_pages": 8
  }
}
```

---

## API 엔드포인트

---

## 1. 인증 관련

### 1.1. 소셜 로그인

**소셜 OAuth를 통한 로그인 및 회원가입**

```http
POST /api/auth/social-login
```

**Request Body**
```json
{
  "provider": "kakao",  // "kakao" | "naver" | "google"
  "code": "authorization_code_from_oauth"
}
```

**Response 200 OK** (기존 회원)
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user_id": 12345,
    "is_new_user": false,
    "user": {
      "email": "user@example.com",
      "name": "홍길동",
      "profile_image_url": "https://cdn.../profile.jpg",
      "user_type": "helper",
      "companion_score": 85.5
    }
  }
}
```

**Response 201 Created** (신규 회원)
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user_id": 12346,
    "is_new_user": true,
    "user": {
      "email": "newuser@example.com"
    }
  }
}
```

**Error Responses**
- `400 Bad Request`: 잘못된 provider 또는 code
- `500 Internal Server Error`: OAuth 서버 오류

---

### 1.2. 토큰 검증

**현재 토큰의 유효성 확인 및 사용자 정보 조회**

```http
GET /api/auth/verify
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "user_id": 12345,
    "email": "user@example.com",
    "is_verified": true,
    "is_active": true
  }
}
```

**Error Responses**
- `401 Unauthorized`: 토큰 만료 또는 유효하지 않음

---

### 1.3. 본인 인증 요청

**PASS 앱을 통한 본인 인증 시작**

```http
POST /api/auth/verification/request
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "phone": "01012345678"
}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "session_id": "pass-session-abc123",
    "qr_code_url": "https://pass-api.../qr?session=abc123",
    "expires_in": 300  // 5분
  }
}
```

**Error Responses**
- `400 Bad Request`: 이미 인증된 사용자
- `429 Too Many Requests`: 인증 요청 횟수 초과

---

### 1.4. 본인 인증 완료 (Webhook)

**PASS 서버에서 호출하는 콜백 엔드포인트**

```http
POST /api/auth/verification/callback
Content-Type: application/json
```

**Request Body** (PASS 서버)
```json
{
  "session_id": "pass-session-abc123",
  "verified": true,
  "name": "홍길동",
  "phone": "01012345678",
  "birth_date": "1990-01-01",
  "ci": "encrypted_ci_value"
}
```

**Response 200 OK**
```json
{
  "success": true
}
```

---

## 2. 사용자 프로필

### 2.1. 프로필 조회

**특정 사용자의 프로필 정보 조회**

```http
GET /api/users/{user_id}
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "user_id": 12345,
    "name": "홍길동",
    "email": "user@example.com",
    "phone": "010-1234-5678",
    "profile_image_url": "https://cdn.../profile.jpg",
    "bio": "반갑습니다!",
    "user_type": "helper",
    "birth_date": "1990-01-01",
    "gender": "male",
    "companion_score": 85.5,
    "total_companions": 42,
    "total_volunteer_minutes": 1260,
    "total_points": 5000,
    "badges": [
      {
        "badge_id": 1,
        "badge_name": "첫걸음 천사",
        "badge_icon_url": "https://cdn.../badge1.png",
        "earned_at": "2025-01-15T10:30:00Z"
      }
    ],
    "created_at": "2025-01-01T00:00:00Z"
  }
}
```

**Error Responses**
- `404 Not Found`: 존재하지 않는 사용자

---

### 2.2. 프로필 수정

**사용자 기본 정보 수정**

```http
PUT /api/users/{user_id}/profile
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "name": "홍길동",
  "bio": "안녕하세요, 동행하는 우인입니다",
  "birth_date": "1990-01-01",
  "gender": "male"
}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "user_id": 12345,
    "name": "홍길동",
    "bio": "안녕하세요, 동행하는 우인입니다",
    "updated_at": "2025-10-30T12:00:00Z"
  }
}
```

**Error Responses**
- `400 Bad Request`: 유효하지 않은 입력
- `403 Forbidden`: 다른 사용자 프로필 수정 시도

---

### 2.3. 프로필 사진 업로드

**프로필 사진 업로드 (얼굴 인식 검증 포함)**

```http
POST /api/users/{user_id}/profile-image
Authorization: Bearer {token}
Content-Type: multipart/form-data
```

**Request Body**
```
image: [File] (최대 5MB, jpg/png)
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "profile_image_url": "https://cdn.dongheng.app/users/12345/profile.jpg",
    "uploaded_at": "2025-10-30T12:00:00Z"
  }
}
```

**Error Responses**
- `400 Bad Request`: 얼굴이 인식되지 않는 이미지
- `413 Payload Too Large`: 파일 크기 초과

---

### 2.4. 사용자 유형 설정

**요청자/도우미 유형 선택**

```http
PUT /api/users/{user_id}/user-type
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "user_type": "helper"  // "requester" | "helper" | "both"
}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "user_id": 12345,
    "user_type": "helper",
    "updated_at": "2025-10-30T12:00:00Z"
  }
}
```

---

### 2.5. 약관 동의

**서비스 이용 약관 동의 기록**

```http
POST /api/users/{user_id}/consents
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "service_terms": true,
  "privacy_policy": true,
  "location_terms": true,
  "marketing": false  // 선택 항목
}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "consents": [
      {
        "consent_type": "service_terms",
        "agreed": true,
        "agreed_at": "2025-10-30T12:00:00Z"
      }
    ]
  }
}
```

---

## 3. 동행 요청

### 3.1. 동행 요청 생성

**새로운 동행 요청 생성**

```http
POST /api/companion-requests
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "title": "세브란스병원까지 동행 부탁드려요",
  "description": "병원 진료 예약이 있어서 가야 합니다",
  "start_location": {
    "latitude": 35.1595,
    "longitude": 126.8526
  },
  "destination": {
    "latitude": 35.1612,
    "longitude": 126.8540
  },
  "start_address": "광주광역시 동구 동명동 123",
  "destination_address": "광주 동구 세브란스병원",
  "estimated_minutes": 15
}
```

**Response 201 Created**
```json
{
  "success": true,
  "data": {
    "request_id": 789,
    "requester_id": 12345,
    "title": "세브란스병원까지 동행 부탁드려요",
    "status": "pending",
    "start_location": {
      "latitude": 35.1595,
      "longitude": 126.8526
    },
    "destination": {
      "latitude": 35.1612,
      "longitude": 126.8540
    },
    "estimated_minutes": 15,
    "requested_at": "2025-10-30T14:00:00Z",
    "expires_at": "2025-10-30T14:30:00Z"
  },
  "message": "요청이 생성되었습니다. 주변 도우미에게 알림을 전송했습니다."
}
```

**Error Responses**
- `400 Bad Request`: 필수 필드 누락 또는 유효하지 않은 좌표
- `429 Too Many Requests`: 동시 요청 개수 제한 초과

---

### 3.2. 동행 요청 상세 조회

**특정 동행 요청의 상세 정보 조회**

```http
GET /api/companion-requests/{request_id}
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "request_id": 789,
    "requester": {
      "user_id": 12345,
      "name": "홍길동",
      "profile_image_url": "https://cdn.../profile.jpg",
      "companion_score": 85.5
    },
    "title": "세브란스병원까지 동행 부탁드려요",
    "description": "병원 진료 예약이 있어서 가야 합니다",
    "status": "pending",
    "start_location": {
      "latitude": 35.1595,
      "longitude": 126.8526
    },
    "destination": {
      "latitude": 35.1612,
      "longitude": 126.8540
    },
    "start_address": "광주광역시 동구 동명동 123",
    "destination_address": "광주 동구 세브란스병원",
    "estimated_minutes": 15,
    "view_count": 5,
    "requested_at": "2025-10-30T14:00:00Z",
    "expires_at": "2025-10-30T14:30:00Z"
  }
}
```

**Error Responses**
- `404 Not Found`: 존재하지 않는 요청

---

### 3.3. 주변 동행 요청 목록 조회

**현재 위치 기반 주변 요청 목록**

```http
GET /api/companion-requests/nearby
Authorization: Bearer {token}
```

**Query Parameters**
- `latitude` (required): 현재 위도
- `longitude` (required): 현재 경도
- `radius` (optional): 검색 반경 (미터, 기본값: 5000)
- `status` (optional): 요청 상태 필터 (기본값: pending)

**Example**
```
GET /api/companion-requests/nearby?latitude=35.1595&longitude=126.8526&radius=3000
```

**Response 200 OK**
```json
{
  "success": true,
  "data": [
    {
      "request_id": 789,
      "requester": {
        "user_id": 12345,
        "name": "홍길동",
        "profile_image_url": "https://cdn.../profile.jpg",
        "companion_score": 85.5
      },
      "title": "세브란스병원까지 동행 부탁드려요",
      "start_location": {
        "latitude": 35.1595,
        "longitude": 126.8526
      },
      "destination": {
        "latitude": 35.1612,
        "longitude": 126.8540
      },
      "distance": 1200,  // 현재 위치로부터의 거리 (미터)
      "estimated_minutes": 15,
      "expires_at": "2025-10-30T14:30:00Z"
    }
  ]
}
```

---

### 3.4. 동행 요청 취소

**생성한 동행 요청 취소**

```http
DELETE /api/companion-requests/{request_id}
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "message": "요청이 취소되었습니다"
}
```

**Error Responses**
- `403 Forbidden`: 요청 작성자가 아님
- `400 Bad Request`: 이미 매칭된 요청은 취소 불가

---

## 4. 매칭

### 4.1. 매칭 수락

**도우미가 동행 요청 수락**

```http
POST /api/matches
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "request_id": 789
}
```

**Response 201 Created**
```json
{
  "success": true,
  "data": {
    "match_id": 1001,
    "request_id": 789,
    "requester": {
      "user_id": 12345,
      "name": "홍길동",
      "profile_image_url": "https://cdn.../profile.jpg",
      "phone": "010-1234-5678"
    },
    "helper_id": 12346,
    "status": "accepted",
    "start_qr_code": "qr-start-abc123",
    "matched_at": "2025-10-30T14:05:00Z"
  },
  "message": "매칭이 성사되었습니다"
}
```

**Error Responses**
- `409 Conflict`: 이미 다른 도우미와 매칭됨
- `404 Not Found`: 존재하지 않는 요청
- `400 Bad Request`: 만료된 요청

---

### 4.2. 매칭 정보 조회

**특정 매칭의 상세 정보**

```http
GET /api/matches/{match_id}
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "match_id": 1001,
    "request_id": 789,
    "requester": {
      "user_id": 12345,
      "name": "홍길동",
      "profile_image_url": "https://cdn.../profile.jpg"
    },
    "helper": {
      "user_id": 12346,
      "name": "김도우",
      "profile_image_url": "https://cdn.../helper.jpg"
    },
    "status": "ongoing",
    "matched_at": "2025-10-30T14:05:00Z",
    "started_at": "2025-10-30T14:10:00Z",
    "completed_at": null,
    "actual_duration_minutes": null
  }
}
```

---

### 4.3. 내 동행 내역 조회

**사용자의 과거 동행 기록**

```http
GET /api/users/{user_id}/matches
Authorization: Bearer {token}
```

**Query Parameters**
- `status` (optional): 필터 (accepted, ongoing, completed, cancelled)
- `page` (optional): 페이지 번호 (기본값: 1)
- `per_page` (optional): 페이지당 개수 (기본값: 20)

**Response 200 OK**
```json
{
  "success": true,
  "data": [
    {
      "match_id": 1001,
      "partner": {
        "user_id": 12345,
        "name": "홍길동",
        "profile_image_url": "https://cdn.../profile.jpg"
      },
      "status": "completed",
      "matched_at": "2025-10-30T14:05:00Z",
      "actual_duration_minutes": 25,
      "earned_points": 250,
      "earned_volunteer_minutes": 25
    }
  ],
  "pagination": {
    "page": 1,
    "per_page": 20,
    "total": 42,
    "total_pages": 3
  }
}
```

---

## 5. QR 인증

### 5.1. QR 코드 조회 (시작)

**동행 시작용 QR 코드 조회**

```http
GET /api/matches/{match_id}/qr/start
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "qr_code": "qr-start-abc123",
    "qr_image_url": "https://api.dongheng.app/qr/images/qr-start-abc123.png",
    "auth_type": "start",
    "created_at": "2025-10-30T14:05:00Z",
    "scanned": false
  }
}
```

**Error Responses**
- `404 Not Found`: QR 코드 없음 (매칭되지 않음)
- `403 Forbidden`: 해당 매칭의 참여자가 아님

---

### 5.2. QR 코드 조회 (종료)

**동행 종료용 QR 코드 조회**

```http
GET /api/matches/{match_id}/qr/end
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "qr_code": "qr-end-xyz789",
    "qr_image_url": "https://api.dongheng.app/qr/images/qr-end-xyz789.png",
    "auth_type": "end",
    "created_at": "2025-10-30T14:05:00Z",
    "scanned": false
  }
}
```

---

### 5.3. QR 코드 스캔

**QR 코드 스캔하여 인증**

```http
POST /api/qr/scan
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "qr_code": "qr-start-abc123",
  "location": {
    "latitude": 35.1595,
    "longitude": 126.8526
  }
}
```

**Response 200 OK** (시작 QR 스캔)
```json
{
  "success": true,
  "data": {
    "match_id": 1001,
    "auth_type": "start",
    "scanned_at": "2025-10-30T14:10:00Z",
    "status": "ongoing"
  },
  "message": "동행이 시작되었습니다"
}
```

**Response 200 OK** (종료 QR 스캔)
```json
{
  "success": true,
  "data": {
    "match_id": 1001,
    "auth_type": "end",
    "scanned_at": "2025-10-30T14:35:00Z",
    "status": "completed",
    "actual_duration_minutes": 25,
    "earned_points": 250,
    "earned_volunteer_minutes": 25
  },
  "message": "동행이 완료되었습니다. 포인트 250점과 봉사시간 25분이 지급되었습니다."
}
```

**Error Responses**
- `400 Bad Request`: 유효하지 않은 QR 코드
- `409 Conflict`: 이미 스캔된 QR 코드

---

## 6. 위치

### 6.1. 위치 정보 전송

**사용자의 현재 위치 업데이트**

```http
POST /api/users/{user_id}/location
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "latitude": 35.1595,
  "longitude": 126.8526,
  "is_active": true
}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "location_id": 5001,
    "user_id": 12345,
    "latitude": 35.1595,
    "longitude": 126.8526,
    "updated_at": "2025-10-30T14:10:00Z"
  }
}
```

**Note**: 실시간 위치 공유는 WebSocket 사용 권장

---

## 7. 채팅

### 7.1. 채팅 메시지 전송

**1:1 채팅 메시지 전송 (HTTP fallback)**

```http
POST /api/matches/{match_id}/messages
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "message": "친절해요"
}
```

**Response 201 Created**
```json
{
  "success": true,
  "data": {
    "message_id": 7001,
    "match_id": 1001,
    "sender_id": 12345,
    "message_content": "친절해요",
    "created_at": "2025-10-30T14:15:00Z"
  }
}
```

**Note**: 실시간 채팅은 WebSocket 사용 권장

---

### 7.2. 채팅 내역 조회

**특정 매칭의 채팅 히스토리**

```http
GET /api/matches/{match_id}/messages
Authorization: Bearer {token}
```

**Query Parameters**
- `limit` (optional): 최대 메시지 개수 (기본값: 50)
- `before` (optional): 특정 메시지 이전 내역 (cursor-based pagination)

**Response 200 OK**
```json
{
  "success": true,
  "data": [
    {
      "message_id": 7001,
      "sender_id": 12345,
      "sender_name": "홍길동",
      "message_content": "친절해요",
      "is_read": true,
      "read_at": "2025-10-30T14:15:30Z",
      "created_at": "2025-10-30T14:15:00Z"
    },
    {
      "message_id": 7002,
      "sender_id": 12346,
      "sender_name": "김도우",
      "message_content": "감사합니다",
      "is_read": false,
      "created_at": "2025-10-30T14:16:00Z"
    }
  ]
}
```

---

## 8. 평가

### 8.1. 후기 작성

**동행 완료 후 상대방 평가**

```http
POST /api/reviews
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "match_id": 1001,
  "reviewee_id": 12346,
  "rating": 5,
  "comment": "정말 친절하고 도움이 많이 되었어요. 감사합니다!",
  "selected_badges": ["친절해요", "시간 잘 지켰어요", "소통이 원활해요"]
}
```

**Response 201 Created**
```json
{
  "success": true,
  "data": {
    "review_id": 2001,
    "match_id": 1001,
    "reviewer_id": 12345,
    "reviewee_id": 12346,
    "rating": 5,
    "comment": "정말 친절하고 도움이 많이 되었어요. 감사합니다!",
    "selected_badges": ["친절해요", "시간 잘 지켰어요", "소통이 원활해요"],
    "created_at": "2025-10-30T14:40:00Z"
  },
  "message": "평가가 완료되었습니다"
}
```

**Error Responses**
- `400 Bad Request`: 이미 평가 완료
- `403 Forbidden`: 해당 매칭의 참여자가 아님

---

### 8.2. 받은 후기 조회

**특정 사용자가 받은 후기 목록**

```http
GET /api/users/{user_id}/reviews
Authorization: Bearer {token}
```

**Query Parameters**
- `page` (optional): 페이지 번호
- `per_page` (optional): 페이지당 개수 (기본값: 20)

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "average_rating": 4.8,
    "total_reviews": 42,
    "reviews": [
      {
        "review_id": 2001,
        "reviewer": {
          "user_id": 12345,
          "name": "홍길동",
          "profile_image_url": "https://cdn.../profile.jpg"
        },
        "rating": 5,
        "comment": "정말 친절하고 도움이 많이 되었어요.",
        "selected_badges": ["친절해요", "시간 잘 지켰어요"],
        "created_at": "2025-10-30T14:40:00Z"
      }
    ]
  },
  "pagination": {
    "page": 1,
    "per_page": 20,
    "total": 42,
    "total_pages": 3
  }
}
```

---

## 9. 알림

### 9.1. 알림 목록 조회

**사용자의 알림 목록**

```http
GET /api/notifications
Authorization: Bearer {token}
```

**Query Parameters**
- `is_read` (optional): 읽음 여부 필터 (true/false)
- `page` (optional): 페이지 번호

**Response 200 OK**
```json
{
  "success": true,
  "data": [
    {
      "notification_id": 3001,
      "type": "match_accepted",
      "title": "도우미가 수락했습니다!",
      "content": "김도우님이 동행을 수락했습니다.",
      "data": {
        "match_id": 1001
      },
      "is_read": false,
      "created_at": "2025-10-30T14:05:00Z"
    }
  ],
  "pagination": {
    "page": 1,
    "per_page": 20,
    "total": 15
  }
}
```

---

### 9.2. 알림 읽음 처리

**알림을 읽음으로 표시**

```http
PUT /api/notifications/{notification_id}/read
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "notification_id": 3001,
    "is_read": true,
    "read_at": "2025-10-30T14:10:00Z"
  }
}
```

---

## 10. 기타

### 10.1. SOS 긴급 신고

**동행 중 긴급 상황 신고**

```http
POST /api/sos
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "match_id": 1001,
  "location": {
    "latitude": 35.1595,
    "longitude": 126.8526
  },
  "description": "도움이 필요합니다"
}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": {
    "sos_id": 4001,
    "match_id": 1001,
    "reported_at": "2025-10-30T14:20:00Z",
    "status": "reported"
  },
  "message": "긴급 신고가 접수되었습니다. 관리자가 확인 중입니다."
}
```

---

### 10.2. 신고하기

**사용자 또는 동행 신고**

```http
POST /api/reports
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "reported_user_id": 12346,
  "match_id": 1001,
  "report_type": "inappropriate_behavior",  // "inappropriate_behavior" | "no_show" | "safety_threat" | "other"
  "description": "약속 시간에 나타나지 않았습니다"
}
```

**Response 201 Created**
```json
{
  "success": true,
  "data": {
    "report_id": 5001,
    "status": "pending",
    "created_at": "2025-10-30T15:00:00Z"
  },
  "message": "신고가 접수되었습니다. 검토 후 조치하겠습니다."
}
```

---

### 10.3. 사용자 차단

**특정 사용자 차단**

```http
POST /api/users/block
Authorization: Bearer {token}
```

**Request Body**
```json
{
  "blocked_user_id": 12346
}
```

**Response 200 OK**
```json
{
  "success": true,
  "message": "사용자를 차단했습니다"
}
```

---

### 10.4. 차단 목록 조회

**내가 차단한 사용자 목록**

```http
GET /api/users/blocked
Authorization: Bearer {token}
```

**Response 200 OK**
```json
{
  "success": true,
  "data": [
    {
      "blocked_user_id": 12346,
      "name": "차단된사용자",
      "blocked_at": "2025-10-30T15:00:00Z"
    }
  ]
}
```

---

## WebSocket

### 연결

**실시간 위치 공유 및 채팅**

```
wss://api.dongheng.app/ws/matches/{match_id}?token={jwt_token}
```

### 메시지 형식

#### Client → Server

**위치 업데이트**
```json
{
  "type": "location",
  "latitude": 35.1595,
  "longitude": 126.8526
}
```

**채팅 메시지**
```json
{
  "type": "message",
  "content": "친절해요"
}
```

**입력 중 표시**
```json
{
  "type": "typing_start"
}
```

**메시지 읽음**
```json
{
  "type": "message_read",
  "message_id": 7001
}
```

#### Server → Client

**상대방 위치**
```json
{
  "type": "location",
  "user_id": 12346,
  "latitude": 35.1600,
  "longitude": 126.8530
}
```

**근접 알림**
```json
{
  "type": "proximity_alert",
  "distance": 50,
  "message": "상대방이 가까이 있습니다"
}
```

**새 메시지**
```json
{
  "type": "new_message",
  "message_id": 7001,
  "sender_id": 12346,
  "sender_name": "김도우",
  "content": "친절해요",
  "timestamp": "2025-10-30T14:15:00Z"
}
```

**입력 중**
```json
{
  "type": "typing_indicator",
  "user_id": 12346,
  "status": "typing"
}
```

**동행 종료**
```json
{
  "type": "tracking_ended",
  "reason": "companion_completed"
}
```

---

## 에러 코드

### 인증 관련
| 코드 | 메시지 | 설명 |
|------|--------|------|
| `AUTH_INVALID_TOKEN` | 유효하지 않은 토큰입니다 | JWT 검증 실패 |
| `AUTH_EXPIRED_TOKEN` | 토큰이 만료되었습니다 | 토큰 만료 |
| `AUTH_UNAUTHORIZED` | 인증이 필요합니다 | 토큰 없음 |
| `AUTH_VERIFICATION_FAILED` | 본인 인증에 실패했습니다 | PASS 인증 실패 |

### 사용자 관련
| 코드 | 메시지 | 설명 |
|------|--------|------|
| `USER_NOT_FOUND` | 사용자를 찾을 수 없습니다 | 존재하지 않는 사용자 |
| `USER_ALREADY_EXISTS` | 이미 가입된 사용자입니다 | 중복 가입 |
| `USER_NOT_VERIFIED` | 본인 인증이 필요합니다 | 미인증 사용자 |
| `USER_INACTIVE` | 비활성화된 계정입니다 | 정지된 계정 |

### 동행 요청 관련
| 코드 | 메시지 | 설명 |
|------|--------|------|
| `REQUEST_NOT_FOUND` | 요청을 찾을 수 없습니다 | 존재하지 않는 요청 |
| `REQUEST_EXPIRED` | 만료된 요청입니다 | 시간 초과 |
| `REQUEST_ALREADY_MATCHED` | 이미 매칭된 요청입니다 | 중복 매칭 시도 |
| `REQUEST_CANCELLED` | 취소된 요청입니다 | 취소된 요청 접근 |

### 매칭 관련
| 코드 | 메시지 | 설명 |
|------|--------|------|
| `MATCH_NOT_FOUND` | 매칭 정보를 찾을 수 없습니다 | 존재하지 않는 매칭 |
| `MATCH_ALREADY_EXISTS` | 이미 다른 도우미와 매칭되었습니다 | 중복 매칭 |
| `MATCH_FORBIDDEN` | 해당 매칭에 접근할 수 없습니다 | 권한 없음 |

### QR 관련
| 코드 | 메시지 | 설명 |
|------|--------|------|
| `QR_INVALID` | 유효하지 않은 QR 코드입니다 | 잘못된 QR |
| `QR_ALREADY_SCANNED` | 이미 스캔된 QR 코드입니다 | 중복 스캔 |
| `QR_EXPIRED` | 만료된 QR 코드입니다 | QR 만료 |

### 일반 에러
| 코드 | 메시지 | 설명 |
|------|--------|------|
| `VALIDATION_ERROR` | 입력값이 올바르지 않습니다 | 유효성 검증 실패 |
| `RATE_LIMIT_EXCEEDED` | 요청 횟수를 초과했습니다 | Rate limit |
| `INTERNAL_SERVER_ERROR` | 서버 오류가 발생했습니다 | 서버 에러 |
| `SERVICE_UNAVAILABLE` | 서비스를 일시적으로 사용할 수 없습니다 | 점검 중 |

---

## 변경 이력

| 버전 | 날짜 | 변경 내용 |
|------|------|-----------|
| 1.0 | 2025-10-30 | 초안 작성 (시퀀스 다이어그램 기반) |

---

**작성자**: Claude (Anthropic AI)  
**검토자**: [백엔드 개발자명]  
**승인자**: [프로젝트 매니저명]
