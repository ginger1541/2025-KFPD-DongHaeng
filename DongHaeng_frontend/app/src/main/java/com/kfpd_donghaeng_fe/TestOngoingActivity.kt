package com.kfpd_donghaeng_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kfpd_donghaeng_fe.ui.auth.MakeAccountRoute
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TestOngoingActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KFPD_DongHaeng_FETheme  {
                AppNavigation_TestOngoing()
            }
        }
    }
}

@Composable
fun AppNavigation_TestOngoing() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("signup") {
            MakeAccountRoute()
        }
    }
}