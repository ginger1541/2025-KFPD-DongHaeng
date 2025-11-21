package com.kfpd_donghaeng_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kfpd_donghaeng_fe.ui.auth.LoginRoute
import com.kfpd_donghaeng_fe.ui.auth.MakeAccountRoute
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import dagger.hilt.android.AndroidEntryPoint

import kotlinx.coroutines.delay

// ----------------------------------------------------
// 1. TestEntry Activity 클래스 정의
// ----------------------------------------------------
@AndroidEntryPoint
class TestEntry: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KFPD_DongHaeng_FETheme  {
                AppNavigation() // 👈 클래스 밖의 함수 호출
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            LaunchedEffect(Unit) {
                delay(1000L)
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Splash Screen Loading...")
            }
        }
        composable("login"){
            LoginRoute(onNavigateToMakeAccount = {
                navController.navigate("signup") {
                    popUpTo("login") { inclusive = true }
                }
            },)
        }

        composable("signup") {
            MakeAccountRoute()
        }
    }
}