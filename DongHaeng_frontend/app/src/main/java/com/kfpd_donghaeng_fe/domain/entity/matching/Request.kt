package com.kfpd_donghaeng_fe.domain.entity.matching



//요청자 , 요청 정보
data class RequestDataEntity(
    val startAddress : String,
    val destinationAddress : String,
    val status : String,
    val Name : String,
    val ProfileImageUrl : String,
    val DHScore : Int
)

data class UserPhone(
    val Requestphone:String
)