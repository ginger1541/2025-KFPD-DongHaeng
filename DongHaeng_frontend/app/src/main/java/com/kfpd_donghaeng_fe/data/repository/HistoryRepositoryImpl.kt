package com.kfpd_donghaeng_fe.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoryRepositoryImpl @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : HistoryRepository {

    private val HISTORY_LIST_KEY = stringPreferencesKey("search_history_list")
    private val gson = Gson()

    override val searchHistoriesFlow: Flow<List<PlaceSearchResult>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                // I/O 에러 처리
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val jsonString = preferences[HISTORY_LIST_KEY] ?: "[]"
            // JSON 문자열을 List<PlaceSearchResult>로 복원
            val type = object : TypeToken<List<PlaceSearchResult>>() {}.type
            try {
                gson.fromJson(jsonString, type) ?: emptyList()
            } catch (e: Exception) {
                // 파싱 오류 발생 시 빈 리스트 반환
                e.printStackTrace()
                emptyList()
            }
        }

    override suspend fun saveHistories(histories: List<PlaceSearchResult>) {
        val json = gson.toJson(histories)
        dataStore.edit { preferences ->
            preferences[HISTORY_LIST_KEY] = json
        }
    }
}