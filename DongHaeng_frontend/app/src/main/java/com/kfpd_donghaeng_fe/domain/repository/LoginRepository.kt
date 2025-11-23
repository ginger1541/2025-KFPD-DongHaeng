package com.kfpd_donghaeng_fe.domain.repository

import com.kfpd_donghaeng_fe.domain.entity.auth.LoginResultEntity

interface LoginRepository {
    suspend fun isLoggedIn(): Boolean
    suspend fun attemptLogin(email: String, password: String): LoginResultEntity
}