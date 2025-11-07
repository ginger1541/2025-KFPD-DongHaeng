/*
  Warnings:

  - You are about to drop the column `actual_duration_minutes` on the `matches` table. All the data in the column will be lost.

*/
-- AlterTable
ALTER TABLE `matches` DROP COLUMN `actual_duration_minutes`,
    ADD COLUMN `actual_minutes` INTEGER NULL;
