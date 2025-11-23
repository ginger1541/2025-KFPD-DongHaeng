package com.kfpd_donghaeng_fe.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.preferencesDataStoreFile
import com.kfpd_donghaeng_fe.data.repository.HistoryRepositoryImpl
import com.kfpd_donghaeng_fe.domain.repository.HistoryRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

// Context Extension: 애플리케이션 전체에서 DataStore 인스턴스에 접근 가능하게 합니다.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    // 팀원과 통일된 파일 이름 (이 파일 하나에 검색어, 설정 등 다 들어갑니다)
    private const val USER_PREFERENCES_NAME = "user_preferences"

    @Singleton
    @Provides
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_NAME) }
        )
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryRepositoryModule {
    @Singleton
    @Binds
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository
}