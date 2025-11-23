package com.kfpd_donghaeng_fe.data.repository

import com.kfpd_donghaeng_fe.domain.repository.LoginRepository
import com.kfpd_donghaeng_fe.data.remote.api.LoginApiService

import com.kfpd_donghaeng_fe.data.remote.dto.LoginRequestDto

import com.kfpd_donghaeng_fe.data.remote.dto.toDomainLogin
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginResultEntity
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val apiService: LoginApiService
) : LoginRepository {

    override suspend fun isLoggedIn(): Boolean {
        // TODO: 실제로 토큰이 로컬에 저장되어 있는지 확인하는 로직이 들어갑니다.
        // 지금은 임시로 true 반환
        return true
    }

    suspend fun attemptLogin(email: String, password: String): LoginResultEntity{
        val request = LoginRequestDto(email, password)// LoginRequest는 API Service 파일에 정의됨
        // 실제 API 호출!
        val response = apiService.login(request)

        // TODO: 받은 토큰을 SharedPreferences나 DataStore에 저장하는 로직 추가 (data layer 역할 필요)

        return response.toDomainLogin()
    }
}







