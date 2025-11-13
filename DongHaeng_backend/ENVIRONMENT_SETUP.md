# 🔐 환경변수 설정 가이드

## 📋 목차

1. [로컬 개발](#로컬-개발)
2. [프로덕션 설정](#프로덕션-설정)
3. [GCP Secret Manager 사용](#gcp-secret-manager-사용)
4. [보안 Best Practices](#보안-best-practices)

---

## 로컬 개발

### 1. 환경변수 파일 생성

```bash
# .env.example 복사
cp .env.example .env

# 또는 수동 생성
touch .env
```

### 2. 필수 값 설정

```env
NODE_ENV=development
PORT=3000
DATABASE_URL="mysql://root:password@localhost:3306/dongheng_db"
REDIS_HOST=localhost
REDIS_PORT=6379
JWT_SECRET=dev-secret-key
JWT_REFRESH_SECRET=dev-refresh-secret
```

### 3. 확인

```bash
# 환경변수 로드 테스트
npm run dev
```

---

## 프로덕션 설정

### 방법 1: 직접 .env 파일 사용

GCP VM에서:

```bash
# 1. 프로덕션 환경변수 파일 생성
nano .env.production

# 2. 값 입력 (아래 가이드 참고)

# 3. 파일 권한 설정 (보안)
chmod 600 .env.production

# 4. 환경변수 로드하여 앱 실행
export $(cat .env.production | xargs) && npm start
```

---

### 방법 2: systemd 서비스 사용

`/etc/systemd/system/dongheng.service`:

```ini
[Unit]
Description=Dongheng Backend Server
After=network.target

[Service]
Type=simple
User=dongheng
WorkingDirectory=/home/dongheng/app
EnvironmentFile=/home/dongheng/app/.env.production
ExecStart=/usr/bin/node /home/dongheng/app/dist/index.js
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
```

실행:

```bash
sudo systemctl daemon-reload
sudo systemctl enable dongheng
sudo systemctl start dongheng
sudo systemctl status dongheng
```

---

### 방법 3: PM2 Ecosystem 파일

`ecosystem.config.js`:

```javascript
module.exports = {
  apps: [{
    name: 'dongheng-backend',
    script: './dist/index.js',
    instances: 2,
    exec_mode: 'cluster',
    env_production: {
      NODE_ENV: 'production',
      PORT: 3000,
      // 환경변수를 여기에 정의하거나
      // .env.production 파일 사용
    },
    env_file: '.env.production'
  }]
}
```

실행:

```bash
pm2 start ecosystem.config.js --env production
pm2 save
pm2 startup
```

---

## GCP Secret Manager 사용

### 1. Secret Manager 활성화

```bash
# API 활성화
gcloud services enable secretmanager.googleapis.com

# Secret 생성
echo -n "your-jwt-secret" | gcloud secrets create jwt-secret --data-file=-
echo -n "your-db-password" | gcloud secrets create db-password --data-file=-
```

### 2. Secret 접근 권한 설정

```bash
# Compute Engine 서비스 계정에 권한 부여
gcloud secrets add-iam-policy-binding jwt-secret \
  --member="serviceAccount:YOUR_SERVICE_ACCOUNT@developer.gserviceaccount.com" \
  --role="roles/secretmanager.secretAccessor"
```

### 3. 애플리케이션에서 사용

코드 예시 (`src/config/secrets.ts`):

```typescript
import { SecretManagerServiceClient } from '@google-cloud/secret-manager';

const client = new SecretManagerServiceClient();

export async function getSecret(secretName: string): Promise<string> {
  const name = `projects/${process.env.GCP_PROJECT_ID}/secrets/${secretName}/versions/latest`;
  const [version] = await client.accessSecretVersion({ name });
  return version.payload?.data?.toString() || '';
}

// 사용
const jwtSecret = await getSecret('jwt-secret');
```

### 4. 시작 스크립트에서 환경변수 로드

`scripts/load-secrets.sh`:

```bash
#!/bin/bash

export JWT_SECRET=$(gcloud secrets versions access latest --secret="jwt-secret")
export DB_PASSWORD=$(gcloud secrets versions access latest --secret="db-password")

# 앱 실행
node dist/index.js
```

---

## 보안 Best Practices

### ✅ 해야 할 것

1. **강력한 비밀키 생성**
```bash
# JWT Secret 생성 (64자)
openssl rand -base64 64

# UUID 사용
node -e "console.log(require('crypto').randomUUID())"
```

2. **파일 권한 설정**
```bash
chmod 600 .env.production
chown app-user:app-user .env.production
```

3. **Git에서 제외**
```bash
# .gitignore에 추가
.env
.env.production
.env.local
.env.*.local
```

4. **환경별 분리**
- 개발: `.env`
- 스테이징: `.env.staging`
- 프로덕션: `.env.production`

5. **정기적 로테이션**
- JWT Secret: 3개월마다
- DB Password: 6개월마다
- API Key: 필요 시

---

### ❌ 하지 말아야 할 것

1. 환경변수를 코드에 하드코딩
2. .env 파일을 Git에 커밋
3. 환경변수를 로그에 출력
4. 약한 비밀키 사용 (예: "password", "secret")
5. 같은 비밀키를 개발/프로덕션에서 공유

---

## 주요 환경변수 설명

### DATABASE_URL

**형식:**
```
mysql://USER:PASSWORD@HOST:PORT/DATABASE?OPTIONS
```

**예시:**
```bash
# 로컬
DATABASE_URL="mysql://root:password@localhost:3306/dongheng_db"

# Cloud SQL (Public IP)
DATABASE_URL="mysql://dongheng:pass@35.123.456.789:3306/dongheng_db"

# Cloud SQL (Unix Socket)
DATABASE_URL="mysql://dongheng:pass@localhost/dongheng_db?socket=/cloudsql/project:region:instance"
```

**옵션:**
- `connection_limit`: 최대 연결 수 (기본: 10)
- `pool_timeout`: 타임아웃 (초)
- `charset`: utf8mb4

---

### JWT_SECRET

**생성:**
```bash
openssl rand -base64 64
```

**요구사항:**
- 최소 32자 이상
- 영문, 숫자, 특수문자 조합
- 프로덕션/개발 환경 분리

---

### REDIS_HOST

**Cloud Memorystore:**
- Private IP만 지원 (예: 10.0.0.3)
- VPC 내부에서만 접근 가능
- 비밀번호 없음 (기본 설정)

**로컬:**
- localhost 또는 127.0.0.1

---

### FIREBASE_PRIVATE_KEY

**형식:**
```json
{
  "type": "service_account",
  "project_id": "your-project",
  "private_key": "-----BEGIN PRIVATE KEY-----\n...\n-----END PRIVATE KEY-----\n",
  "client_email": "firebase-adminsdk@your-project.iam.gserviceaccount.com"
}
```

**환경변수로 변환:**
```bash
# JSON 파일 다운로드 후
export FIREBASE_PRIVATE_KEY=$(cat firebase-key.json | jq -r .private_key)
export FIREBASE_CLIENT_EMAIL=$(cat firebase-key.json | jq -r .client_email)
```

---

## Cloud SQL 연결 옵션

### 옵션 1: Public IP (간단)

```env
DATABASE_URL="mysql://user:pass@35.123.456.789:3306/db"
```

**장점:** 간단함
**단점:** 보안 취약, 추가 비용

---

### 옵션 2: Cloud SQL Proxy (권장)

```bash
# Cloud SQL Proxy 다운로드
wget https://dl.google.com/cloudsql/cloud_sql_proxy.linux.amd64 -O cloud_sql_proxy
chmod +x cloud_sql_proxy

# 실행
./cloud_sql_proxy -instances=PROJECT:REGION:INSTANCE=tcp:3306 &
```

```env
DATABASE_URL="mysql://user:pass@127.0.0.1:3306/db"
```

**장점:** 안전, 암호화
**단점:** 추가 프로세스 필요

---

### 옵션 3: Unix Socket (최고 성능)

```env
DATABASE_URL="mysql://user:pass@localhost/db?socket=/cloudsql/PROJECT:REGION:INSTANCE"
```

**장점:** 빠름, 안전
**단점:** GCP 내부만 사용 가능

---

## 환경변수 검증

코드에서 필수 환경변수 확인:

```typescript
// src/config/env.ts
const requiredEnvVars = [
  'DATABASE_URL',
  'JWT_SECRET',
  'REDIS_HOST'
];

requiredEnvVars.forEach((key) => {
  if (!process.env[key]) {
    throw new Error(`Missing required environment variable: ${key}`);
  }
});

export const config = {
  database: {
    url: process.env.DATABASE_URL!,
  },
  jwt: {
    secret: process.env.JWT_SECRET!,
    expiresIn: process.env.JWT_EXPIRES_IN || '30d',
  },
  redis: {
    host: process.env.REDIS_HOST!,
    port: parseInt(process.env.REDIS_PORT || '6379'),
  },
};
```

---

## 체크리스트

### 개발 환경
- [ ] `.env` 파일 생성
- [ ] 로컬 MySQL/Redis 연결 확인
- [ ] JWT Secret 설정
- [ ] 앱 정상 구동 확인

### 프로덕션 환경
- [ ] `.env.production` 생성
- [ ] 강력한 비밀키 생성
- [ ] Cloud SQL 연결 확인
- [ ] Memorystore Redis 연결 확인
- [ ] Firebase 서비스 계정 설정
- [ ] 파일 권한 설정 (600)
- [ ] Git ignore 확인
- [ ] Secret Manager 사용 (선택)

---

**작성일:** 2025-11-07
**버전:** 1.0
