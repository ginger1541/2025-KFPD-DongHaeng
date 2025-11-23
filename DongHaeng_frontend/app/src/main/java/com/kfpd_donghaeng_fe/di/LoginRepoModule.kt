package com.kfpd_donghaeng_fe.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.kfpd_donghaeng_fe.data.datasource.TokenLocalDataSourceImpl
import com.kfpd_donghaeng_fe.data.local.TokenLocalDataSource
import com.kfpd_donghaeng_fe.data.repository.LoginRepositoryImpl
import com.kfpd_donghaeng_fe.domain.repository.LoginRepository

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

@Module
@InstallIn(SingletonComponent::class) // 앱의 생명주기 동안 단일 인스턴스를 유지
abstract class LoginRepoModuleModule {

    /**
     * Hilt에게 TokenLocalDataSource 인터페이스가 요청될 때,
     * TokenLocalDataSourceImpl 구현체를 제공하도록 알려줍니다.
     */
    @Binds
    @Singleton
    abstract fun bindTokenLocalDataSource(
        tokenLocalDataSourceImpl: TokenLocalDataSourceImpl // Hilt가 이 구현체를 생성할 수 있습니다.
    ): TokenLocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {

    private const val USER_PREFERENCES_NAME = "user_preferences" // 👈 DataStore 파일 이름

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(
            // DataStore의 초기화와 파일 생성 방법을 정의합니다.
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()), // Coroutine Scope 설정
            produceFile = { context.preferencesDataStoreFile(USER_PREFERENCES_NAME) } // 파일 이름 지정
        )
    }
}


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Hilt에게 LoginRepository 인터페이스가 요청될 때,
     * LoginRepositoryImpl 구현체를 제공하도록 알려줍니다.
     */
    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl // Hilt는 이 구현체를 만들 수 있습니다 (이미 의존성 경로 완성)
    ): LoginRepository
}