package com.kfpd_donghaeng_fe.di

import com.kfpd_donghaeng_fe.data.repository.RouteRepositoryImpl
import com.kfpd_donghaeng_fe.domain.repository.RouteRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class) // ViewModel에서 사용되므로 ViewModelComponent에 설치
abstract class RepositoryModule {

    /**
     * RouteRepository 인터페이스에 대한 구현체로 RouteRepositoryImpl을 바인딩합니다.
     * Hilt는 RouteRepositoryImpl의 생성자(@Inject)를 보고 필요한 SKRouteApiService를
     * NetworkModule에서 주입받아 인스턴스를 생성해 줍니다.
     */
    @Binds
    @ViewModelScoped // ViewModel의 생명주기와 일치
    abstract fun bindRouteRepository(
        routeRepositoryImpl: RouteRepositoryImpl
    ): RouteRepository
}