// data/remote/dto/MatchingHistoryDto.kt
package com.kfpd_donghaeng_fe.data.remote.dto

import com.kfpd_donghaeng_fe.domain.entity.MatchingHistory

data class MatchingHistoryDto(
    val id: Long,
    val date: String,
    val fromName: String,
    val toName: String,
    val departTime: String,
    val arriveTime: String,
    val distanceMeter: Int
) {
    fun toDomain(): MatchingHistory =
        MatchingHistory(
            id = id,
            date = date,
            fromName = fromName,
            toName = toName,
            departTime = departTime,
            arriveTime = arriveTime,
            distanceMeter = distanceMeter
        )
}
