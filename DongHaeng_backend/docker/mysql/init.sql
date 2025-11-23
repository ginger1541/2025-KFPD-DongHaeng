-- MySQL 초기화 스크립트
-- Docker 컨테이너 최초 실행 시 자동으로 실행됨

-- 데이터베이스 문자셋 확인
SELECT @@character_set_database, @@collation_database;

-- 타임존 설정
SET time_zone = '+09:00';

-- 테이블 생성은 Prisma migrate로 처리하므로 여기서는 설정만

-- 초기 설정 완료 로그
SELECT 'MySQL initialization completed!' AS status;
