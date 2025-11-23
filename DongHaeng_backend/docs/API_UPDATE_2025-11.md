# API 업데이트 문서 (2025년 11월)

## 📋 개요

이 문서는 2025년 11월에 진행된 API 업데이트 사항을 상세히 기술합니다.

### 주요 변경사항

1. **동행 요청 API 업데이트**
   - `scheduledAt` 필드 추가 (예약 일시)
   - `route` 필드 추가 (경로 정보)

2. **새로운 채팅방 API 추가**
   - 단일 채팅방 조회
   - 메시지 히스토리 조회
   - 참여 중 채팅방 목록 조회

---

## 1. 동행 요청 API 업데이트

### 1.1 POST /api/v1/companions - 동행 요청 생성

**변경 전 (Legacy):**
```json
{
  "title": "동행 요청",
  "startLatitude": 35.1595,
  "startLongitude": 126.8526,
  "destinationLatitude": 35.1756,
  "destinationLongitude": 126.9135,
  "startAddress": "광주광역시 북구 용봉동",
  "destinationAddress": "광주광역시 동구 금남로",
  "estimatedMinutes": 30
}
```

**변경 후 (Current):**
```json
{
  "title": "광주 금남로까지 동행 부탁드립니다",
  "description": "계단 3개 있습니다",
  "startLatitude": 35.1595,
  "startLongitude": 126.8526,
  "destinationLatitude": 35.1756,
  "destinationLongitude": 126.9135,
  "startAddress": "광주광역시 북구 용봉동",
  "destinationAddress": "광주광역시 동구 금남로",
  "estimatedMinutes": 30,
  "scheduledAt": "2025-12-01T15:00:00+09:00",
  "route": {
    "coord_type": "WGS84",
    "total_distance_meters": 2150,
    "total_duration_seconds": 900,
    "estimated_price": 3200,
    "points": [
      { "lat": 35.176123, "lng": 126.905432 },
      { "lat": 35.175900, "lng": 126.906100 },
      { "lat": 35.175500, "lng": 126.907000 }
    ]
  }
}
```

#### 신규 필드 상세

##### `scheduledAt` (필수)
- **타입**: `string` (ISO 8601 DateTime)
- **설명**: 동행 예약 일시
- **검증 규칙**:
  - ISO 8601 형식 (예: `2025-12-01T15:00:00+09:00`)
  - 현재 시간 이후여야 함
  - 타임존 정보 포함 권장
- **예시**:
  ```json
  "scheduledAt": "2025-12-01T15:00:00+09:00"
  ```

##### `route` (선택)
- **타입**: `object`
- **설명**: 경로 정보 (SK T map API 등에서 가져온 데이터)
- **하위 필드**:

  | 필드 | 타입 | 필수 | 설명 | 검증 규칙 |
  |------|------|------|------|-----------|
  | `coord_type` | `string` | ✅ | 좌표계 | "WGS84" 고정 |
  | `total_distance_meters` | `number` | ✅ | 총 거리 (미터) | 0보다 큰 정수 |
  | `total_duration_seconds` | `number` | ✅ | 예상 소요 시간 (초) | 0보다 큰 정수 |
  | `estimated_price` | `number` | ✅ | 예상 택시 요금 (원) | 0 이상의 정수 |
  | `points` | `array` | ✅ | 경로 좌표 배열 | 최소 2개 이상 |

- **points 배열 항목**:
  | 필드 | 타입 | 필수 | 설명 | 검증 규칙 |
  |------|------|------|------|-----------|
  | `lat` | `number` | ✅ | 위도 | -90 ~ 90 |
  | `lng` | `number` | ✅ | 경도 | -180 ~ 180 |

- **예시**:
  ```json
  "route": {
    "coord_type": "WGS84",
    "total_distance_meters": 2150,
    "total_duration_seconds": 900,
    "estimated_price": 3200,
    "points": [
      { "lat": 35.176123, "lng": 126.905432 },
      { "lat": 35.175900, "lng": 126.906100 },
      { "lat": 35.175500, "lng": 126.907000 }
    ]
  }
  ```

#### 응답 예시

