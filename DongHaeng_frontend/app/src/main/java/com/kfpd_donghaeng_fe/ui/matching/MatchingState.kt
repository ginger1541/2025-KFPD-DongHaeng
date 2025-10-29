package com.kfpd_donghaeng_fe.ui.matching

data class MatchingState(
    val OngoingPage: Int = 0, // ongoing page 용
    val DHType: DHType? = null, // null,  accept, ongoing, request, terminate

)

//동행 끝 시작
enum class DHType {
    accept,
    ongoing,
    request,
    terminate
}