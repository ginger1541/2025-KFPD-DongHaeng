# 📊 PM2 프로세스 관리 가이드

## 🎯 PM2란?

PM2는 Node.js 애플리케이션을 위한 프로덕션 프로세스 매니저입니다.

**주요 기능:**
- 클러스터 모드 (멀티 프로세스)
- 자동 재시작
- 로드 밸런싱
- 로그 관리
- 모니터링

---

## 📦 설치

### 글로벌 설치

```bash
npm install -g pm2

# 확인
pm2 --version
```

---

## 🚀 기본 사용법

### 1. 앱 시작

```bash
# 단일 인스턴스
pm2 start dist/index.js --name dongheng-backend

# Ecosystem 파일 사용 (권장)
pm2 start ecosystem.config.js --env production

# 클러스터 모드 (4개 인스턴스)
pm2 start dist/index.js -i 4 --name dongheng-backend
```

### 2. 앱 관리

```bash
# 상태 확인
pm2 status
pm2 list

# 특정 앱 정보
pm2 info dongheng-backend

# 중지
pm2 stop dongheng-backend

# 재시작
pm2 restart dongheng-backend

# 리로드 (무중단 재시작)
pm2 reload dongheng-backend

# 삭제
pm2 delete dongheng-backend

# 모든 앱 삭제
pm2 delete all
```

### 3. 로그 확인

```bash
# 실시간 로그
pm2 logs dongheng-backend

# 최근 로그
pm2 logs --lines 100

# 에러 로그만
pm2 logs --err

# 로그 초기화
pm2 flush
```

---

## 🔧 Ecosystem 설정

### ecosystem.config.js

이미 생성된 `ecosystem.config.js` 파일을 사용하세요.

**주요 설정:**

```javascript
{
  instances: 2,              // 인스턴스 수 (또는 'max')
  exec_mode: 'cluster',      // 클러스터 모드
  max_memory_restart: '1G',  // 메모리 제한
  autorestart: true,         // 자동 재시작
  watch: false,              // 파일 변경 감지 (개발용)
}
```

### 환경별 실행

```bash
# 프로덕션
pm2 start ecosystem.config.js --env production

# 개발 (로컬)
pm2 start ecosystem.config.js --env development
```

---

## 📊 모니터링

### 1. 내장 모니터

```bash
# 실시간 모니터링
pm2 monit
```

### 2. 웹 대시보드

```bash
# PM2 Plus (무료)
pm2 link YOUR_SECRET_KEY YOUR_PUBLIC_KEY

# 웹에서 확인: https://app.pm2.io
```

### 3. 메트릭 확인

```bash
# CPU, 메모리 사용량
pm2 ls

# 상세 정보
pm2 show dongheng-backend
```

---

## 🔄 자동 시작 설정

서버 재부팅 시 자동으로 PM2 시작:

```bash
# 1. PM2 스타트업 스크립트 생성
pm2 startup

# 2. 표시된 명령어 실행 (예시)
sudo env PATH=$PATH:/usr/bin pm2 startup systemd -u dongheng --hp /home/dongheng

# 3. 현재 프로세스 목록 저장
pm2 save

# 4. 테스트 (서버 재부팅 후 확인)
sudo reboot
pm2 list
```

---

## 🔥 클러스터 모드

### 장점

- CPU 코어 모두 활용
- 자동 로드 밸런싱
- 무중단 배포 (reload)

### 설정

```bash
# CPU 코어 수만큼 자동
pm2 start ecosystem.config.js

# 또는 직접 지정
pm2 start dist/index.js -i 4
```

### 무중단 재시작

```bash
# reload: 인스턴스 하나씩 재시작 (다운타임 0)
pm2 reload ecosystem.config.js

# restart: 모든 인스턴스 동시 재시작 (잠깐 다운타임)
pm2 restart ecosystem.config.js
```

---

## 📝 로그 관리

### 로그 로테이션

PM2 로그 로테이션 모듈 설치:

