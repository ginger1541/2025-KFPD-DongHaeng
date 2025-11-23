package com.kfpd_donghaeng_fe.di

import com.kfpd_donghaeng_fe.data.repository.LoginRepositoryImpl
import com.kfpd_donghaeng_fe.domain.repository.LoginRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // 앱이 켜져있는 동안 계속 유지
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindLoginRepository(
        loginRepositoryImpl: LoginRepositoryImpl
    ): LoginRepository
}