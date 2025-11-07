-- CreateTable
CREATE TABLE `users` (
    `user_id` BIGINT NOT NULL AUTO_INCREMENT,
    `email` VARCHAR(255) NOT NULL,
    `password_hash` VARCHAR(255) NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL,
    `profile_image_url` VARCHAR(500) NULL,
    `bio` TEXT NULL,
    `user_type` ENUM('requester', 'helper', 'both') NOT NULL,
    `birth_date` DATE NOT NULL,
    `gender` ENUM('male', 'female', 'prefer_not_to_say') NOT NULL,
    `is_verified` BOOLEAN NOT NULL DEFAULT false,
    `verification_method` VARCHAR(50) NULL,
    `companion_score` DECIMAL(5, 2) NOT NULL DEFAULT 0,
    `total_companions` INTEGER NOT NULL DEFAULT 0,
    `total_volunteer_minutes` INTEGER NOT NULL DEFAULT 0,
    `total_points` INTEGER NOT NULL DEFAULT 0,
    `is_active` BOOLEAN NOT NULL DEFAULT true,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at` DATETIME(3) NOT NULL,
    `last_login_at` DATETIME(3) NULL,

    UNIQUE INDEX `users_email_key`(`email`),
    UNIQUE INDEX `users_phone_key`(`phone`),
    INDEX `idx_email`(`email`),
    INDEX `idx_phone`(`phone`),
    INDEX `idx_user_type`(`user_type`),
    INDEX `idx_companion_score`(`companion_score` DESC),
    PRIMARY KEY (`user_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `user_locations` (
    `location_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `coordinates` POINT NULL,
    `latitude` DECIMAL(10, 8) NOT NULL,
    `longitude` DECIMAL(11, 8) NOT NULL,
    `updated_at` DATETIME(3) NOT NULL,
    `is_active` BOOLEAN NOT NULL DEFAULT true,

    INDEX `idx_user_id`(`user_id`),
    INDEX `idx_is_active`(`is_active`),
    PRIMARY KEY (`location_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `companion_requests` (
    `request_id` BIGINT NOT NULL AUTO_INCREMENT,
    `requester_id` BIGINT NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `description` TEXT NULL,
    `start_location` POINT NULL,
    `destination` POINT NULL,
    `latitude` DECIMAL(10, 7) NOT NULL,
    `longitude` DECIMAL(10, 7) NOT NULL,
    `start_address` VARCHAR(500) NOT NULL,
    `destination_address` VARCHAR(500) NOT NULL,
    `estimated_minutes` INTEGER NOT NULL,
    `status` ENUM('pending', 'matching', 'in_progress', 'completed', 'cancelled') NOT NULL DEFAULT 'pending',
    `view_count` INTEGER NOT NULL DEFAULT 0,
    `requested_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `expires_at` DATETIME(3) NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at` DATETIME(3) NOT NULL,

    INDEX `idx_requester_id`(`requester_id`),
    INDEX `idx_status`(`status`),
    INDEX `idx_requested_at`(`requested_at` DESC),
    PRIMARY KEY (`request_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `matches` (
    `match_id` BIGINT NOT NULL AUTO_INCREMENT,
    `request_id` BIGINT NOT NULL,
    `requester_id` BIGINT NOT NULL,
    `helper_id` BIGINT NOT NULL,
    `status` ENUM('pending', 'in_progress', 'completed', 'cancelled') NOT NULL DEFAULT 'pending',
    `matched_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `started_at` DATETIME(3) NULL,
    `completed_at` DATETIME(3) NULL,
    `actual_duration_minutes` INTEGER NULL,
    `earned_points` INTEGER NULL,
    `earned_volunteer_minutes` INTEGER NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at` DATETIME(3) NOT NULL,

    UNIQUE INDEX `matches_request_id_key`(`request_id`),
    INDEX `idx_requester_id`(`requester_id`),
    INDEX `idx_helper_id`(`helper_id`),
    INDEX `idx_status`(`status`),
    INDEX `idx_matched_at`(`matched_at` DESC),
    INDEX `idx_requester_matched_at`(`requester_id`, `matched_at` DESC),
    INDEX `idx_helper_matched_at`(`helper_id`, `matched_at` DESC),
    PRIMARY KEY (`match_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `qr_authentications` (
    `auth_id` BIGINT NOT NULL AUTO_INCREMENT,
    `match_id` BIGINT NOT NULL,
    `auth_type` ENUM('start', 'end') NOT NULL,
    `qrCode` VARCHAR(255) NOT NULL,
    `scanned_by_user_id` BIGINT NULL,
    `scan_location` POINT NULL,
    `scanned_at` DATETIME(3) NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    UNIQUE INDEX `qr_authentications_qrCode_key`(`qrCode`),
    INDEX `idx_match_id`(`match_id`),
    INDEX `idx_qr_code`(`qrCode`),
    PRIMARY KEY (`auth_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `chat_messages` (
    `message_id` BIGINT NOT NULL AUTO_INCREMENT,
    `match_id` BIGINT NOT NULL,
    `sender_id` BIGINT NOT NULL,
    `message_content` TEXT NOT NULL,
    `is_read` BOOLEAN NOT NULL DEFAULT false,
    `read_at` DATETIME(3) NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    INDEX `idx_match_id`(`match_id`),
    INDEX `idx_sender_id`(`sender_id`),
    INDEX `idx_created_at`(`created_at` DESC),
    PRIMARY KEY (`message_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `reviews` (
    `review_id` BIGINT NOT NULL AUTO_INCREMENT,
    `match_id` BIGINT NOT NULL,
    `reviewer_id` BIGINT NOT NULL,
    `reviewee_id` BIGINT NOT NULL,
    `rating` INTEGER NOT NULL,
    `comment` TEXT NULL,
    `selected_badges` JSON NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updated_at` DATETIME(3) NOT NULL,

    INDEX `idx_reviewee_id`(`reviewee_id`),
    INDEX `idx_rating`(`rating`),
    UNIQUE INDEX `unique_match_reviewer`(`match_id`, `reviewer_id`),
    PRIMARY KEY (`review_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `badge_types` (
    `badge_type_id` BIGINT NOT NULL AUTO_INCREMENT,
    `badge_name` VARCHAR(100) NOT NULL,
    `badge_icon_url` VARCHAR(500) NULL,
    `description` TEXT NULL,
    `unlock_condition` JSON NOT NULL,
    `required_companions` INTEGER NOT NULL DEFAULT 0,
    `required_points` INTEGER NOT NULL DEFAULT 0,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    UNIQUE INDEX `badge_types_badge_name_key`(`badge_name`),
    PRIMARY KEY (`badge_type_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `badges` (
    `badge_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `badge_type_id` BIGINT NOT NULL,
    `earned_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    INDEX `idx_user_id`(`user_id`),
    INDEX `idx_badge_type_id`(`badge_type_id`),
    UNIQUE INDEX `unique_user_badge`(`user_id`, `badge_type_id`),
    PRIMARY KEY (`badge_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `points_history` (
    `history_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `points_change` INTEGER NOT NULL,
    `transaction_type` ENUM('earn', 'spend', 'expire') NOT NULL,
    `source_type` ENUM('companion', 'reward_usage', 'event', 'admin') NOT NULL,
    `reference_id` BIGINT NULL,
    `description` TEXT NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    INDEX `idx_user_id`(`user_id`),
    INDEX `idx_transaction_type`(`transaction_type`),
    INDEX `idx_created_at`(`created_at` DESC),
    PRIMARY KEY (`history_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `volunteer_hours` (
    `record_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `match_id` BIGINT NOT NULL,
    `minutes` INTEGER NOT NULL,
    `is_synced_to_1365` BOOLEAN NOT NULL DEFAULT false,
    `sync_reference_id` VARCHAR(255) NULL,
    `synced_at` DATETIME(3) NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    INDEX `idx_user_id`(`user_id`),
    INDEX `idx_match_id`(`match_id`),
    INDEX `idx_is_synced`(`is_synced_to_1365`),
    PRIMARY KEY (`record_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `notifications` (
    `notification_id` BIGINT NOT NULL AUTO_INCREMENT,
    `user_id` BIGINT NOT NULL,
    `notification_type` ENUM('match_request', 'match_accepted', 'companion_start', 'message', 'badge_earned', 'system') NOT NULL,
    `title` VARCHAR(200) NOT NULL,
    `content` TEXT NOT NULL,
    `data` JSON NULL,
    `is_read` BOOLEAN NOT NULL DEFAULT false,
    `read_at` DATETIME(3) NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    INDEX `idx_user_id`(`user_id`),
    INDEX `idx_is_read`(`is_read`),
    INDEX `idx_created_at`(`created_at` DESC),
    PRIMARY KEY (`notification_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `reports` (
    `report_id` BIGINT NOT NULL AUTO_INCREMENT,
    `reporter_id` BIGINT NOT NULL,
    `reported_user_id` BIGINT NOT NULL,
    `match_id` BIGINT NULL,
    `report_type` ENUM('inappropriate_behavior', 'no_show', 'safety_threat', 'other') NOT NULL,
    `description` TEXT NOT NULL,
    `status` ENUM('pending', 'reviewing', 'completed', 'rejected') NOT NULL DEFAULT 'pending',
    `admin_note` TEXT NULL,
    `resolved_at` DATETIME(3) NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    INDEX `idx_reported_user_id`(`reported_user_id`),
    INDEX `idx_status`(`status`),
    INDEX `idx_created_at`(`created_at` DESC),
    PRIMARY KEY (`report_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `blocked_users` (
    `block_id` BIGINT NOT NULL AUTO_INCREMENT,
    `blocker_id` BIGINT NOT NULL,
    `blocked_id` BIGINT NOT NULL,
    `created_at` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),

    INDEX `idx_blocker_id`(`blocker_id`),
    INDEX `idx_blocked_id`(`blocked_id`),
    UNIQUE INDEX `unique_blocker_blocked`(`blocker_id`, `blocked_id`),
    PRIMARY KEY (`block_id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE `user_locations` ADD CONSTRAINT `user_locations_user_id_fkey` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `companion_requests` ADD CONSTRAINT `companion_requests_requester_id_fkey` FOREIGN KEY (`requester_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `matches` ADD CONSTRAINT `matches_request_id_fkey` FOREIGN KEY (`request_id`) REFERENCES `companion_requests`(`request_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `matches` ADD CONSTRAINT `matches_requester_id_fkey` FOREIGN KEY (`requester_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `matches` ADD CONSTRAINT `matches_helper_id_fkey` FOREIGN KEY (`helper_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `qr_authentications` ADD CONSTRAINT `qr_authentications_match_id_fkey` FOREIGN KEY (`match_id`) REFERENCES `matches`(`match_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `qr_authentications` ADD CONSTRAINT `qr_authentications_scanned_by_user_id_fkey` FOREIGN KEY (`scanned_by_user_id`) REFERENCES `users`(`user_id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `chat_messages` ADD CONSTRAINT `chat_messages_match_id_fkey` FOREIGN KEY (`match_id`) REFERENCES `matches`(`match_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `chat_messages` ADD CONSTRAINT `chat_messages_sender_id_fkey` FOREIGN KEY (`sender_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `reviews` ADD CONSTRAINT `reviews_match_id_fkey` FOREIGN KEY (`match_id`) REFERENCES `matches`(`match_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `reviews` ADD CONSTRAINT `reviews_reviewer_id_fkey` FOREIGN KEY (`reviewer_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `reviews` ADD CONSTRAINT `reviews_reviewee_id_fkey` FOREIGN KEY (`reviewee_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `badges` ADD CONSTRAINT `badges_user_id_fkey` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `badges` ADD CONSTRAINT `badges_badge_type_id_fkey` FOREIGN KEY (`badge_type_id`) REFERENCES `badge_types`(`badge_type_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `points_history` ADD CONSTRAINT `points_history_user_id_fkey` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `volunteer_hours` ADD CONSTRAINT `volunteer_hours_user_id_fkey` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `volunteer_hours` ADD CONSTRAINT `volunteer_hours_match_id_fkey` FOREIGN KEY (`match_id`) REFERENCES `matches`(`match_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `notifications` ADD CONSTRAINT `notifications_user_id_fkey` FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `reports` ADD CONSTRAINT `reports_reporter_id_fkey` FOREIGN KEY (`reporter_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `reports` ADD CONSTRAINT `reports_reported_user_id_fkey` FOREIGN KEY (`reported_user_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `reports` ADD CONSTRAINT `reports_match_id_fkey` FOREIGN KEY (`match_id`) REFERENCES `matches`(`match_id`) ON DELETE SET NULL ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `blocked_users` ADD CONSTRAINT `blocked_users_blocker_id_fkey` FOREIGN KEY (`blocker_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `blocked_users` ADD CONSTRAINT `blocked_users_blocked_id_fkey` FOREIGN KEY (`blocked_id`) REFERENCES `users`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE;
