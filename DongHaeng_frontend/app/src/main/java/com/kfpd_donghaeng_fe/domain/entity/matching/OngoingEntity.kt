package com.kfpd_donghaeng_fe.domain.entity.matching

import com.kfpd_donghaeng_fe.domain.entity.auth.UserType

data class OngoingEntity(
    val OngoingPage : Int =0,
    val userType: UserType? = null,
)

data class OngoingRequestEntity(
    val requestId: Long=0,
    val startAddress : String ="start",
    val destinationAddress: String = "end" ,
    val status: String="stuats" ,
    val Name : String ="hi",
    val ProfileImageUrl: String ?=null,
    val DHScore:Int=90,
)