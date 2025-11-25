package com.kfpd_donghaeng_fe.data.remote.api


import android.util.Log
import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataSource: TokenLocalDataSource
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenDataSource.getToken() }
        val request = chain.request()

        // 🔍 [디버깅] 토큰 확인 로그
        if (token.isNullOrEmpty()) {
            Log.e("AuthInterceptor", "🚨 저장된 토큰이 없습니다! (비로그인 요청)")
        } else {
            Log.d("AuthInterceptor", "✅ 토큰 발견! 헤더에 추가합니다: ${token.take(10)}...")
        }

        val newRequest = if (token.isNullOrEmpty()) {
            request
        } else {
            request.newBuilder()
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(newRequest)
    }
}