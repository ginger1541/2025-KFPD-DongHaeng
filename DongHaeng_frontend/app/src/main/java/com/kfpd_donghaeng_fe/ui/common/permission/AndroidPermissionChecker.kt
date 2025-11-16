package com.kfpd_donghaeng_fe.ui.common.permission

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.kfpd_donghaeng_fe.domain.entity.LocationAccuracy
import com.kfpd_donghaeng_fe.domain.entity.LocationPermissionState
import com.kfpd_donghaeng_fe.domain.service.PermissionChecker

class AndroidPermissionChecker(
    private val context: android.content.Context
) : PermissionChecker {
    override fun getLocationPermissionState(): LocationPermissionState {
        val fine = ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val coarse = ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val accuracy = when {
            fine -> LocationAccuracy.Precise
            coarse -> LocationAccuracy.Approximate
            else -> LocationAccuracy.None
        }
        return LocationPermissionState(isGranted = fine || coarse, accuracy = accuracy)
    }
}