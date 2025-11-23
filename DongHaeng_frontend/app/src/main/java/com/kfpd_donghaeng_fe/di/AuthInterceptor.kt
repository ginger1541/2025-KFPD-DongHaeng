package com.kfpd_donghaeng_fe.di // NetworkModule과 같은 패키지 사용 가정

import com.kfpd_donghaeng_fe.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class KakaoAuthInterceptor : Interceptor {

    private val KAKAO_REST_API_KEY = BuildConfig.KAKAO_REST_API_KEY

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val newRequest = originalRequest.newBuilder()
            .header("Authorization", "KakaoAK $KAKAO_REST_API_KEY")
            .build()

        return chain.proceed(newRequest)
    }
}