**201 Created - 성공:**
```json
{
  "success": true,
  "data": {
    "id": 123,
    "title": "광주 금남로까지 동행 부탁드립니다",
    "description": "계단 3개 있습니다",
    "startLatitude": 35.1595,
    "startLongitude": 126.8526,
    "destinationLatitude": 35.1756,
    "destinationLongitude": 126.9135,
    "startAddress": "광주광역시 북구 용봉동",
    "destinationAddress": "광주광역시 동구 금남로",
    "estimatedMinutes": 30,
    "scheduledAt": "2025-12-01T15:00:00+09:00",
    "route": {
      "coord_type": "WGS84",
      "total_distance_meters": 2150,
      "total_duration_seconds": 900,
      "estimated_price": 3200,
      "points": [...]
    },
    "status": "pending",
    "createdAt": "2025-11-21T10:00:00Z"
  }
}
```

**400 Bad Request - 검증 실패:**
```json
{
  "success": false,
  "message": "예약 일시는 현재 시간 이후여야 합니다",
  "errors": [
    {
      "field": "scheduledAt",
      "message": "예약 일시는 현재 시간 이후여야 합니다"
    }
  ]
}
```

### 1.2 GET /api/v1/companions/:id - 단일 동행 요청 조회

**응답 변경사항:**
- `scheduledAt` 필드 추가
- `route` 필드 추가

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "id": 123,
    "title": "광주 금남로까지 동행 부탁드립니다",
    "scheduledAt": "2025-12-01T15:00:00+09:00",
    "route": {
      "coord_type": "WGS84",
      "total_distance_meters": 2150,
      "estimated_price": 3200,
      "points": [...]
    },
    // ... 기타 필드
  }
}
```

### 1.3 GET /api/v1/companions/nearby - 주변 요청 조회

**응답 변경사항:**
- 각 요청에 `scheduledAt` 필드 포함

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "requests": [
      {
        "id": 123,
        "scheduledAt": "2025-12-01T15:00:00+09:00",
        // ... 기타 필드
      }
    ]
  }
}
```

### 1.4 POST /api/v1/companions/:id/accept - 요청 수락

**응답 변경사항:**
- `chat_room_id` 필드 추가

**응답 예시:**
```json
{
  "success": true,
  "data": {
    "match_id": 456,
    "chat_room_id": 456,
    "status": "pending",
    "created_at": "2025-11-21T10:30:00Z"
  }
}
```

---

## 2. 새로운 채팅방 API

### 2.1 GET /api/v1/chat-rooms/:id - 단일 채팅방 조회

**엔드포인트:**
```
GET /api/v1/chat-rooms/:id
```

**Path Parameters:**
- `id` (required): 채팅방 ID (match_id와 동일)

