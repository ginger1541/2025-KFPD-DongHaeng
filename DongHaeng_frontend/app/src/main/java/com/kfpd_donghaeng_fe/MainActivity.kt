package com.kfpd_donghaeng_fe

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.kfpd_donghaeng_fe.ui.dashboard.MainScreen
import com.kfpd_donghaeng_fe.ui.chat.ChatDetailScreen
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import androidx.navigation.NavHostController
import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.data.findRequestById
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.ui.matching.MatchingScreen
import com.kfpd_donghaeng_fe.ui.matching.RequestDetailScreen
import com.kfpd_donghaeng_fe.ui.matching.ReviewScreen
import com.kfpd_donghaeng_fe.ui.matching.ongoing.OngoingScreen
import com.kfpd_donghaeng_fe.util.navigateToOngoingScreen
import com.kfpd_donghaeng_fe.util.navigateToReviewScreen
import com.kfpd_donghaeng_fe.ui.auth.MakeAccountRoute
import com.kfpd_donghaeng_fe.domain.entity.auth.UserType
import com.kfpd_donghaeng_fe.domain.service.AppSettingsNavigator
import com.kfpd_donghaeng_fe.domain.service.PermissionChecker
import com.kfpd_donghaeng_fe.ui.auth.LoginRoute
import com.kfpd_donghaeng_fe.ui.auth.onboarding.OnboardingScreen
import com.kfpd_donghaeng_fe.ui.common.permission.AndroidAppSettingsNavigatorImpl
import com.kfpd_donghaeng_fe.ui.common.permission.AndroidPermissionChecker
import com.kfpd_donghaeng_fe.ui.matching.CompanionRequestDetailScreen
import com.kfpd_donghaeng_fe.util.AppScreens

import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val permissionChecker: PermissionChecker by lazy { AndroidPermissionChecker(this) }
    private val appSettingsNavigator: AppSettingsNavigator by lazy { AndroidAppSettingsNavigatorImpl(this) }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KFPD_DongHaeng_FETheme {
                Surface(
                    //modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = "home/NEEDY" // 상수 사용
                    ) {

                        // "signup" 화면 정의
                        composable("signup") {
//
                            MakeAccountRoute()
                        }

                        // "home" 화면 정의
                        composable(
                            route = AppScreens.HOME_ROUTE,

                            // {userType} 인자에 대한 정보 정의는 유지
                            arguments = listOf(navArgument("userType") {
                                type = NavType.StringType // 문자열 타입
                            })
                        ) { backStackEntry ->
                            // 주소에서 userType 값 꺼내기
                            val userTypeString = backStackEntry.arguments?.getString("userType")
                            val userType = UserType.valueOf(userTypeString ?: UserType.NEEDY.name)

                            MainScreen(
                                userType = userType,
                                mainNavController = navController // 상위 NavHostController 전달
                            )
                        }

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



                        // MATCHING 경로 정의
//                        composable(
//                            // 경로 상수 사용
//                            route = AppScreens.MATCHING_ROUTE,
//                            arguments = listOf(navArgument("userType") {
//                                type = NavType.StringType
//                            })
//                        ) { backStackEntry ->
//                            val userTypeString = backStackEntry.arguments?.getString("userType")
//                            val userType = UserType.valueOf(userTypeString ?: UserType.NEEDY.name)
//
//                            MatchingScreen(
//                                userType = userType,
//                                navController = navController,
//                                checker = permissionChecker,
//                                navigator = appSettingsNavigator
//                            )
//                        }

                        composable(
                            // 💡 [필수 수정]: startSearch 쿼리 파라미터를 경로에 추가합니다.
                            route = "${AppScreens.MATCHING_BASE}/{userType}?startSearch={startSearch}",
                            arguments = listOf(
                                navArgument("userType") {
                                    type = NavType.StringType
                                },
                                // 💡 [필수 추가]: startSearch 인자 정의를 추가합니다.
                                navArgument("startSearch") {
                                    type = NavType.BoolType
                                    defaultValue = false // 기본값은 false
                                }
                            )
                        ) { backStackEntry ->
                            val userTypeString = backStackEntry.arguments?.getString("userType")
                            val userType = UserType.valueOf(userTypeString ?: UserType.NEEDY.name)

                            // 💡 [필수 추가]: startSearch 인자 값을 읽어서 전달합니다.
                            val startSearch = backStackEntry.arguments?.getBoolean("startSearch") ?: false

                            MatchingScreen(
                                userType = userType,
                                navController = navController,
                                checker = permissionChecker,
                                navigator = appSettingsNavigator,
                                startSearch = startSearch // 인자 전달
                            )
                        }

                        // 임시 요청상세 경로
                        composable(
                            route = AppScreens.REQUEST_DETAIL_SCREEN, // "request_detail_route/{requestId}"
                            arguments = listOf(navArgument("requestId") {
                                type = NavType.LongType
                                defaultValue = -1L
                            })
                        ) { backStackEntry ->

                            // 1. 경로 인자 추출
                            val requestId = backStackEntry.arguments?.getLong("requestId") ?: -1L

                            // 2. ID를 사용하여 더미 Request 객체 찾기
                            val request = findRequestById(requestId)

                            if (request != null) {
                                RequestDetailScreen(
                                    request = request,
                                    onBackClick = { navController.popBackStack() },
                                    onAcceptClick = {
                                        navController.navigateToOngoingScreen()
                                    }
                                )
                            } else {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("요청 ID ($requestId)를 찾을 수 없습니다.")
                                }
                            }
                        }

                        composable(AppScreens.REVIEW_SCREEN) {
                            ReviewScreen(

                            )
                        }

                        composable(AppScreens.ONGOING_SCREEN) {
                            OngoingScreen(
                                onNavigateToReview = {
                                    // ReviewScreen으로 이동합니다. (스택 정리 로직은 navigateToReviewScreen 내부에 있을 수 있음)
                                    navController.navigateToReviewScreen()
                                }
                            )
                        }

                        // 채팅 상세 화면 경로 추가
                        composable(
                            route = "chat_detail/{chatRoomId}",
                            arguments = listOf(navArgument("chatRoomId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val chatRoomId = backStackEntry.arguments?.getLong("chatRoomId") ?: 0L

                            ChatDetailScreen(
                                chatRoomId = chatRoomId,
                                onBackClick = { navController.popBackStack() } // 뒤로가기
                            )
                        }

                        // 요청 상세 화면
                        composable(
                            route = "companion_request_detail/{requestId}",
                            arguments = listOf(navArgument("requestId") { type = NavType.LongType })
                        ) { backStackEntry ->
                            val requestId = backStackEntry.arguments?.getLong("requestId") ?: -1L

                            CompanionRequestDetailScreen(
                                requestId = requestId,
                                onBackClick = { navController.popBackStack() },
                                onMatchSuccess = { chatRoomId ->
                                    // 매칭 성공 시 채팅방으로 이동 (스택 정리 옵션은 선택)
                                    navController.navigate("chat_detail/$chatRoomId") {
                                        popUpTo("home/HELPER")
                                    }
                                }
                            )
                        }

                    }
                }
            }
        }
    }
}

