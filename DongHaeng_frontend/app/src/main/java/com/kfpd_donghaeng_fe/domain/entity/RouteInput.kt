package com.kfpd_donghaeng_fe.domain.entity

data class RouteInput(
    val id: String,
    val type: LocationType,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isEditable: Boolean = true
)

enum class LocationType {
    START,
    WAYPOINT,
    END
}

data class RouteState(
    val locations: List<RouteInput> = emptyList(),
    val hasWaypoint: Boolean = false,
    val canAddWaypoint: Boolean = true,
    val maxWaypoints: Int = 5
)