**Headers:**
```
Authorization: Bearer {access_token}
```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": {
    "chat_room_id": 123,
    "match_id": 123,
    "request_id": 456,
    "partner": {
      "user_id": 45,
      "nickname": "김철수",
      "profile_image_url": "https://storage.googleapis.com/..."
    },
    "request": {
      "scheduled_at": "2025-12-01T15:00:00+09:00",
      "start_address": "광주광역시 북구 용봉동",
      "end_address": "광주광역시 동구 금남로"
    },
    "last_message": {
      "message_id": 789,
      "sender_id": 45,
      "message": "5분 후에 도착해요!",
      "created_at": "2025-11-21T10:30:00Z"
    },
    "unread_count": 3
  }
}
```

**응답 필드 설명:**

| 필드 | 타입 | 설명 |
|------|------|------|
| `chat_room_id` | `bigint` | 채팅방 ID (match_id와 동일) |
| `match_id` | `bigint` | 매칭 ID |
| `request_id` | `bigint` | 동행 요청 ID |
| `partner` | `object` | 상대방 정보 |
| `partner.user_id` | `bigint` | 상대방 사용자 ID |
| `partner.nickname` | `string` | 상대방 닉네임 |
| `partner.profile_image_url` | `string` | 상대방 프로필 이미지 URL |
| `request` | `object` | 동행 요청 정보 |
| `request.scheduled_at` | `string` | 예약 일시 |
| `request.start_address` | `string` | 출발지 주소 |
| `request.end_address` | `string` | 도착지 주소 |
| `last_message` | `object\|null` | 마지막 메시지 (없으면 null) |
| `last_message.message_id` | `bigint` | 메시지 ID |
| `last_message.sender_id` | `bigint` | 발신자 ID |
| `last_message.message` | `string` | 메시지 내용 |
| `last_message.created_at` | `string` | 생성 시간 |
| `unread_count` | `number` | 안 읽은 메시지 수 |

**에러 응답:**

**404 Not Found - 채팅방 없음:**
```json
{
  "success": false,
  "message": "채팅방을 찾을 수 없습니다"
}
```

**403 Forbidden - 권한 없음:**
```json
{
  "success": false,
  "message": "채팅방에 접근할 권한이 없습니다"
}
```

### 2.2 GET /api/v1/chat-rooms/:id/messages - 메시지 히스토리 조회

**엔드포인트:**
```
GET /api/v1/chat-rooms/:id/messages
```

**Path Parameters:**
- `id` (required): 채팅방 ID

**Query Parameters:**

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `before_id` | `bigint` | ❌ | - | 이 ID 이전의 메시지 조회 (페이징) |
| `limit` | `number` | ❌ | 50 | 가져올 메시지 수 (최대 100) |

**Headers:**
```
Authorization: Bearer {access_token}
```

**사용 예시:**

1. **첫 페이지 로드:**
   ```
   GET /api/v1/chat-rooms/123/messages?limit=20
   ```

2. **다음 페이지 로드:**
   ```
   GET /api/v1/chat-rooms/123/messages?before_id=98&limit=20
   ```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "message_id": 99,
      "sender_id": 45,
      "message": "도착했어요!",
      "created_at": "2025-11-21T10:29:00Z"
    },
    {
      "message_id": 98,
      "sender_id": 12,
      "message": "거의 다 왔습니다",
      "created_at": "2025-11-21T10:28:00Z"
    }
  ],
  "pagination": {
    "has_more": true,
    "next_before_id": 98
  }
}
```

**응답 필드 설명:**

| 필드 | 타입 | 설명 |
|------|------|------|
| `data` | `array` | 메시지 배열 (최신순 정렬) |
| `data[].message_id` | `bigint` | 메시지 ID |
| `data[].sender_id` | `bigint` | 발신자 ID |
| `data[].message` | `string` | 메시지 내용 |
| `data[].created_at` | `string` | 생성 시간 (ISO 8601) |
| `pagination.has_more` | `boolean` | 더 가져올 메시지가 있는지 여부 |
| `pagination.next_before_id` | `bigint\|null` | 다음 페이지 요청 시 사용할 before_id |

**페이징 동작 방식:**
1. 메시지는 `created_at DESC` (최신순)로 정렬됩니다
2. `before_id`가 없으면 가장 최신 메시지부터 `limit`개를 반환합니다
3. `before_id`가 있으면 해당 ID보다 작은 메시지를 `limit`개 반환합니다
4. `has_more`가 `true`면 더 가져올 메시지가 있습니다
5. `next_before_id`를 사용해 다음 페이지를 요청할 수 있습니다

**에러 응답:**

**404 Not Found - 채팅방 없음:**
```json
{
  "success": false,
  "message": "채팅방을 찾을 수 없습니다"
}
```

**403 Forbidden - 권한 없음:**
```json
{
  "success": false,
  "message": "채팅방에 접근할 권한이 없습니다"
}
```

### 2.3 GET /api/v1/chat-rooms - 참여 중 채팅방 목록 조회

**엔드포인트:**
```
GET /api/v1/chat-rooms
```

**Query Parameters:**

| 파라미터 | 타입 | 필수 | 기본값 | 설명 |
|---------|------|------|--------|------|
| `status` | `string` | ❌ | `all` | 필터링 옵션 (`active`, `completed`, `all`) |

**status 값:**
- `active`: 진행 중인 동행 (pending, in_progress 상태)
- `completed`: 완료된 동행
- `all`: 전체 채팅방

