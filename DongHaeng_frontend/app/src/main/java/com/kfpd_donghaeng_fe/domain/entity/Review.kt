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
    val matchId :Long,
    val revieweeId :Long,
    val rating:Int, //1~5 조건 있음
    val comment : Text,
    val selectedBadges : List<BadgeType>, // 복수선택 가능 리스트
    //----------- 응답에만 있는 것 -----------
    val createdAt :String
)