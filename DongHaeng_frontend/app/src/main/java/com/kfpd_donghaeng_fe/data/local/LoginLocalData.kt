package com.kfpd_donghaeng_fe.data.local

import android.content.SharedPreferences
import jakarta.inject.Inject

class LoginLocalData @Inject constructor(private val prefs: SharedPreferences) {
    fun getAccessToken(): String? {
        return prefs.getString("access_token", null)
    }
}