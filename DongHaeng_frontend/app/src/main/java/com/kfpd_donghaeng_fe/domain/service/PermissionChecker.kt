package com.kfpd_donghaeng_fe.domain.service

import com.kfpd_donghaeng_fe.domain.entity.LocationPermissionState

interface PermissionChecker {
    fun getLocationPermissionState(): LocationPermissionState
}