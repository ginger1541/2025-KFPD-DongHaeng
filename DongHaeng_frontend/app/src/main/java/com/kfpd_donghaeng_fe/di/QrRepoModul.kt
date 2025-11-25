package com.kfpd_donghaeng_fe.di


import com.kfpd_donghaeng_fe.data.remote.api.EndQRApiService
import com.kfpd_donghaeng_fe.data.remote.api.EndQRScanApiService
import com.kfpd_donghaeng_fe.data.remote.api.StartQRApiService
import com.kfpd_donghaeng_fe.data.remote.api.StartQRScanApiService

import com.kfpd_donghaeng_fe.data.repository.OngoingQRRepositoryImpl
import com.kfpd_donghaeng_fe.domain.repository.OngoingInfoRepo
import com.kfpd_donghaeng_fe.domain.repository.OngoingQRRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QRApiServiceModule {

    // 1. 매칭 시작 QR 정보 조회 서비스
    @Provides
    @Singleton
    fun provideStartQRApiService(retrofit: Retrofit): StartQRApiService {
        return retrofit.create(StartQRApiService::class.java)
    }

    // 2. 매칭 종료 QR 정보 조회 서비스
    @Provides
    @Singleton
    fun provideEndQRApiService(retrofit: Retrofit): EndQRApiService {
        return retrofit.create(EndQRApiService::class.java)
    }

    // 3. 매칭 시작 QR 스캔 요청 서비스
    @Provides
    @Singleton
    fun provideStartQRScanApiService(retrofit: Retrofit): StartQRScanApiService {
        return retrofit.create(StartQRScanApiService::class.java)
    }

    // 4. 매칭 종료 QR 스캔 요청 서비스
    @Provides
    @Singleton
    fun provideEndQRScanApiService(retrofit: Retrofit): EndQRScanApiService {
        return retrofit.create(EndQRScanApiService::class.java)
    }


}


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindOngoingQRRepository(
        ongoingQRRepositoryImpl: OngoingQRRepositoryImpl // 💡 Hilt가 이 구현체의 생성자를 찾아서 생성합니다.
    ): OngoingQRRepository
}


