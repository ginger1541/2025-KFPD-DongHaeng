# ================================
# Stage 1: Builder (빌드 환경)
# ================================
FROM node:20-alpine AS builder

# 작업 디렉토리 설정
WORKDIR /app

# 패키지 파일 복사
COPY package*.json ./
COPY prisma ./prisma/

# 의존성 설치 (프로덕션 + devDependencies)
RUN npm ci

# 소스 코드 복사
COPY . .

# Prisma Client 생성
RUN npm run prisma:generate

# TypeScript 빌드
RUN npm run build

# ================================
# Stage 2: Production (운영 환경)
# ================================
FROM node:20-alpine

# 필수 시스템 패키지 설치 (bcrypt 등 네이티브 모듈용)
RUN apk add --no-cache \
    python3 \
    make \
    g++ \
    && rm -rf /var/cache/apk/*

# 작업 디렉토리 설정
WORKDIR /app

# 프로덕션 패키지 파일 복사
COPY package*.json ./
COPY prisma ./prisma/

# 프로덕션 의존성만 설치
RUN npm ci --only=production && npm cache clean --force

# 빌드된 파일 복사
COPY --from=builder /app/dist ./dist
COPY --from=builder /app/node_modules/.prisma ./node_modules/.prisma

# 환경변수 설정
ENV NODE_ENV=production
ENV PORT=3000

# 헬스체크
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD node -e "require('http').get('http://localhost:3000/health', (r) => {process.exit(r.statusCode === 200 ? 0 : 1)})"

# 포트 노출
EXPOSE 3000

# 앱 실행 (PM2는 docker-compose에서 처리)
CMD ["node", "dist/index.js"]
