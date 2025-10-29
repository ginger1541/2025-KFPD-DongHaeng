package com.kfpd_donghaeng_fe.data

data class Request(
    val id: Int,
    val departure: String,
    val arrival: String,
    val departureTime: String,
    val travelTime: String,
    val distance: String,
    val pricePoints: Int
)

// 미리보기용 데이터
val DummyRequests = listOf(
    Request(
        id = 1,
        departure = "연세대학교 세브란스 병원",
        arrival = "동국대학교 서울캠퍼스",
        departureTime = "10분 후 출발",
        travelTime = "약 18분 소요",
        distance = "0.5km",
        pricePoints = 250
    ),
    Request(
        id = 2,
        departure = "서울 마포 경찰서",
        arrival = "서울역",
        departureTime = "10분 후 출발",
        travelTime = "약 25분 소요",
        distance = "0.7km",
        pricePoints = 320
    ),
    Request(
        id = 3,
        departure = "이대역",
        arrival = "홍대입구역",
        departureTime = "10분 후 출발",
        travelTime = "약 10분 소요",
        distance = "1.2km",
        pricePoints = 180
    )
)

fun findRequestById(id: Long): Request? {
    return DummyRequests.find { it.id.toLong() == id }
}