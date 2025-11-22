#!/bin/bash

# 동행 백엔드 배포 스크립트
# 사용법: ./deploy.sh

set -e  # 오류 발생 시 스크립트 중단

echo "=========================================="
echo "동행 백엔드 배포 시작"
echo "=========================================="

# 색상 코드
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# 1. 최신 코드 가져오기
echo -e "${YELLOW}[1/7] Git Pull...${NC}"
git pull origin main

# 2. 백엔드 디렉토리로 이동
cd DongHaeng_backend

# 3. 의존성 설치
echo -e "${YELLOW}[2/7] npm install...${NC}"
npm install

# 4. Prisma 클라이언트 생성
echo -e "${YELLOW}[3/7] Prisma Generate...${NC}"
npm run prisma:generate

# 5. 데이터베이스 마이그레이션
echo -e "${YELLOW}[4/7] Database Migration...${NC}"
npm run prisma:migrate:prod

# 6. TypeScript 빌드
echo -e "${YELLOW}[5/7] TypeScript Build...${NC}"
npm run build

# 7. 서버 재시작
echo -e "${YELLOW}[6/7] Server Restart...${NC}"

# PM2가 설치되어 있는지 확인
if command -v pm2 &> /dev/null; then
    echo "PM2로 재시작 중..."
    pm2 restart companion-api || pm2 start dist/index.js --name companion-api
else
    echo -e "${RED}PM2가 설치되어 있지 않습니다. 수동으로 서버를 재시작하세요.${NC}"
    exit 1
fi

# 8. 배포 확인
echo -e "${YELLOW}[7/7] Deployment Verification...${NC}"
sleep 3
pm2 status

echo ""
echo -e "${GREEN}=========================================="
echo "배포 완료!"
echo -e "==========================================${NC}"
echo ""
echo "로그 확인: pm2 logs companion-api"
echo "상태 확인: pm2 status"
echo ""
