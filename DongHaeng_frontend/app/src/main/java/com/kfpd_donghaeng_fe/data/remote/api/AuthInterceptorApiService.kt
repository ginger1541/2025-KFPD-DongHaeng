package com.kfpd_donghaeng_fe.data.remote.api


import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenDataSource: TokenLocalDataSource // DataStore에서 토큰을 가져오기 위한 의존성
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // Interceptor는 동기(synchronous)로 작동해야 하므로, 코루틴의 결과를 가져오기 위해 runBlocking 사용
        val token = runBlocking {
            tokenDataSource.getToken()
        }

        val request = chain.request()

        val newRequest = if (token.isNullOrEmpty()) {
            // 토큰이 없으면 헤더를 추가하지 않습니다. (로그인 요청 등 인증 불필요 API)
            request
        } else {
            // 토큰이 있으면 Authorization 헤더를 추가합니다.
            request.newBuilder()
                // 💡 "Authorization: Bearer [토큰 값]" 형식으로 헤더 추가
                .header("Authorization", "Bearer $token")
                .build()
        }

        return chain.proceed(newRequest)
    }
}