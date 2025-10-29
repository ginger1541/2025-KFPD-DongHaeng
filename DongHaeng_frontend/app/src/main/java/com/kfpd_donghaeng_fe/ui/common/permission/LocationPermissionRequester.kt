package com.kfpd_donghaeng_fe.ui.common.permission

import android.content.ContextWrapper
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import com.kfpd_donghaeng_fe.domain.entity.LocationAccuracy
import com.kfpd_donghaeng_fe.domain.entity.LocationPermissionState
import com.kfpd_donghaeng_fe.domain.service.AppSettingsNavigator
import com.kfpd_donghaeng_fe.domain.service.PermissionChecker

class LocationPermissionRequester(
    val state: State<LocationPermissionState>,          // 현재 권한 스냅샷
    val shouldShowRationale: State<Boolean>,            // 합리적 설명 필요 여부
    val request: () -> Unit,                            // 시스템 다이얼로그 띄우기
    val openAppSettings: () -> Unit                     // 앱 설정 열기
)

@Composable
fun rememberLocationPermissionRequester(
    checker: PermissionChecker,
    appSettingsNavigator: AppSettingsNavigator
): LocationPermissionRequester {
    val context = LocalContext.current
    val activity = remember { context.findActivity() }

    // 현재 스냅샷
    var snapshot by remember { mutableStateOf(checker.getLocationPermissionState()) }

    fun computeRationale(): Boolean {
        val fineDenied = ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
        val coarseDenied = ActivityCompat.checkSelfPermission(
            context, android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED

        val anyDenied = fineDenied && coarseDenied
        val needRationale =
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity, android.Manifest.permission.ACCESS_FINE_LOCATION
            ) || ActivityCompat.shouldShowRequestPermissionRationale(
                activity, android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        return anyDenied && needRationale
    }
    var rationale by remember { mutableStateOf(computeRationale()) }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fine = result[android.Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarse = result[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
        snapshot = LocationPermissionState(
            isGranted = fine || coarse,
            accuracy = when {
                fine -> LocationAccuracy.Precise
                coarse -> LocationAccuracy.Approximate
                else -> LocationAccuracy.None
            }
        )
        rationale = computeRationale()
    }

    val request: () -> Unit = {
        launcher.launch(
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    LaunchedEffect(Unit) {
        snapshot = checker.getLocationPermissionState()
        rationale = computeRationale()
    }

    return remember {
        LocationPermissionRequester(
            state = derivedStateOf { snapshot },
            shouldShowRationale = derivedStateOf { rationale },
            request = request,
            openAppSettings = appSettingsNavigator::openAppSettings
        )
    }
}

private tailrec fun android.content.Context.findActivity(): ComponentActivity {
    return when (this) {
        is ComponentActivity -> this
        is ContextWrapper -> baseContext.findActivity()
        else -> error("No Activity found in context chain.")
    }
}