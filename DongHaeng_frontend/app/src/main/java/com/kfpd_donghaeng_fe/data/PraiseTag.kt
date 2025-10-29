package com.kfpd_donghaeng_fe.data

data class PraiseTag(
    val id: Int,
    val text: String
)

val PredefinedPraiseTags = listOf(
    PraiseTag(1, "친절해요"),
    PraiseTag(2, "시간약속을 잘 지켜요"),
    PraiseTag(3, "안전하게 도와줬어요"),
    PraiseTag(4, "소통이 원활해요"),
    PraiseTag(5, "전문적이에요"),
    PraiseTag(6, "다시 만나고 싶어요")
)