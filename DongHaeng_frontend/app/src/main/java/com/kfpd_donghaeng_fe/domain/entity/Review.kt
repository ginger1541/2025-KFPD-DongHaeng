package com.kfpd_donghaeng_fe.domain.entity

import org.w3c.dom.Text


enum class BadgeType(val description: String) {
    KIND("친절해요"),
    PUNCTUAL("시간 잘 지켰어요"),
    COMMUNICATIVE("소통이 원활해요");
    companion object {
        fun fromString(badgeString: String): BadgeType? {
            return entries.find {
                it.description == badgeString
            }
        }
    }

}


//createdat == notnull
data class ReviewResponseEntity(
    val reviewId: Long,  // ✅ 추가
    val matchId: Long,
    val reviewerId: Long,  // ✅ 추가
    val revieweeId: Long,
    val rating: Int,
    val comment: String,
    val selectedBadges: List<String>,
    val createdAt: String
)