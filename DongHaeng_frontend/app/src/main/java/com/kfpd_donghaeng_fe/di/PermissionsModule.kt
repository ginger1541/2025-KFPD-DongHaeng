package com.kfpd_donghaeng_fe.di

import android.content.Context
import com.kfpd_donghaeng_fe.domain.service.AppSettingsNavigator
import com.kfpd_donghaeng_fe.domain.service.PermissionChecker
import com.kfpd_donghaeng_fe.ui.common.permission.AndroidAppSettingsNavigatorImpl
import com.kfpd_donghaeng_fe.ui.common.permission.AndroidPermissionChecker
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PermissionsModule {

    @Provides @Singleton
    fun providePermissionChecker(
        @ApplicationContext ctx: Context
    ): PermissionChecker = AndroidPermissionChecker(ctx)

    @Provides @Singleton
    fun provideAppSettingsNavigator(
        @ApplicationContext ctx: Context
    ): AppSettingsNavigator = AndroidAppSettingsNavigatorImpl(ctx)
}