package com.kfpd_donghaeng_fe.domain.entity

data class MatchingHistory(
    val id: Long,
    val date: String,          // 나중에 LocalDate/LocalDateTime 로 바꿔도 됨
    val fromName: String,
    val toName: String,
    val departTime: String,
    val arriveTime: String,
    val distanceMeter: Int     // 서버가 m 단위로 준다고 가정
)
