package com.kfpd_donghaeng_fe.data.repository

import androidx.datastore.core.DataStore
import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
import com.kfpd_donghaeng_fe.domain.repository.LoginRepository
import com.kfpd_donghaeng_fe.data.remote.api.LoginApiService

import com.kfpd_donghaeng_fe.data.remote.dto.LoginRequestDto

import com.kfpd_donghaeng_fe.data.remote.dto.toDomainLogin
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginResultEntity
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val apiService: LoginApiService,
    private val tokenDataSource: TokenLocalDataSource
) : LoginRepository {

    override suspend fun isLoggedIn(): Boolean {
        return tokenDataSource.getToken() != null
    }

    suspend fun attemptLogin(email: String, password: String): LoginResultEntity{
        val request = LoginRequestDto(email, password)// LoginRequest는 API Service 파일에 정의됨
        // 실제 API 호출!
        val response = apiService.login(request)

        val loginResult = response.toDomainLogin()

        // 2. 받은 토큰을 DataStore에 저장하는 로직
        tokenDataSource.saveToken(loginResult.token)

        return response.toDomainLogin()
    }
}







