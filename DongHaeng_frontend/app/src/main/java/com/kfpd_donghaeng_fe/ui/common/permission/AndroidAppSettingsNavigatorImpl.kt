package com.kfpd_donghaeng_fe.ui.common.permission

import android.content.Intent
import android.net.Uri
import com.kfpd_donghaeng_fe.domain.service.AppSettingsNavigator
import android.provider.Settings

class AndroidAppSettingsNavigatorImpl(
    private val context: android.content.Context
) : AppSettingsNavigator {
    override fun openAppSettings() {
        val intent = Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", context.packageName, null)
        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}