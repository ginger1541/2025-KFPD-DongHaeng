package com.kfpd_donghaeng_fe.domain.entity

data class MatchRequestData(
    val startAddress : String,
    val destinationAddress : String,
    val status : String,
    val Name : String,
    val ProfileImageUrl : String, // TODO: url check!!!
    val DHScore : Int
)