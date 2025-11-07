# 🚀 CI/CD 설정 가이드

## 📋 개요

이 프로젝트는 GitHub Actions를 사용하여 자동화된 CI/CD 파이프라인을 구축합니다.

### 워크플로우 구성

1. **CI (ci.yml)** - Pull Request & develop 푸시 시
   - 린트 검사
   - 테스트 실행
   - Docker 빌드 테스트

2. **CD (cd.yml)** - main 브랜치 푸시 시
   - GCP Container Registry에 이미지 푸시
   - GCP Compute Engine에 배포
   - 헬스체크
   - 실패 시 롤백

3. **Docker Build (docker-build.yml)** - 릴리스 발행 시
   - 버전 태그와 함께 이미지 빌드

---

## 🔧 GitHub Secrets 설정

### 필수 Secrets

GitHub 저장소 설정에서 다음 Secrets를 추가하세요:
**Settings > Secrets and variables > Actions > New repository secret**

#### 1. GCP_SA_KEY

**GCP 서비스 계정 키 (JSON)**

생성 방법:

```bash
# 1. 서비스 계정 생성
gcloud iam service-accounts create github-actions \
  --display-name="GitHub Actions Deployer"

# 2. 권한 부여
gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/compute.admin"

gcloud projects add-iam-policy-binding YOUR_PROJECT_ID \
  --member="serviceAccount:github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com" \
  --role="roles/artifactregistry.writer"

# 3. 키 생성
gcloud iam service-accounts keys create github-actions-key.json \
  --iam-account=github-actions@YOUR_PROJECT_ID.iam.gserviceaccount.com

# 4. JSON 내용 복사하여 GitHub Secret에 추가
cat github-actions-key.json
```

#### 2. GCP_PROJECT_ID

```
your-gcp-project-id
```

GCP 프로젝트 ID 확인:

```bash
gcloud config get-value project
```

#### 3. GCP_INSTANCE_NAME

```
dongheng-server
```

Compute Engine 인스턴스 이름

#### 4. GCP_ZONE

```
asia-northeast3-a
```

인스턴스가 위치한 Zone

#### 5. GCP_INSTANCE_IP

```
35.123.456.789
```

인스턴스의 외부 IP (헬스체크용)

---

## 📦 GCP Artifact Registry 설정

### 1. Artifact Registry 활성화

```bash
gcloud services enable artifactregistry.googleapis.com
```

### 2. Docker 저장소 생성

```bash
gcloud artifacts repositories create dongheng \
  --repository-format=docker \
  --location=asia-northeast3 \
  --description="Dongheng Backend Docker Images"
```

### 3. 저장소 확인

```bash
gcloud artifacts repositories list
```

---

## 🖥️ GCP Compute Engine 준비

### VM 인스턴스에서 실행할 작업

```bash
# 1. VM 접속
gcloud compute ssh dongheng-server --zone=asia-northeast3-a

# 2. Docker 설치
sudo apt-get update
sudo apt-get install -y docker.io
sudo systemctl start docker
sudo systemctl enable docker

# 3. 현재 사용자를 docker 그룹에 추가
sudo usermod -aG docker $USER

# 4. gcloud SDK 설치 (이미 있으면 생략)
curl https://sdk.cloud.google.com | bash
exec -l $SHELL

# 5. Docker 인증 설정
gcloud auth configure-docker asia-northeast3-docker.pkg.dev

# 6. 환경변수 파일 생성
nano ~/.env.production
# (환경변수 입력 - ENVIRONMENT_SETUP.md 참고)

chmod 600 ~/.env.production

# 7. 서비스 계정으로 인증 (GitHub Actions에서 접근용)
gcloud auth activate-service-account --key-file=github-actions-key.json
```

---

## 🔄 워크플로우 동작 방식

### CI 워크플로우 (Pull Request)

```
1. 코드 체크아웃
2. Node.js 20 설정
3. 의존성 설치
4. 린트 검사 (ESLint)
5. 포맷 검사 (Prettier)
6. Prisma Client 생성
7. 테스트 DB 마이그레이션
8. 테스트 실행
9. 커버리지 업로드
10. Docker 이미지 빌드 테스트
```

### CD 워크플로우 (main 브랜치 푸시)

```
1. 코드 체크아웃
2. GCP 인증
3. Docker 설정
4. 이미지 빌드 & 푸시
   - SHA 태그 (예: abc1234)
   - latest 태그
5. GCP VM에 SSH 접속
6. 최신 이미지 Pull
7. 기존 컨테이너 중지 & 삭제
8. 새 컨테이너 실행
9. 헬스체크
10. 실패 시 자동 롤백
```

