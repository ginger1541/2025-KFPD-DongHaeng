/*
  Warnings:

  - Added the required column `scheduled_at` to the `companion_requests` table without a default value. This is not possible if the table is not empty.

*/
-- AlterTable (3단계로 분리하여 기존 데이터 처리)
-- 1. route 컬럼 추가 (NULL 허용)
ALTER TABLE `companion_requests` ADD COLUMN `route` JSON NULL;

-- 2. scheduled_at 컬럼을 먼저 NULL 허용으로 추가
ALTER TABLE `companion_requests` ADD COLUMN `scheduled_at` DATETIME(3) NULL;

-- 3. 기존 데이터의 scheduled_at을 requested_at 값으로 업데이트
UPDATE `companion_requests` SET `scheduled_at` = `requested_at` WHERE `scheduled_at` IS NULL;

-- 4. scheduled_at을 NOT NULL로 변경
ALTER TABLE `companion_requests` MODIFY COLUMN `scheduled_at` DATETIME(3) NOT NULL;

-- CreateIndex
CREATE INDEX `idx_scheduled_at` ON `companion_requests`(`scheduled_at` ASC);
