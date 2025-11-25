package com.kfpd_donghaeng_fe.data.local




// domain/datasource/TokenLocalDataSource.kt (인터페이스)
interface TokenLocalDataSource {
    suspend fun saveToken(token: String)
    suspend fun getToken(): String?
    suspend fun deleteToken()

    suspend fun saveUserType(type: String)
    suspend fun getUserType(): String?
    suspend fun saveUserId(id: Long)
    suspend fun getUserId(): Long?

    suspend fun deleteUserId()
    suspend fun deleteUserType()
}