---

## 📝 배포 프로세스

### 개발 플로우

```
1. feature 브랜치에서 개발
   git checkout -b feature/new-feature

2. 커밋 & 푸시
   git add .
   git commit -m "Add new feature"
   git push origin feature/new-feature

3. Pull Request 생성
   → CI 워크플로우 자동 실행
   → 린트, 테스트, 빌드 검증

4. develop 브랜치로 머지
   → CI 워크플로우 실행 (재검증)

5. main 브랜치로 머지
   → CD 워크플로우 실행
   → 자동 배포
```

### 핫픽스 플로우

```
1. main 브랜치에서 hotfix 브랜치 생성
   git checkout -b hotfix/critical-bug main

2. 수정 & 테스트

3. main에 직접 머지
   → 즉시 배포
```

---

## 🧪 로컬에서 워크플로우 테스트

### act 사용 (선택사항)

```bash
# act 설치 (Linux/macOS)
curl https://raw.githubusercontent.com/nektos/act/master/install.sh | sudo bash

# Windows (Chocolatey)
choco install act-cli

# CI 워크플로우 로컬 실행
act pull_request -W .github/workflows/ci.yml
```

---

## 🔍 모니터링 & 디버깅

### GitHub Actions 로그 확인

1. GitHub 저장소 > **Actions** 탭
2. 실행된 워크플로우 클릭
3. 각 Job의 로그 확인

### GCP VM 로그 확인

```bash
# VM 접속
gcloud compute ssh dongheng-server --zone=asia-northeast3-a

# 컨테이너 로그
docker logs dongheng-backend

# 실시간 로그
docker logs -f dongheng-backend

# 최근 100줄
docker logs --tail 100 dongheng-backend
```

---

## 🚨 문제 해결

### 배포 실패 시

1. **GitHub Actions 로그 확인**
   - 어느 단계에서 실패했는지 확인

2. **이미지 빌드 실패**
   ```bash
   # 로컬에서 빌드 테스트
   cd DongHaeng_backend
   docker build -t test .
   ```

3. **VM 접속 실패**
   - VM이 실행 중인지 확인
   - SSH 키 설정 확인
   - 방화벽 규칙 확인

4. **컨테이너 실행 실패**
   ```bash
   # VM에서 수동 실행 테스트
   docker run -it --rm \
     --env-file ~/.env.production \
     asia-northeast3-docker.pkg.dev/PROJECT_ID/dongheng/dongheng-backend:latest
   ```

5. **헬스체크 실패**
   ```bash
   # 수동 헬스체크
   curl http://INSTANCE_IP:3000/health

   # 컨테이너 내부 확인
   docker exec -it dongheng-backend sh
   wget -O- http://localhost:3000/health
   ```

---

## 🔐 보안 Best Practices

### ✅ 체크리스트

- [ ] GitHub Secrets에 민감 정보 저장 (코드에 하드코딩 금지)
- [ ] GCP 서비스 계정 최소 권한 원칙 적용
- [ ] VM에 환경변수 파일 안전하게 저장 (chmod 600)
- [ ] SSH 키 기반 인증 사용
- [ ] 프로덕션 DB 접근 제한 (IP 화이트리스트)
- [ ] HTTPS 적용 (Let's Encrypt)
- [ ] 정기적인 보안 패치

---

## 📊 배포 알림 (선택)

### Slack 알림 추가

cd.yml에 추가:

```yaml
- name: Notify Slack
  if: always()
  uses: 8398a7/action-slack@v3
  with:
    status: ${{ job.status }}
    text: 'Deployment ${{ job.status }}'
    webhook_url: ${{ secrets.SLACK_WEBHOOK_URL }}
```

### Discord 알림

```yaml
- name: Notify Discord
  if: always()
  uses: sarisia/actions-status-discord@v1
  with:
    webhook: ${{ secrets.DISCORD_WEBHOOK }}
    status: ${{ job.status }}
```

---

## 🎯 다음 단계

- [ ] GitHub Secrets 설정
- [ ] GCP Artifact Registry 생성
- [ ] GCP VM Docker 설치
- [ ] 환경변수 파일 생성
- [ ] 테스트 배포 실행
- [ ] 모니터링 설정

---

**작성일:** 2025-11-07
**버전:** 1.0
