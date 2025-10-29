package com.kfpd_donghaeng_fe.di

import com.kfpd_donghaeng_fe.data.remote.api.KakaoPlaceApiService
import com.kfpd_donghaeng_fe.data.repository.PlaceRepositoryImpl
import com.kfpd_donghaeng_fe.domain.repository.PlaceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    @Named("kakao")
    fun provideKakaoRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/v2/local/search/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideKakaoPlaceApiService(
        @Named("kakao") retrofit: Retrofit
    ): KakaoPlaceApiService {
        return retrofit.create(KakaoPlaceApiService::class.java)
    }

    @Provides
    @Singleton
    fun providePlaceRepository(
        apiService: KakaoPlaceApiService
    ): PlaceRepository {
        return PlaceRepositoryImpl(apiService)
    }
}