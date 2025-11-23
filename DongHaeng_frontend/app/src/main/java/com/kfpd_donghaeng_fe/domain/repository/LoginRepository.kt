package com.kfpd_donghaeng_fe.domain.repository

interface LoginRepository {
    suspend fun isLoggedIn(): Boolean
}