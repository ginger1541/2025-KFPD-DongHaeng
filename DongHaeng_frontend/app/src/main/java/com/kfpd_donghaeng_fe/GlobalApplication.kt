package com.kfpd_donghaeng_fe

import android.app.Application
import android.util.Log
import com.kakao.vectormap.KakaoMapSdk
import com.kfpd_donghaeng_fe.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {

    // 지도 사용 가능 여부를 저장할 전역 변수
    companion object {
        var isMapLoaded = false
    }

    override fun onCreate() {
        super.onCreate()

        try {
            // 카카오맵 초기화 시도
            KakaoMapSdk.init(this, "36496bc3af191f9e2fab5202d4f894dc")
            isMapLoaded = true // 성공하면 true
        } catch (e: UnsatisfiedLinkError) {
            // 에뮬레이터라서 실패하면 여기로 빠짐
            // 앱을 죽이지 않고, 에러 로그만 남기고 넘어감
            e.printStackTrace()
            isMapLoaded = false // 실패했으므로 false
        } catch (e: Exception) {
            e.printStackTrace()
            isMapLoaded = false
        }
    }
}