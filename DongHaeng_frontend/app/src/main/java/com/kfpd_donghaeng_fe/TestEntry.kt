package com.kfpd_donghaeng_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.ui.auth.LoginRoute
import com.kfpd_donghaeng_fe.ui.auth.MakeAccountRoute
import com.kfpd_donghaeng_fe.ui.auth.onboarding.OnboardingScreen
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
                AppNavigation_TestLogin() // 👈 클래스 밖의 함수 호출
            }
        }
    }
}

@Composable
fun AppNavigation_TestLogin() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            LaunchedEffect(Unit) {
                delay(2000L)
                navController.navigate("login") {
                    popUpTo("splash") { inclusive = true }
                }
            }
            OnboardingScreen(
                uiState = LoginAccountUiState(),
                onNextClick = {},
                MovetoMakeAccount = {},
                page = 0 // 주황색 배경, 흰색 로고 (스플래시 디자인)
            )
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