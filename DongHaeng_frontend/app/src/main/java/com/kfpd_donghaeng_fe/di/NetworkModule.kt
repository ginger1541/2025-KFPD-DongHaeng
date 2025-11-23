package com.kfpd_donghaeng_fe.di

import com.kfpd_donghaeng_fe.data.remote.api.KakaoPlaceApiService
import com.kfpd_donghaeng_fe.data.remote.api.LoginApiService
import com.kfpd_donghaeng_fe.data.repository.PlaceRepositoryImpl
import com.kfpd_donghaeng_fe.domain.repository.PlaceRepository
import com.kfpd_donghaeng_fe.data.remote.api.MatchApiService
import com.kfpd_donghaeng_fe.data.remote.api.ChatApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  NetworkModule {

    //okhttpclient
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // 디버깅을 위해 BODY 레벨 로깅 설정
        }
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor) // 로깅 인터셉터 추가
            .connectTimeout(30, TimeUnit.SECONDS) // 연결 타임아웃
            .readTimeout(30, TimeUnit.SECONDS) // 읽기 타임아웃
            .writeTimeout(30, TimeUnit.SECONDS) // 쓰기 타임아웃
            .build()
    }

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

    // 정연 수정 부분
    @Provides
    @Singleton
    @Named("my_server")
    fun provideMyServerRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://34.64.76.147:3000/") // API 가이드 주소로 변경
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // MatchApiService 만들기
    @Provides
    @Singleton
    fun provideMatchApiService(@Named("my_server") retrofit: Retrofit): MatchApiService {
        return retrofit.create(MatchApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatApiService(@Named("my_server") retrofit: Retrofit): ChatApiService {
        return retrofit.create(ChatApiService::class.java)
    }


    //login 용!
    @Provides
    @Singleton
    fun provideLoginApiService(@Named("my_server") retrofit: Retrofit): LoginApiService {
        // 자체 서버 Retrofit 인스턴스를 사용합니다.
        return retrofit.create(LoginApiService::class.java)
    }

}