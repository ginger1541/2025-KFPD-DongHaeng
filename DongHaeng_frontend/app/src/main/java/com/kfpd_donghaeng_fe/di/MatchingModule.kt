package com.kfpd_donghaeng_fe.di

import com.kfpd_donghaeng_fe.domain.repository.MatchingRepository
import com.kfpd_donghaeng_fe.domain.repository.fake.FakeMatchingRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object MatchingModule {

    @Provides
    @Singleton
    fun provideMatchingRepository(): MatchingRepository =
        FakeMatchingRepository()   // 나중에 Remote로 교체 가능

//    @Provides
//    @Singleton
//    fun provideGetRecentMatchingHistoryUseCase(
//        repo: MatchingRepository
//    ) = GetRecentMatchingHistoryUseCase(repo)
//
//    @Provides
//    @Singleton
//    fun provideGetNearbyRequestsUseCase(
//        repo: MatchingRepository
//    ) = GetNearbyRequestsUseCase(repo)
}
