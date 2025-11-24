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

    override suspend fun attemptLogin(email: String, password: String): LoginResultEntity {
        // 1. API 호출
        val request = LoginRequestDto(email, password)
        val response = apiService.login(request)

        // 2. 변환 (이때 DTO에서 토큰을 꺼내옴)
        val loginResult = response.toDomainLogin()

        // 3. 토큰 저장
        tokenDataSource.saveToken(loginResult.token)

        // 유저 타입 저장
        loginResult.userData.userType?.let { type ->
            tokenDataSource.saveUserType(type)
        }
        return loginResult
    }

    override suspend fun isLoggedIn(): Boolean {
        return tokenDataSource.getToken() != null
    }

}







