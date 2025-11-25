package com.kfpd_donghaeng_fe.data.datasource

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
import com.kfpd_donghaeng_fe.domain.entity.LocationType
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject



class TokenLocalDataSourceImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : TokenLocalDataSource {

    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val USER_TYPE_KEY = stringPreferencesKey("user_type")
        private val USER_ID_KEY = longPreferencesKey("user_id")

        private val TARGET_LAT = doublePreferencesKey("target_lat")
        private val TARGET_LNG = doublePreferencesKey("target_lng")
        private val TARGET_NAME = stringPreferencesKey("target_name")
        private val TARGET_ADDRESS = stringPreferencesKey("target_address")
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
            preferences.remove(USER_ID_KEY)
            preferences.remove(USER_TYPE_KEY)
        }
    }

    override suspend fun deleteUserId() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID_KEY)
        }
    }

    override suspend fun deleteUserType() {
        dataStore.edit { preferences ->
            preferences.remove(USER_TYPE_KEY)
        }
    }

    override suspend fun saveUserType(type: String) {
        dataStore.edit { preferences ->
            preferences[USER_TYPE_KEY] = type
        }
    }

    override suspend fun saveUserId(id: Long) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = id
        }
    }

    override suspend fun getUserId(): Long? {
        return dataStore.data
            .map { preferences -> preferences[USER_ID_KEY] }
            .firstOrNull()
    }

    // ✅ [추가] 유저 타입 조회 구현
    override suspend fun getUserType(): String? {
        return dataStore.data
            .map { preferences -> preferences[USER_TYPE_KEY] }
            .firstOrNull()
    }

    override suspend fun saveTargetLocation(location: RouteLocation) {
        dataStore.edit { prefs ->
            prefs[TARGET_LAT] = location.latitude ?: 0.0
            prefs[TARGET_LNG] = location.longitude ?: 0.0
            prefs[TARGET_NAME] = location.placeName
            prefs[TARGET_ADDRESS] = location.address
        }
    }

    override fun getTargetLocationFlow(): Flow<RouteLocation?> {
        return dataStore.data.map { prefs ->
            val lat = prefs[TARGET_LAT]
            val lng = prefs[TARGET_LNG]
            val name = prefs[TARGET_NAME]
            val address = prefs[TARGET_ADDRESS]

            if (lat != null && lng != null && name != null) {
                RouteLocation(
                    id = "saved_target",
                    type = LocationType.PLACE,
                    placeName = name,
                    address = address ?: "",
                    latitude = lat,
                    longitude = lng
                )
            } else {
                null // 저장된 위치가 없으면 null 반환
            }
        }
    }
}