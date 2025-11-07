# 🐳 Docker 사용 가이드

## 📋 목차

1. [로컬 개발 환경](#로컬-개발-환경)
2. [프로덕션 빌드](#프로덕션-빌드)
3. [GCP 배포](#gcp-배포)
4. [문제 해결](#문제-해결)

---

## 로컬 개발 환경

### 1️⃣ 개발용 DB만 띄우기 (추천)

로컬에서 코드 개발 시 MySQL, Redis만 Docker로 실행:

```bash
# MySQL + Redis 시작
docker-compose -f docker-compose.dev.yml up -d

# 확인
docker ps

# 로그 확인
docker-compose -f docker-compose.dev.yml logs -f

# 종료
docker-compose -f docker-compose.dev.yml down
```

그리고 별도 터미널에서 Node.js 앱 실행:

```bash
npm run dev
```

**장점:**
- 코드 수정 시 즉시 반영 (hot reload)
- 디버깅 용이
- DB만 Docker로 격리

---

### 2️⃣ 전체 스택 Docker로 실행

앱까지 전부 Docker로 실행:

```bash
# 환경변수 설정
cp .env.docker .env

# 빌드 & 실행
docker-compose up --build

# 백그라운드 실행
docker-compose up -d

# 로그 확인
docker-compose logs -f app

# 종료
docker-compose down

# 볼륨까지 삭제 (데이터 초기화)
docker-compose down -v
```

---

### 3️⃣ Nginx 포함 실행

Nginx 리버스 프록시까지 포함:

```bash
docker-compose --profile with-nginx up -d
```

접속:
- 앱: http://localhost:3000
- Nginx: http://localhost (포트 80)

---

## 프로덕션 빌드

### 이미지 빌드

```bash
# 이미지 빌드
docker build -t dongheng-backend:latest .

# 빌드 확인
docker images | grep dongheng

# 이미지 실행 테스트
docker run -p 3000:3000 \
  -e DATABASE_URL="mysql://user:pass@host:3306/db" \
  -e REDIS_HOST="redis-host" \
  -e JWT_SECRET="secret" \
  dongheng-backend:latest
```

---

### Docker Hub 푸시 (선택)

```bash
# 로그인
docker login

# 태그
docker tag dongheng-backend:latest yourusername/dongheng-backend:latest

# 푸시
docker push yourusername/dongheng-backend:latest
```

---

## GCP 배포

### 방법 1: Docker Compose 사용

GCP VM에서 직접 실행:

```bash
# 1. GCP VM 접속
gcloud compute ssh dongheng-server

# 2. 저장소 클론
git clone https://github.com/yourusername/companion-backend.git
cd companion-backend

# 3. 환경변수 설정
nano .env.docker
# DATABASE_URL, JWT_SECRET 등 프로덕션 값 입력

# 4. 실행
docker-compose up -d

# 5. 확인
docker-compose ps
curl http://localhost:3000/health
```

---

### 방법 2: Google Container Registry (GCR)

```bash
# 1. GCR 활성화
gcloud services enable containerregistry.googleapis.com

# 2. Docker 인증
gcloud auth configure-docker

# 3. 이미지 빌드 & 푸시
docker build -t gcr.io/YOUR_PROJECT_ID/dongheng-backend:latest .
docker push gcr.io/YOUR_PROJECT_ID/dongheng-backend:latest

# 4. GCP VM에서 실행
gcloud compute ssh dongheng-server
docker pull gcr.io/YOUR_PROJECT_ID/dongheng-backend:latest
docker run -d -p 3000:3000 \
  --env-file .env.production \
  gcr.io/YOUR_PROJECT_ID/dongheng-backend:latest
```

---

### 방법 3: Cloud Run (서버리스)

```bash
# Cloud Run 배포
gcloud run deploy dongheng-backend \
  --image gcr.io/YOUR_PROJECT_ID/dongheng-backend:latest \
  --platform managed \
  --region asia-northeast3 \
  --allow-unauthenticated \
  --set-env-vars "DATABASE_URL=...,JWT_SECRET=..."
```

---

## 문제 해결

### 🔧 일반적인 문제

#### 1. 포트 충돌
```bash
# 이미 사용 중인 포트 확인
netstat -ano | findstr :3306
netstat -ano | findstr :6379

# 프로세스 종료 (Windows)
taskkill /PID <PID> /F
```

#### 2. 볼륨 권한 문제
```bash
# 볼륨 재생성
docker-compose down -v
docker-compose up -d
```

#### 3. 빌드 캐시 문제
```bash
# 캐시 없이 재빌드
docker-compose build --no-cache
docker-compose up
```

#### 4. 컨테이너 로그 확인
```bash
# 전체 로그
docker-compose logs

# 특정 서비스 로그
docker-compose logs app
docker-compose logs mysql

# 실시간 로그
docker-compose logs -f app
```

#### 5. 컨테이너 내부 접속
```bash
# 앱 컨테이너
docker exec -it dongheng-backend sh

# MySQL 컨테이너
docker exec -it dongheng-mysql mysql -u root -p
```

---

### 🗄️ 데이터베이스 관련

#### Prisma 마이그레이션

```bash
# 컨테이너 내부에서 실행
docker exec -it dongheng-backend npm run prisma:migrate:prod

# 또는 로컬에서 실행 (DATABASE_URL 설정 필요)
npm run prisma:migrate:prod
```

#### MySQL 백업

```bash
# 백업
docker exec dongheng-mysql mysqldump -u root -p dongheng_db > backup.sql

# 복원
docker exec -i dongheng-mysql mysql -u root -p dongheng_db < backup.sql
```

---

### 📊 모니터링

#### 리소스 사용량

```bash
# 컨테이너 리소스 확인
docker stats

# 특정 컨테이너만
docker stats dongheng-backend
```

#### 헬스체크

```bash
# 헬스체크 상태
docker inspect --format='{{json .State.Health}}' dongheng-backend | jq

# 앱 헬스체크
curl http://localhost:3000/health
```

---

## 유용한 명령어 모음

```bash
# 모든 컨테이너 중지
docker stop $(docker ps -aq)

# 사용하지 않는 이미지 삭제
docker image prune -a

# 전체 정리 (주의!)
docker system prune -a --volumes

# 네트워크 확인
docker network ls
docker network inspect dongheng-network

# 볼륨 확인
docker volume ls
docker volume inspect dongheng_mysql_data
```

---

## 환경별 설정

### 개발 환경
- `docker-compose.dev.yml` 사용
- 소스코드 볼륨 마운트
- 핫 리로드 활성화

### 프로덕션 환경
- `docker-compose.yml` 사용
- 빌드된 이미지 사용
- 헬스체크 활성화
- 로그 로테이션 설정

---

## 다음 단계

✅ Docker 설정 완료
⬜ GitHub Actions CI/CD 설정
⬜ GCP 인프라 구축
⬜ 모니터링 & 로깅 설정

---

**작성일:** 2025-11-07
**버전:** 1.0
