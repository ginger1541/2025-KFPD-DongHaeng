package com.kfpd_donghaeng_fe.domain.entity

enum class LocationAccuracy { None, Approximate, Precise }

data class LocationPermissionState(
    val isGranted: Boolean,
    val accuracy: LocationAccuracy
)