# 동행 백엔드 배포 가이드

## 📋 목차
1. [배포 전 체크리스트](#배포-전-체크리스트)
2. [GCP 서버 접속 방법](#gcp-서버-접속-방법)
3. [배포 방법](#배포-방법)
4. [배포 후 확인](#배포-후-확인)
5. [롤백 방법](#롤백-방법)
6. [문제 해결](#문제-해결)

---

## 배포 전 체크리스트

배포하기 전에 다음 사항들을 확인하세요:

- [ ] 로컬에서 빌드 성공 (`npm run build`)
- [ ] 테스트 통과 (`npm test`)
- [ ] Git 커밋 및 푸시 완료
- [ ] 환경 변수 설정 확인 (`.env.production`)
- [ ] 데이터베이스 백업 완료 (중요!)

---

## GCP 서버 접속 방법

### 방법 1: Google Cloud Console (권장)

가장 간단한 방법입니다:

1. [Google Cloud Console](https://console.cloud.google.com/) 접속
2. 왼쪽 메뉴에서 **"Compute Engine"** → **"VM 인스턴스"** 클릭
3. 실행 중인 VM 인스턴스 찾기
4. 우측의 **"SSH"** 버튼 클릭
5. 브라우저에서 터미널이 열립니다

### 방법 2: gcloud CLI

로컬 터미널에서 실행:

```bash
# 1. gcloud CLI 설치 확인
gcloud --version

# 2. GCP 계정 로그인
gcloud auth login

# 3. 프로젝트 설정
gcloud config set project YOUR_PROJECT_ID

# 4. VM 인스턴스 목록 확인
gcloud compute instances list

# 5. SSH 접속
gcloud compute ssh INSTANCE_NAME --zone=ZONE
```

**예시:**
```bash
gcloud compute ssh companion-api --zone=asia-northeast3-a
```

### 방법 3: SSH 키 사용

```bash
ssh -i ~/.ssh/gcp_key username@YOUR_SERVER_IP
```

---

## 배포 방법

### 옵션 A: 자동 배포 스크립트 사용 (권장)

서버에 접속한 후:

```bash
# 1. 프로젝트 루트로 이동
cd ~/2025-KFPD-DongHaeng
# 또는
cd /home/your-username/2025-KFPD-DongHaeng

# 2. 배포 스크립트에 실행 권한 부여 (최초 1회만)
chmod +x deploy.sh

# 3. 배포 실행
./deploy.sh
```

배포 스크립트가 다음 작업을 자동으로 수행합니다:
1. ✅ Git Pull
2. ✅ npm install
3. ✅ Prisma Generate
4. ✅ Database Migration
5. ✅ TypeScript Build
6. ✅ Server Restart
7. ✅ Deployment Verification

### 옵션 B: 수동 배포

서버에 접속한 후 다음 명령어를 순서대로 실행:

```bash
# 1. 프로젝트 루트로 이동
cd ~/2025-KFPD-DongHaeng

# 2. 최신 코드 가져오기
git pull origin main

# 3. 백엔드 디렉토리로 이동
cd DongHaeng_backend

# 4. 의존성 설치
npm install

# 5. Prisma 클라이언트 생성
npm run prisma:generate

# 6. 데이터베이스 마이그레이션 실행
npm run prisma:migrate:prod

# 7. TypeScript 빌드
npm run build

# 8. 서버 재시작
# PM2 사용 시:
pm2 restart companion-api

# systemd 사용 시:
sudo systemctl restart companion-api

# Docker 사용 시:
docker-compose down
docker-compose up -d --build
```

---

## 배포 후 확인

### 1. 서버 상태 확인

```bash
# PM2 상태 확인
pm2 status

# PM2 로그 확인
pm2 logs companion-api

# 최근 로그 확인
pm2 logs companion-api --lines 50
```

### 2. Health Check

```bash
# 서버 Health Check
curl http://localhost:3000/health

# 또는 외부에서
curl https://your-domain.com/health
```

**예상 응답:**
```json
{
  "status": "ok",
  "timestamp": "2025-11-21T10:00:00Z",
  "version": "1.0.0"
}
```

### 3. API 테스트

```bash
# API 버전 확인
curl https://your-domain.com/api/v1

# 동행 요청 목록 조회 (인증 필요)
curl -H "Authorization: Bearer YOUR_TOKEN" \
  https://your-domain.com/api/v1/companions/nearby?latitude=35.1595&longitude=126.8526
```

### 4. 데이터베이스 확인

```bash
# Prisma Studio 실행 (로컬 포트 포워딩 필요)
npx prisma studio

# 또는 MySQL 직접 접속
mysql -u username -p -h database-host
```

### 5. 로그 모니터링

```bash
# 실시간 로그 확인
pm2 logs companion-api --lines 0

# 에러 로그만 확인
pm2 logs companion-api --err

# 로그 파일 확인
tail -f logs/error.log
tail -f logs/combined.log
```

---

## 롤백 방법

배포 후 문제가 발생하면 이전 버전으로 롤백할 수 있습니다:

```bash
# 1. 이전 커밋 확인
git log --oneline -n 10

# 2. 특정 커밋으로 롤백
git reset --hard COMMIT_HASH

# 3. 배포 스크립트 다시 실행
./deploy.sh

# 또는 PM2 재시작만
pm2 restart companion-api
```

**데이터베이스 롤백 (신중하게!):**
```bash
# 마이그레이션 되돌리기 (백업 필수!)
npx prisma migrate resolve --rolled-back MIGRATION_NAME

# 또는 데이터베이스 백업 복원
# (사전에 백업한 파일 사용)
```

---

## 문제 해결

### 문제 1: 빌드 실패

**증상:** `npm run build` 실패

**해결 방법:**
```bash
# node_modules 삭제 후 재설치
rm -rf node_modules
rm package-lock.json
npm install

# TypeScript 버전 확인
npx tsc --version

# 빌드 재시도
npm run build
```

### 문제 2: 마이그레이션 실패

**증상:** `prisma migrate deploy` 실패

**해결 방법:**
```bash
# Prisma 상태 확인
npx prisma migrate status

# Prisma 클라이언트 재생성
npx prisma generate

# 마이그레이션 재시도
npx prisma migrate deploy

# 또는 특정 마이그레이션만 해결
npx prisma migrate resolve --applied MIGRATION_NAME
```

### 문제 3: 서버 시작 실패

**증상:** PM2로 서버 시작 시 즉시 종료

**해결 방법:**
```bash
# 에러 로그 확인
pm2 logs companion-api --err

# 환경 변수 확인
cat .env.production

# 수동으로 서버 실행해서 에러 확인
NODE_ENV=production node dist/index.js

# 포트 충돌 확인
lsof -i :3000
```

### 문제 4: 데이터베이스 연결 실패

**증상:** "Can't connect to database" 에러

**해결 방법:**
```bash
# DATABASE_URL 확인
echo $DATABASE_URL

# MySQL 연결 테스트
mysql -u username -p -h database-host

# Cloud SQL Proxy 사용 시
./cloud_sql_proxy -instances=INSTANCE_CONNECTION_NAME=tcp:3306

# 방화벽 규칙 확인 (GCP Console)
```

### 문제 5: PM2 프로세스 없음

**증상:** `pm2 restart` 시 프로세스를 찾을 수 없음

**해결 방법:**
```bash
# PM2 프로세스 목록 확인
pm2 list

# 프로세스가 없으면 새로 시작
pm2 start dist/index.js --name companion-api

# PM2 저장 (재부팅 후에도 자동 시작)
pm2 save

# PM2 스타트업 스크립트 생성
pm2 startup
```

---

## 추가 리소스

### 유용한 PM2 명령어

```bash
# 프로세스 재시작
pm2 restart companion-api

# 프로세스 중지
pm2 stop companion-api

# 프로세스 삭제
pm2 delete companion-api

# 메모리 사용량 확인
pm2 monit

# 프로세스 정보 확인
pm2 info companion-api

# 로그 삭제
pm2 flush
```

### 데이터베이스 백업

```bash
# MySQL 백업 (배포 전 필수!)
mysqldump -u username -p database_name > backup_$(date +%Y%m%d_%H%M%S).sql

# GCP Cloud SQL 백업
gcloud sql backups create --instance=INSTANCE_NAME

# 백업 목록 확인
gcloud sql backups list --instance=INSTANCE_NAME
```

### 모니터링

```bash
# 서버 리소스 확인
htop
df -h
free -h

# 네트워크 확인
netstat -tuln | grep 3000

# 프로세스 확인
ps aux | grep node
```

---

## 배포 체크리스트 요약

### 배포 전
- [ ] 코드 리뷰 완료
- [ ] 테스트 통과
- [ ] Git 커밋 및 푸시
- [ ] 환경 변수 확인
- [ ] 데이터베이스 백업

### 배포 중
- [ ] 서버 접속
- [ ] Git Pull
- [ ] 의존성 설치
- [ ] 마이그레이션 실행
- [ ] 빌드 성공
- [ ] 서버 재시작

### 배포 후
- [ ] Health Check 성공
- [ ] API 테스트 성공
- [ ] 로그 확인 (에러 없음)
- [ ] PM2 상태 확인
- [ ] 데이터베이스 연결 확인
- [ ] 실시간 모니터링 (5-10분)

---

## 문의 및 지원

배포 중 문제가 발생하면:
1. 위 문제 해결 섹션 참고
2. PM2 로그 확인 (`pm2 logs`)
3. 팀원에게 문의
4. GitHub Issues에 보고

---

**마지막 업데이트:** 2025-11-21
**작성자:** Backend Team
