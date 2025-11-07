# ☁️ GCP 배포 완전 가이드

## 📋 목차

1. [사전 준비](#사전-준비)
2. [GCP 프로젝트 설정](#gcp-프로젝트-설정)
3. [Compute Engine 설정](#compute-engine-설정)
4. [Cloud SQL 설정](#cloud-sql-설정)
5. [Memorystore Redis 설정](#memorystore-redis-설정)
6. [Cloud Storage 설정](#cloud-storage-설정)
7. [Firebase 설정](#firebase-설정)
8. [배포](#배포)
9. [SSL/HTTPS 설정](#sslhttps-설정)
10. [모니터링](#모니터링)

---

## 사전 준비

### 필수 요구사항

- [ ] Google 계정
- [ ] GCP 크레딧 (42만원 확인됨 ✅)
- [ ] 로컬에 gcloud CLI 설치
- [ ] 도메인 (선택사항, SSL 사용 시)

### gcloud CLI 설치

#### Windows

```powershell
# PowerShell에서 실행
(New-Object Net.WebClient).DownloadFile("https://dl.google.com/dl/cloudsdk/channels/rapid/GoogleCloudSDKInstaller.exe", "$env:Temp\GoogleCloudSDKInstaller.exe")
& $env:Temp\GoogleCloudSDKInstaller.exe
```

#### Linux/macOS

```bash
curl https://sdk.cloud.google.com | bash
exec -l $SHELL
```

### 초기 인증

```bash
# GCP 로그인
gcloud auth login

# 프로젝트 설정
gcloud config set project YOUR_PROJECT_ID
```

---

## GCP 프로젝트 설정

### 1. 프로젝트 생성 (또는 기존 프로젝트 사용)

```bash
# 프로젝트 목록 확인
gcloud projects list

# 새 프로젝트 생성 (필요 시)
gcloud projects create dongheng-project --name="DongHaeng App"

# 프로젝트 설정
gcloud config set project dongheng-project
```

### 2. 결제 계정 연결

GCP Console > Billing > Link Billing Account
또는:

```bash
# 결제 계정 목록
gcloud beta billing accounts list

# 프로젝트에 연결
gcloud beta billing projects link dongheng-project \
  --billing-account=BILLING_ACCOUNT_ID
```

### 3. 필수 API 활성화

```bash
# Compute Engine API
gcloud services enable compute.googleapis.com

# Cloud SQL Admin API
gcloud services enable sqladmin.googleapis.com

# Redis API (Memorystore)
gcloud services enable redis.googleapis.com

# Cloud Storage API
gcloud services enable storage-api.googleapis.com

# Artifact Registry API
gcloud services enable artifactregistry.googleapis.com

# Cloud Build API (CI/CD용)
gcloud services enable cloudbuild.googleapis.com

# 한 번에 활성화
gcloud services enable \
  compute.googleapis.com \
  sqladmin.googleapis.com \
  redis.googleapis.com \
  storage-api.googleapis.com \
  artifactregistry.googleapis.com \
  cloudbuild.googleapis.com
```

---

## Compute Engine 설정

### 1. VM 인스턴스 생성

#### 무료 티어 (e2-micro)

```bash
gcloud compute instances create dongheng-server \
  --zone=asia-northeast3-a \
  --machine-type=e2-micro \
  --image-family=ubuntu-2204-lts \
  --image-project=ubuntu-os-cloud \
  --boot-disk-size=30GB \
  --boot-disk-type=pd-standard \
  --tags=http-server,https-server \
  --metadata=startup-script='#!/bin/bash
    apt-get update
    apt-get install -y docker.io git
    systemctl start docker
    systemctl enable docker
    usermod -aG docker $USER
  '
```

#### 프로덕션용 (e2-medium)

```bash
gcloud compute instances create dongheng-server \
  --zone=asia-northeast3-a \
  --machine-type=e2-medium \
  --image-family=ubuntu-2204-lts \
  --image-project=ubuntu-os-cloud \
  --boot-disk-size=50GB \
  --boot-disk-type=pd-ssd \
  --tags=http-server,https-server
```

### 2. 방화벽 규칙 설정

```bash
# HTTP (80)
gcloud compute firewall-rules create allow-http \
  --allow tcp:80 \
  --target-tags http-server \
  --description="Allow HTTP traffic"

# HTTPS (443)
gcloud compute firewall-rules create allow-https \
  --allow tcp:443 \
  --target-tags https-server \
  --description="Allow HTTPS traffic"

# 애플리케이션 (3000) - 선택사항
gcloud compute firewall-rules create allow-app \
  --allow tcp:3000 \
  --target-tags http-server \
  --description="Allow app traffic"
```

### 3. VM 접속

```bash
# SSH 접속
gcloud compute ssh dongheng-server --zone=asia-northeast3-a

# 또는 웹 SSH 사용
# GCP Console > Compute Engine > VM instances > SSH 버튼
```

---

## Cloud SQL 설정

### 옵션 1: 무료 티어 (db-f1-micro)

```bash
# Cloud SQL 인스턴스 생성
gcloud sql instances create dongheng-db \
  --database-version=MYSQL_8_0 \
  --tier=db-f1-micro \
  --region=asia-northeast3 \
  --root-password=CHANGE_THIS_PASSWORD \
  --no-assign-ip \
  --network=default

# 데이터베이스 생성
gcloud sql databases create dongheng_db \
  --instance=dongheng-db \
  --charset=utf8mb4 \
  --collation=utf8mb4_unicode_ci

# 사용자 생성
gcloud sql users create dongheng \
  --instance=dongheng-db \
  --password=CHANGE_THIS_PASSWORD
```

### 옵션 2: 프로덕션 (db-n1-standard-1)

```bash
gcloud sql instances create dongheng-db \
  --database-version=MYSQL_8_0 \
  --tier=db-n1-standard-1 \
  --region=asia-northeast3 \
  --root-password=STRONG_PASSWORD \
  --backup \
  --backup-start-time=03:00 \
  --maintenance-window-day=SUN \
  --maintenance-window-hour=4 \
  --no-assign-ip \
  --network=default
```

### Private IP 연결 (권장)

```bash
# VPC 피어링 설정
gcloud compute addresses create google-managed-services-default \
  --global \
  --purpose=VPC_PEERING \
  --prefix-length=16 \
  --network=default

gcloud services vpc-peerings connect \
  --service=servicenetworking.googleapis.com \
  --ranges=google-managed-services-default \
  --network=default

# Cloud SQL 인스턴스에 Private IP 할당
gcloud sql instances patch dongheng-db \
  --network=default \
  --no-assign-ip
```

### 연결 정보 확인

```bash
# Private IP 확인
gcloud sql instances describe dongheng-db --format="value(ipAddresses[0].ipAddress)"

# 연결 문자열
mysql://dongheng:PASSWORD@PRIVATE_IP:3306/dongheng_db
```

---

## Memorystore Redis 설정

### 무료 티어 대안: Upstash (권장)

Memorystore는 최소 $25/월이므로 Upstash 무료 티어 사용:

1. https://upstash.com 가입
2. Redis 데이터베이스 생성 (서울 리전 선택)
3. 연결 정보 복사

### Memorystore 사용 (크레딧 있을 때)

```bash
# Redis 인스턴스 생성 (1GB)
gcloud redis instances create dongheng-redis \
  --size=1 \
  --region=asia-northeast3 \
  --redis-version=redis_7_0

# 연결 정보 확인
gcloud redis instances describe dongheng-redis \
  --region=asia-northeast3 \
  --format="value(host,port)"
```

---

## Cloud Storage 설정

### 버킷 생성

```bash
# Storage 버킷 생성
gsutil mb -c STANDARD -l asia-northeast3 gs://dongheng-storage/

# 공개 접근 차단 (보안)
gsutil iam ch allUsers:objectViewer gs://dongheng-storage/

# CORS 설정 (프론트엔드 접근용)
cat > cors.json <<EOF
[
  {
    "origin": ["https://yourdomain.com"],
    "method": ["GET", "POST", "PUT", "DELETE"],
    "responseHeader": ["Content-Type"],
    "maxAgeSeconds": 3600
  }
]
EOF

gsutil cors set cors.json gs://dongheng-storage/
```

### 서비스 계정 설정

```bash
# 서비스 계정 생성
gcloud iam service-accounts create dongheng-storage \
  --display-name="DongHaeng Storage Access"

# Storage 권한 부여
gsutil iam ch serviceAccount:dongheng-storage@PROJECT_ID.iam.gserviceaccount.com:objectAdmin \
  gs://dongheng-storage/

# 키 생성
gcloud iam service-accounts keys create storage-key.json \
  --iam-account=dongheng-storage@PROJECT_ID.iam.gserviceaccount.com
```

---

## Firebase 설정

### 1. Firebase 프로젝트 생성

1. https://console.firebase.google.com 접속
2. "프로젝트 추가"
3. 기존 GCP 프로젝트 선택 (dongheng-project)

### 2. FCM 활성화

1. 프로젝트 설정 > Cloud Messaging 탭
2. Cloud Messaging API 활성화

### 3. 서비스 계정 키 생성

```bash
# Firebase Admin SDK용 서비스 계정 키
# Firebase Console > 프로젝트 설정 > 서비스 계정 > 새 비공개 키 생성

# 또는 gcloud로
gcloud iam service-accounts keys create firebase-key.json \
  --iam-account=firebase-adminsdk-xxxxx@PROJECT_ID.iam.gserviceaccount.com
```

### 4. Android/iOS 앱 추가

Firebase Console에서:
- Android 앱 추가 (패키지명: com.kfpd_donghaeng_fe)
- google-services.json 다운로드
- iOS 앱 추가 (필요 시)

---

## 배포

### 1. VM 초기 설정

```bash
# VM 접속
gcloud compute ssh dongheng-server --zone=asia-northeast3-a

# Docker 설치 (startup-script로 이미 설치됨)
sudo apt-get update
sudo apt-get install -y docker.io git

# Docker Compose 설치
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
  -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# gcloud 인증
gcloud auth configure-docker asia-northeast3-docker.pkg.dev
```

### 2. 코드 배포

```bash
# 저장소 클론
git clone https://github.com/yourusername/2025-KFPD-DongHaeng.git
cd 2025-KFPD-DongHaeng/DongHaeng_backend

# 환경변수 설정
nano .env.production
# (DATABASE_URL, REDIS_HOST 등 입력)

chmod 600 .env.production
```

### 3. Docker로 실행

```bash
# 빌드 & 실행
docker-compose --env-file .env.production up -d

# 로그 확인
docker-compose logs -f

# 헬스체크
curl http://localhost:3000/health
```

### 4. PM2로 실행 (대안)

```bash
# Node.js & PM2 설치
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs
sudo npm install -g pm2

# 앱 빌드
npm install
npm run build
npm run prisma:generate
npm run prisma:migrate:prod

# PM2로 시작
pm2 start ecosystem.config.js --env production
pm2 save
pm2 startup
```

---

## SSL/HTTPS 설정

### Let's Encrypt + Certbot

```bash
# Certbot 설치
sudo apt-get install -y certbot python3-certbot-nginx

# SSL 인증서 발급
sudo certbot --nginx -d api.yourdomain.com

# 자동 갱신 설정 (cron)
sudo crontab -e
# 추가: 0 0 * * * certbot renew --quiet
```

### Nginx 설정

```bash
# Nginx 설치
sudo apt-get install -y nginx

# 설정 파일 복사
sudo cp docker/nginx/nginx.conf /etc/nginx/nginx.conf

# Nginx 재시작
sudo systemctl restart nginx
sudo systemctl enable nginx
```

---

## 모니터링

### Cloud Monitoring

```bash
# Monitoring Agent 설치 (VM에서)
curl -sSO https://dl.google.com/cloudagents/add-google-cloud-ops-agent-repo.sh
sudo bash add-google-cloud-ops-agent-repo.sh --also-install
```

### GCP Console에서 확인

1. Monitoring > Dashboards
2. VM 인스턴스 메트릭 확인
   - CPU 사용률
   - 메모리 사용률
   - 네트워크 트래픽

### 알림 설정

```bash
# 알림 정책 생성 (예: CPU 80% 이상)
gcloud alpha monitoring policies create \
  --notification-channels=CHANNEL_ID \
  --display-name="High CPU Usage" \
  --condition-display-name="CPU > 80%" \
  --condition-threshold-value=0.8 \
  --condition-threshold-duration=300s
```

---

## 💰 비용 최적화

### 무료 티어 활용

```
✅ Compute Engine e2-micro (무료)
✅ Cloud SQL db-f1-micro (무료)
❌ Memorystore → Upstash 무료로 대체
✅ Cloud Storage 5GB (무료)
✅ Firebase FCM (무료)
```

### 예상 월 비용

```
e2-micro VM: $0 (무료 티어)
Cloud SQL db-f1-micro: $0 (무료 티어)
Upstash Redis: $0 (무료 10K req/day)
Cloud Storage 5GB: $0 (무료)
External IP: $0 (첫 번째 IP 무료)
─────────────────────────
총 월 비용: $0
```

크레딧 42만원은 나중에 스케일링 시 사용!

---

## 🔄 업데이트 & 유지보수

### 코드 업데이트

```bash
# VM 접속
gcloud compute ssh dongheng-server --zone=asia-northeast3-a

# 코드 업데이트
cd ~/2025-KFPD-DongHaeng/DongHaeng_backend
git pull origin main

# Docker
docker-compose down
docker-compose up --build -d

# 또는 PM2
npm run build
pm2 reload ecosystem.config.js
```

### DB 마이그레이션

```bash
npm run prisma:migrate:prod
```

### 백업

```bash
# Cloud SQL 자동 백업 활성화
gcloud sql instances patch dongheng-db \
  --backup-start-time=03:00 \
  --enable-bin-log

# 수동 백업
gcloud sql backups create --instance=dongheng-db
```

---

## 🎯 체크리스트

### 배포 전
- [ ] GCP 프로젝트 생성
- [ ] API 활성화
- [ ] VM 인스턴스 생성
- [ ] 방화벽 규칙 설정
- [ ] Cloud SQL 생성
- [ ] Redis 설정 (Upstash 또는 Memorystore)
- [ ] Cloud Storage 버킷 생성
- [ ] Firebase 프로젝트 연동

### 배포 중
- [ ] VM에 Docker 설치
- [ ] 코드 클론
- [ ] 환경변수 설정
- [ ] 데이터베이스 마이그레이션
- [ ] 앱 실행 (Docker 또는 PM2)
- [ ] 헬스체크 확인

### 배포 후
- [ ] SSL 인증서 설정
- [ ] Nginx 리버스 프록시 설정
- [ ] 모니터링 설정
- [ ] 알림 설정
- [ ] 백업 설정
- [ ] 도메인 연결

---

## 📚 참고 자료

- [GCP 무료 티어](https://cloud.google.com/free)
- [Compute Engine 문서](https://cloud.google.com/compute/docs)
- [Cloud SQL 문서](https://cloud.google.com/sql/docs)
- [Firebase 문서](https://firebase.google.com/docs)

---

**작성일:** 2025-11-07
**버전:** 1.0
**예상 소요 시간:** 2-3시간
