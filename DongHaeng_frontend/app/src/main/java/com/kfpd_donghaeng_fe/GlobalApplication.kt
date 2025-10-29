package com.kfpd_donghaeng_fe

import android.app.Application
import android.util.Log
import com.kakao.vectormap.KakaoMapSdk
import com.kfpd_donghaeng_fe.BuildConfig
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val nativeAppKey = BuildConfig.KAKAO_NATIVE_APP_KEY
        KakaoMapSdk.init(this, nativeAppKey)
    }
}