**Headers:**
```
Authorization: Bearer {access_token}
```

**사용 예시:**

1. **전체 채팅방:**
   ```
   GET /api/v1/chat-rooms
   ```

2. **진행 중인 채팅방만:**
   ```
   GET /api/v1/chat-rooms?status=active
   ```

3. **완료된 채팅방만:**
   ```
   GET /api/v1/chat-rooms?status=completed
   ```

**응답 (200 OK):**
```json
{
  "success": true,
  "data": [
    {
      "chat_room_id": 123,
      "match_id": 123,
      "request_id": 456,
      "partner": {
        "user_id": 45,
        "nickname": "김철수",
        "profile_image_url": "https://storage.googleapis.com/..."
      },
      "request": {
        "scheduled_at": "2025-12-01T15:00:00+09:00",
        "start_address": "광주광역시 북구 용봉동",
        "end_address": "광주광역시 동구 금남로"
      },
      "last_message": {
        "message_id": 789,
        "message": "5분 후에 도착해요!",
        "created_at": "2025-11-21T10:30:00Z"
      },
      "unread_count": 3
    },
    {
      "chat_room_id": 124,
      "match_id": 124,
      "request_id": 457,
      "partner": {
        "user_id": 46,
        "nickname": "이영희",
        "profile_image_url": "https://storage.googleapis.com/..."
      },
      "request": {
        "scheduled_at": "2025-12-02T10:00:00+09:00",
        "start_address": "서울특별시 강남구",
        "end_address": "서울특별시 송파구"
      },
      "last_message": null,
      "unread_count": 0
    }
  ]
}
```

**응답 특징:**
- 매칭 최신순으로 정렬 (`matchedAt DESC`)
- 각 채팅방은 GET /api/v1/chat-rooms/:id 응답과 동일한 구조
- `sender_id` 없이 메시지만 반환 (목록에서는 불필요)

---

## 3. 데이터베이스 변경사항

### 3.1 companion_requests 테이블

**추가된 컬럼:**

| 컬럼명 | 타입 | NULL | 기본값 | 설명 |
|--------|------|------|--------|------|
| `scheduled_at` | `DATETIME(3)` | ❌ | - | 예약 일시 |
| `route` | `JSON` | ✅ | `NULL` | 경로 정보 |

**인덱스 추가:**
```sql
CREATE INDEX idx_scheduled_at ON companion_requests(scheduled_at ASC);
```

**마이그레이션 참고:**
- 기존 데이터의 `scheduled_at`은 `requested_at` 값으로 초기화됨
- `route`는 NULL 허용

### 3.2 route JSON 스키마

```json
{
  "coord_type": "WGS84",
  "total_distance_meters": 2150,
  "total_duration_seconds": 900,
  "estimated_price": 3200,
  "points": [
    { "lat": 35.176123, "lng": 126.905432 },
    { "lat": 35.175900, "lng": 126.906100 }
  ]
}
```

---

## 4. 마이그레이션 가이드

### 4.1 클라이언트 측 변경사항

#### Android/iOS 앱 업데이트 필요 사항:

1. **동행 요청 생성 화면:**
   - 날짜/시간 선택 UI 추가 (`scheduledAt`)
   - 경로 정보 표시 (`route.points`를 지도에 표시)
   - 예상 택시 요금 표시 (`route.estimated_price`)

2. **채팅방 목록 화면:**
   - 새로운 API 엔드포인트 사용: `GET /api/v1/chat-rooms`
   - `status` 필터 추가

3. **채팅방 상세 화면:**
   - 새로운 API 엔드포인트 사용: `GET /api/v1/chat-rooms/:id`
   - 예약 일시 표시 (`request.scheduled_at`)

4. **메시지 히스토리:**
   - 새로운 API 엔드포인트 사용: `GET /api/v1/chat-rooms/:id/messages`
   - 커서 기반 페이징 구현 (`before_id`, `next_before_id`)

### 4.2 호환성

**하위 호환성:**
- ✅ `scheduledAt` 필드 추가는 새 요청에만 적용
- ✅ 기존 요청은 `requested_at` → `scheduled_at`로 자동 마이그레이션됨
- ✅ `route` 필드는 선택 사항 (NULL 허용)
- ⚠️ 채팅방 API는 새로운 엔드포인트이므로 클라이언트 업데이트 필요

