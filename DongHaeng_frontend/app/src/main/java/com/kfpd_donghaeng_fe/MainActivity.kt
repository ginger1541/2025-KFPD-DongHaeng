package com.kfpd_donghaeng_fe

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

import com.kfpd_donghaeng_fe.ui.dashboard.MainScreen
import com.kfpd_donghaeng_fe.ui.chat.ChatDetailScreen
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import com.kfpd_donghaeng_fe.ui.theme.MainOrange
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
import com.kfpd_donghaeng_fe.viewmodel.SplashViewModel

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
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // SplashViewModel 주입
                    val splashViewModel: SplashViewModel = hiltViewModel()
                    val startDest by splashViewModel.startDestination.collectAsState()

                    // startDest가 결정되기 전에는 로딩 화면 표시
                    if (startDest == null) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = MainOrange)
                        }
                    } else {
                        NavHost(
                            navController = navController,
                            startDestination = startDest!! // 동적으로 결정된 시작 경로
                        ) {
                            // "signup" 화면 정의
                            composable("signup") {
                                MakeAccountRoute()
                            }

                            // "home" 화면 정의
                            composable(
                                route = AppScreens.HOME_ROUTE,
                                arguments = listOf(navArgument("userType") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val userTypeString = backStackEntry.arguments?.getString("userType")
                                val userType = UserType.valueOf(userTypeString ?: UserType.NEEDY.name)

                                MainScreen(
                                    userType = userType,
                                    mainNavController = navController
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
                                    page = 0
                                )
                            }

                            composable("login") {
                                LoginRoute(
                                    // 회원가입 화면으로 이동
                                    onNavigateToMakeAccount = {
                                        navController.navigate("signup") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    },
                                    // ✅ [추가] 로그인 성공 시 홈 화면으로 이동!
                                    onLoginSuccess = {
                                        // 로그인 스택을 지우고 홈으로 이동 (뒤로가기 방지)
                                        navController.navigate("home/NEEDY") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // MATCHING 경로 정의
                            composable(
                                route = "${AppScreens.MATCHING_BASE}/{userType}?startSearch={startSearch}",
                                arguments = listOf(
                                    navArgument("userType") {
                                        type = NavType.StringType
                                    },
                                    navArgument("startSearch") {
                                        type = NavType.BoolType
                                        defaultValue = false
                                    }
                                )
                            ) { backStackEntry ->
                                val userTypeString = backStackEntry.arguments?.getString("userType")
                                val userType = UserType.valueOf(userTypeString ?: UserType.NEEDY.name)
                                val startSearch = backStackEntry.arguments?.getBoolean("startSearch") ?: false

                                MatchingScreen(
                                    userType = userType,
                                    navController = navController,
                                    checker = permissionChecker,
                                    navigator = appSettingsNavigator,
                                    startSearch = startSearch
                                )
                            }

                            // 임시 요청상세 경로
                            composable(
                                route = AppScreens.REQUEST_DETAIL_SCREEN,
                                arguments = listOf(navArgument("requestId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                })
                            ) { backStackEntry ->
                                val requestId = backStackEntry.arguments?.getLong("requestId") ?: -1L
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
                                ReviewScreen()
                            }

                            composable(AppScreens.ONGOING_SCREEN) {
                                OngoingScreen(
                                    onNavigateToReview = {
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
                                    onBackClick = { navController.popBackStack() }
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
}