```bash
pm2 install pm2-logrotate

# 설정
pm2 set pm2-logrotate:max_size 10M         # 파일 크기 10MB
pm2 set pm2-logrotate:retain 30            # 30개 파일 보관
pm2 set pm2-logrotate:compress true        # 압축
pm2 set pm2-logrotate:dateFormat YYYY-MM-DD_HH-mm-ss
pm2 set pm2-logrotate:rotateInterval '0 0 * * *'  # 매일 자정

# 확인
pm2 conf pm2-logrotate
```

### 로그 파일 위치

```bash
# 기본 위치
~/.pm2/logs/

# 커스텀 위치 (ecosystem.config.js에서 설정)
./logs/pm2-error.log
./logs/pm2-out.log
```

---

## 🚀 배포 워크플로우

### 1. 코드 업데이트

```bash
cd /home/dongheng/app
git pull origin main
```

### 2. 의존성 설치 & 빌드

```bash
npm install
npm run build
npm run prisma:generate
```

### 3. 무중단 재배포

```bash
# 리로드 (권장)
pm2 reload ecosystem.config.js --env production

# 또는 재시작
pm2 restart ecosystem.config.js --env production
```

### 4. 확인

```bash
pm2 status
pm2 logs --lines 50
curl http://localhost:3000/health
```

---

## 🐛 문제 해결

### 앱이 계속 재시작될 때

```bash
# 로그 확인
pm2 logs --err

# 상세 정보
pm2 show dongheng-backend

# 메모리 문제인 경우
pm2 set dongheng-backend max_memory_restart 2G
```

### PM2가 시작되지 않을 때

```bash
# PM2 재설치
npm uninstall -g pm2
npm install -g pm2

# 프로세스 초기화
pm2 kill
pm2 start ecosystem.config.js
```

### 포트 충돌

```bash
# 포트 사용 중인 프로세스 확인
sudo lsof -i :3000
sudo netstat -nlp | grep :3000

# PM2 프로세스 모두 종료
pm2 kill
```

---

## 📊 유용한 명령어

```bash
# 프로세스 ID 확인
pm2 id dongheng-backend

# 환경변수 확인
pm2 env 0  # 0은 프로세스 ID

# 프로세스 설정 변경
pm2 set dongheng-backend instances 4

# 업데이트
pm2 update

# PM2 버전 확인
pm2 --version

# 모든 프로세스 정지
pm2 stop all

# 설정 파일 검증
node ecosystem.config.js
```

---

## 🔄 Docker + PM2

Docker 컨테이너 내부에서 PM2 사용 (선택):

### Dockerfile 수정

```dockerfile
# PM2 글로벌 설치
RUN npm install -g pm2

# CMD 변경
CMD ["pm2-runtime", "ecosystem.config.js", "--env", "production"]
```

**장점:**
- 컨테이너 내부에서도 클러스터 모드
- 프로세스 모니터링

**단점:**
- Docker orchestration과 중복
- Kubernetes 사용 시 불필요

---

## 🎯 프로덕션 체크리스트

- [ ] PM2 글로벌 설치
- [ ] ecosystem.config.js 설정
- [ ] 환경변수 파일 준비
- [ ] 로그 디렉토리 생성
- [ ] 클러스터 모드 활성화
- [ ] 자동 시작 설정 (pm2 startup)
- [ ] 로그 로테이션 설정
- [ ] 모니터링 설정
- [ ] 무중단 재시작 테스트
- [ ] 메모리 제한 설정

---

## 📈 성능 튜닝

### 인스턴스 수 설정

```bash
# CPU 코어 수 확인
nproc

# 권장: CPU 코어 수와 동일
pm2 start ecosystem.config.js --instances max

# 또는 고정
pm2 start ecosystem.config.js --instances 4
```

### 메모리 관리

```bash
# 메모리 제한 설정
pm2 set dongheng-backend max_memory_restart 1G

# 힙 크기 조정
pm2 start dist/index.js --node-args="--max-old-space-size=2048"
```

---

## 🔗 참고 자료

- [PM2 공식 문서](https://pm2.keymetrics.io/docs)
- [Ecosystem File](https://pm2.keymetrics.io/docs/usage/application-declaration/)
- [PM2 Plus (모니터링)](https://app.pm2.io)

---

**작성일:** 2025-11-07
**버전:** 1.0