**권장 마이그레이션 순서:**
1. 백엔드 배포 (하위 호환성 유지)
2. 클라이언트 업데이트 배포
3. 기존 클라이언트도 정상 작동 (선택 필드는 무시됨)

---

## 5. 테스트 가이드

### 5.1 단위 테스트

```bash
# Validator 테스트
npm test tests/unit/validators/companion.validator.test.ts

# 22개 테스트 케이스:
# - scheduledAt 검증 (필수, 미래 시간, ISO 형식)
# - route 검증 (coord_type, distance, price, points)
```

### 5.2 통합 테스트

```bash
# 동행 API 테스트
npm test tests/integration/companion.api.test.ts

# 채팅 API 테스트
npm test tests/integration/chat.api.test.ts

# 전체 테스트
npm test
```

### 5.3 수동 테스트 시나리오

#### 시나리오 1: 예약 동행 요청 생성

1. POST /api/v1/companions
   - scheduledAt: 내일 오후 3시
   - route 정보 포함

2. GET /api/v1/companions/:id
   - scheduledAt, route 포함 확인

3. POST /api/v1/companions/:id/accept
   - chat_room_id 반환 확인

#### 시나리오 2: 채팅방 조회

1. GET /api/v1/chat-rooms
   - status=active 필터 적용
   - scheduledAt 포함 확인

2. GET /api/v1/chat-rooms/:id
   - 상대방 정보, 요청 정보, 마지막 메시지 확인

3. GET /api/v1/chat-rooms/:id/messages
   - 첫 페이지 (limit=20)
   - 두 번째 페이지 (before_id 사용)

---

## 6. 참고 자료

### 6.1 관련 파일

**Backend:**
- `prisma/schema.prisma` - 데이터베이스 스키마
- `src/validators/companion.validator.ts` - 검증 로직
- `src/services/companion.service.ts` - 비즈니스 로직
- `src/services/chat.service.ts` - 채팅 서비스 (신규)
- `src/controllers/chat.controller.ts` - 채팅 컨트롤러 (신규)
- `src/routes/chat.routes.ts` - 채팅 라우트 (신규)

**Tests:**
- `tests/unit/validators/companion.validator.test.ts`
- `tests/integration/companion.api.test.ts`
- `tests/integration/chat.api.test.ts`

### 6.2 외부 API 참고

- **SK T map API**: 경로 탐색 (route 데이터 생성용)
- **카카오맵 API**: 지도 표시 (points 시각화)

---

## 7. FAQ

### Q1. `scheduledAt`이 필수인 이유는?
A1. 실시간 매칭에서 예약 기반 매칭으로 변경되었기 때문입니다. 사용자가 명확히 언제 동행이 필요한지 지정해야 합니다.

### Q2. `route` 정보는 어떻게 생성하나요?
A2. 클라이언트에서 SK T map API를 호출하여 경로 정보를 받아온 후, 해당 데이터를 변환하여 전송합니다.

### Q3. 기존 Socket.io 채팅과 새로운 Chat API의 관계는?
A3. Socket.io는 실시간 메시지 전송에 사용되고, Chat API는 메시지 히스토리 조회와 채팅방 관리에 사용됩니다.

### Q4. 채팅방 ID와 match_id가 동일한 이유는?
A4. 매칭이 성공하면 자동으로 채팅방이 생성되며, 1:1 대응 관계를 유지하기 위해 동일한 ID를 사용합니다.

### Q5. 페이징은 왜 offset이 아닌 cursor 방식을 사용하나요?
A5. 채팅 메시지는 실시간으로 추가되므로, offset 방식은 중복이나 누락이 발생할 수 있습니다. cursor(before_id) 방식이 더 안정적입니다.

---

## 8. 변경 이력

| 날짜 | 버전 | 변경사항 | 작성자 |
|------|------|----------|--------|
| 2025-11-21 | 1.0.0 | 초기 문서 작성 | Backend Team |

---


