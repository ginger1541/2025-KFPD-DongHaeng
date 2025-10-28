package com.kfpd_donghaeng_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kfpd_donghaeng_fe.ui.auth.SignUpScreen
import com.kfpd_donghaeng_fe.ui.auth.UserType
import com.kfpd_donghaeng_fe.ui.dashboard.MainScreen
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KFPD_DongHaeng_FETheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "signup"
                    ) {

                        // "signup" 화면 정의
                        composable("signup") {
                            SignUpScreen(
                                onNavigateBack = {
                                    // ...
                                },
                                // userType을 받는 람다
                                onSignUpComplete = { userType ->
                                    // "home/NEEDY" 또는 "home/HELPER"로 이동
                                    navController.navigate("home/${userType.name}") {
                                        popUpTo("signup") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // "home" 화면 정의
                        composable(
                            // "home/{userType}" 형태의 주소를 받음
                            route = "home/{userType}",
                            // {userType} 인자에 대한 정보 정의
                            arguments = listOf(navArgument("userType") {
                                type = NavType.StringType // 문자열 타입
                            })
                        ) { backStackEntry ->
                            // 주소에서 userType 값 꺼내기
                            val userTypeString = backStackEntry.arguments?.getString("userType")
                            val userType = UserType.valueOf(userTypeString ?: UserType.NEEDY.name)

                            // HomeScreen에 userType 전달 (임시 화면)
                            MainScreen(userType = userType)
                        }



                    }
                }
            }
        }
    }
}