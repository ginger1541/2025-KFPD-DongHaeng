package com.kfpd_donghaeng_fe.di


import com.kfpd_donghaeng_fe.data.remote.api.EndQRApiService
import com.kfpd_donghaeng_fe.data.remote.api.EndQRScanApiService
import com.kfpd_donghaeng_fe.data.remote.api.StartQRApiService
import com.kfpd_donghaeng_fe.data.remote.api.StartQRScanApiService
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