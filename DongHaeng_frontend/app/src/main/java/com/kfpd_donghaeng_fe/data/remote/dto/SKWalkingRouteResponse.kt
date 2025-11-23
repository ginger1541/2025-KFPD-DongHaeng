package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.google.gson.JsonElement // 💡 import 추가

data class SKWalkingRouteResponse(
    val features: List<Feature>
)

data class Feature(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)

data class Geometry(
    val type: String, // "Point" 또는 "LineString"

    // 💡 [수정] List<List<Double>> -> JsonElement
    // Point일 때는 [Double, Double], LineString일 때는 [[Double, Double], ...] 이므로 유연하게 받음
    val coordinates: JsonElement
)

data class Properties(
    val totalDistance: Int = 0,
    val totalTime: Int = 0,
    val index: Int = 0,
    @SerializedName("lineIndex")
    val lineIndex: Int? = null,
    val description: String? = null
)