package com.kfpd_donghaeng_fe.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationTracker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission") // 호출하는 쪽에서 권한 체크 필수
    fun getLocationFlow(): Flow<Location> = callbackFlow {
        // 1. 위치 요청 설정 (5초마다, 높은 정확도)
        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000L)
            .setMinUpdateIntervalMillis(2000L) // 최소 2초 간격
            .build()

        // 2. 콜백 정의
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.locations.lastOrNull()?.let { location ->
                    trySend(location) // 위치가 올 때마다 Flow로 내보냄
                }
            }
        }

        // 3. 업데이트 시작
        client.requestLocationUpdates(request, callback, Looper.getMainLooper())

        // 4. Flow가 닫힐 때(화면 나갈 때) 업데이트 중지
        awaitClose {
            client.removeLocationUpdates(callback)
        }
    }
}