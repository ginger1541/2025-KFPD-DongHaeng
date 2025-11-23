package com.kfpd_donghaeng_fe.di

import com.kfpd_donghaeng_fe.data.remote.api.AuthInterceptor
import com.kfpd_donghaeng_fe.data.remote.api.KakaoPlaceApiService
import com.kfpd_donghaeng_fe.data.remote.api.LoginApiService
import com.kfpd_donghaeng_fe.data.remote.api.SKRouteApiService
import com.kfpd_donghaeng_fe.data.repository.PlaceRepositoryImpl
import com.kfpd_donghaeng_fe.domain.repository.PlaceRepository
import com.kfpd_donghaeng_fe.data.remote.api.MatchApiService
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
object NetworkModule {

    // Kakao API용 OkHttpClient
    @Provides
    @Singleton
    @Named("kakao")
    fun provideKakaoOkHttpClient(
        kakaoAuthInterceptor: KakaoAuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(kakaoAuthInterceptor) // Kakao 전용
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    //TODO: 백엔드 연결용
    @Provides
    @Singleton
    @Named("backend")
    fun provideBackendOkHttpClient(
        // backendAuthInterceptor: BackendAuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // .addInterceptor(backendAuthInterceptor)
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()
    }

    private const val KAKAO_BASE_URL = "https://dapi.kakao.com/"
    private const val SK_ROUTE_BASE_URL = "https://apis.openapi.sk.com/"

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
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            // 💡 이전에 오류를 낸 'http' 대신 'https'를 사용해야 합니다!
            .baseUrl("http://34.64.76.147:3000")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor, // 토근용
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            // 💡 AuthInterceptor가 반드시 OkHttpClient에 추가되어야 합니다.
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            // ...
            .build()
    }

    @Provides
    @Singleton
    @Named("kakao")
    fun provideKakaoRetrofit(
        @Named("kakao") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(KAKAO_BASE_URL)
            .client(okHttpClient)
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

    // 정연 서버용 Retrofit
    @Provides
    @Singleton
    @Named("my_server") // 이름표 붙이기
    fun provideMyServerRetrofit(): Retrofit {
        return Retrofit.Builder()
            //.baseUrl("http://10.0.2.2:8080/") // 에뮬레이터용 로컬 주소
            .baseUrl("http://34.64.76.147:3000")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 2. MatchApiService 만들기
    @Provides
    @Singleton
    fun provideMatchApiService(@Named("my_server") retrofit: Retrofit): MatchApiService {
        return retrofit.create(MatchApiService::class.java)
    }


    //login 용!
    @Provides
    @Singleton
    fun provideLoginApiService(@Named("my_server") retrofit: Retrofit): LoginApiService {
        // 자체 서버 Retrofit 인스턴스를 사용합니다.
        return retrofit.create(LoginApiService::class.java)
    }


    @Provides
    @Singleton
    @Named("sk_route")
    fun provideSkRouteRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(SK_ROUTE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideSKRouteApiService(
        @Named("sk_route") retrofit: Retrofit
    ): SKRouteApiService {
        return retrofit.create(SKRouteApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideKakaoAuthInterceptor(): KakaoAuthInterceptor {
        return KakaoAuthInterceptor()
    }
}