package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.domain.repository.LoginRepository
import kotlinx.coroutines.delay
import javax.inject.Inject

// 💡 @Inject constructor()가 있어야 Hilt가 이 클래스를 만들 수 있습니다.
class LoginRepositoryImpl @Inject constructor(
    // 나중에 여기에 private val api: LoginApiService 같은게 들어옵니다.
) : LoginRepository {

    override suspend fun isLoggedIn(): Boolean {
        // TODO: 나중에는 여기서 api.login() 을 호출합니다.

        // 지금은 서버 연결 흉내(1초 대기)만 내고 true를 줍니다.
        // 하지만 구조적으로는 이제 '데이터 레이어'를 거쳐가게 된 것입니다.
        delay(1000)
        return true
    }
}