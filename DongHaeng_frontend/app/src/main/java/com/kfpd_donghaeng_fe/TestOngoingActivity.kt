package com.kfpd_donghaeng_fe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kfpd_donghaeng_fe.domain.service.AppSettingsNavigator
import com.kfpd_donghaeng_fe.domain.service.PermissionChecker
import com.kfpd_donghaeng_fe.ui.common.permission.AndroidAppSettingsNavigatorImpl
import com.kfpd_donghaeng_fe.ui.common.permission.AndroidPermissionChecker
import com.kfpd_donghaeng_fe.ui.matching.ongoing.OngoingRoute
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import dagger.hilt.android.AndroidEntryPoint

/*
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

//TODO : utill 로 옮겨야함!
@Composable
fun AppNavigation_TestOngoing() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "ongoing"
    ) {
        composable("review_screen") {
            Text("Review Screen")
        }
        composable("ongoing") {
            OngoingRoute()
        }
    }
}

*/


@AndroidEntryPoint
class TestOngoingActivity : ComponentActivity() {

    // 1. 테스트를 위한 권한 도구 생성
    private val permissionChecker: PermissionChecker by lazy { AndroidPermissionChecker(this) }
    private val appSettingsNavigator: AppSettingsNavigator by lazy { AndroidAppSettingsNavigatorImpl(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KFPD_DongHaeng_FETheme {
                // 2. 도구들을 전달
                AppNavigation_TestOngoing(
                    permissionChecker = permissionChecker,
                    appSettingsNavigator = appSettingsNavigator
                )
            }
        }
    }
}

@Composable
fun AppNavigation_TestOngoing(
    permissionChecker: PermissionChecker,
    appSettingsNavigator: AppSettingsNavigator
) {
    val navController = rememberNavController()

    // 테스트용 임시 ID
    val testMatchId = 5L

    NavHost(
        navController = navController,
        startDestination = "ongoing"
    ) {
        // 1. 동행 중 화면 (Ongoing)
        composable("ongoing") {
            OngoingRoute(
                matchId = testMatchId, // 테스트용 ID 전달
                navController = navController,
                permissionChecker = permissionChecker,
                appSettingsNavigator = appSettingsNavigator
            )
        }

        // 2. 리뷰 작성 화면 (Review) - 이동 테스트용 더미
        // 실제 경로는 "review_route/{matchId}/{partnerId}" 형태임
        composable(
            route = "review_route/{matchId}/{partnerId}",
            arguments = listOf(
                navArgument("matchId") { type = NavType.LongType },
                navArgument("partnerId") { type = NavType.LongType }
            )
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("리뷰 작성 화면으로 이동 성공!\n(Test Mode)")
            }
        }

        // 3. 홈 화면 (Home) - 이동 테스트용 더미
        composable("home") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("홈 화면으로 복귀함")
            }
        }
    }
}