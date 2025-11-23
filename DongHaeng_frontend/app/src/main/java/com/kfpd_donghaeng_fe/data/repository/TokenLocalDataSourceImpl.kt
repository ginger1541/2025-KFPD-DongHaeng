package com.kfpd_donghaeng_fe.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject



class TokenLocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenLocalDataSource {

    companion object {
        // stringPreferencesKey 임포트 필요
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    override suspend fun saveToken(token: String) {
        // edit 임포트 필요
        dataStore.edit { preferences ->
            preferences[AUTH_TOKEN_KEY] = token // set 연산자 사용 (임포트 필요)
        }
    }

    override suspend fun getToken(): String? {
        return dataStore.data
            // map 임포트 필요
            .map { preferences ->
                preferences[AUTH_TOKEN_KEY] // get 연산자 사용 (임포트 필요)
            }
            // firstOrNull 임포트 필요
            .firstOrNull()
    }

    override suspend fun deleteToken() {
        dataStore.edit { preferences ->
            preferences.remove(AUTH_TOKEN_KEY)
        }
    }
}