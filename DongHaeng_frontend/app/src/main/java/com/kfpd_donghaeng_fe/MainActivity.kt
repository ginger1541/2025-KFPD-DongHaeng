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
import com.kfpd_donghaeng_fe.ui.matching.PreFilledRouteData
import com.kfpd_donghaeng_fe.util.AppScreens
import com.kfpd_donghaeng_fe.viewmodel.SplashViewModel
import com.kfpd_donghaeng_fe.viewmodel.matching.RequesterDetailViewModel

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
                                    // ✅ [수정] 서버에서 받은 userType을 앱의 UserType(NEEDY/HELPER)으로 변환하여 이동
                                    onLoginSuccess = { userTypeString ->
                                        // 1. 대문자로 변환 및 매핑 ("requester" -> "NEEDY")
                                        val normalizedType = when (userTypeString.uppercase()) {
                                            "HELPER" -> "HELPER"
                                            "REQUESTER", "NEEDY" -> "NEEDY" // 서버가 requester로 보내면 NEEDY로 처리
                                            else -> "NEEDY" // 알 수 없는 값이면 기본값 NEEDY (또는 에러 처리)
                                        }

                                        // 2. 변환된 타입으로 홈 화면 이동
                                        navController.navigate("home/$normalizedType") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    }
                                )
                            }

                            // MATCHING 경로 정의
                            composable(
                                // 1. URL 구조 정의: 기본 인자 + 선택적 쿼리 파라미터들(?key=value 형태)
                                route = "${AppScreens.MATCHING_BASE}/{userType}?startSearch={startSearch}&startName={startName}&startLat={startLat}&startLng={startLng}&endName={endName}&endLat={endLat}&endLng={endLng}",

                                // 2. 인자 타입 및 기본값 설정
                                arguments = listOf(
                                    navArgument("userType") { type = NavType.StringType },
                                    navArgument("startSearch") { type = NavType.BoolType; defaultValue = false },

                                    // 데이터 전달용 인자들 (없을 수도 있으므로 nullable 처리)
                                    navArgument("startName") { type = NavType.StringType; nullable = true },
                                    navArgument("startLat") { type = NavType.FloatType; defaultValue = 0f },
                                    navArgument("startLng") { type = NavType.FloatType; defaultValue = 0f },
                                    navArgument("endName") { type = NavType.StringType; nullable = true },
                                    navArgument("endLat") { type = NavType.FloatType; defaultValue = 0f },
                                    navArgument("endLng") { type = NavType.FloatType; defaultValue = 0f },
                                )
                            ) { backStackEntry ->
                                // 3. 인자 값 꺼내기
                                val userTypeString = backStackEntry.arguments?.getString("userType")
                                val userType = UserType.valueOf(userTypeString ?: UserType.NEEDY.name)
                                val startSearch = backStackEntry.arguments?.getBoolean("startSearch") ?: false

                                // 전달된 경로 데이터 꺼내기
                                val startName = backStackEntry.arguments?.getString("startName")
                                val startLat = backStackEntry.arguments?.getFloat("startLat") ?: 0f
                                val startLng = backStackEntry.arguments?.getFloat("startLng") ?: 0f
                                val endName = backStackEntry.arguments?.getString("endName") ?: ""
                                val endLat = backStackEntry.arguments?.getFloat("endLat") ?: 0f
                                val endLng = backStackEntry.arguments?.getFloat("endLng") ?: 0f

                                // 4. 데이터 객체 생성 (startName이 있을 때만 만듦)
                                val preFilledData = if (startName != null) {
                                    PreFilledRouteData(
                                        startName = startName,
                                        startLat = startLat.toDouble(),
                                        startLng = startLng.toDouble(),
                                        endName = endName,
                                        endLat = endLat.toDouble(),
                                        endLng = endLng.toDouble()
                                    )
                                } else null

                                // 5. 화면 렌더링 (데이터 전달)
                                MatchingScreen(
                                    userType = userType,
                                    navController = navController,
                                    checker = permissionChecker,
                                    navigator = appSettingsNavigator,
                                    startSearch = startSearch,
                                    preFilledData = preFilledData // ✅ 여기서 전달!
                                )
                            }

                            // 요청자 홈 요청 상세
                            composable(
                                route = AppScreens.REQUEST_DETAIL_SCREEN,
                                arguments = listOf(navArgument("requestId") {
                                    type = NavType.LongType
                                    defaultValue = -1L
                                })
                            ) { backStackEntry ->
                                val requestId = backStackEntry.arguments?.getLong("requestId") ?: -1L

                                // 1. 뷰모델 주입
                                val viewModel: RequesterDetailViewModel = hiltViewModel()

                                // 2. 화면 진입 시 데이터 로드 (한 번만 실행)
                                LaunchedEffect(requestId) {
                                    if (requestId != -1L) {
                                        viewModel.loadRequest(requestId)
                                    }
                                }

                                // 3. 상태 관찰
                                val request by viewModel.uiState.collectAsState()
                                val isLoading by viewModel.isLoading.collectAsState()

                                // 4. UI 표시 분기 처리
                                if (isLoading) {
                                    // 로딩 중일 때
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        CircularProgressIndicator(color = MainOrange)
                                    }
                                } else if (request != null) {
                                    // 데이터 로드 성공 시 화면 표시
                                    RequestDetailScreen(
                                        request = request!!,
                                        onBackClick = { navController.popBackStack() },
                                        onAcceptClick = {
                                            // 요청자는 '수락' 대신 '상태 확인'이나 다른 동작이 필요할 수 있습니다.
                                            // 일단은 화면 이동 없이 두거나, 필요 시 구현
                                            navController.navigateToOngoingScreen()
                                        }
                                    )
                                } else {
                                    // 데이터가 없을 때 (에러 또는 로드 실패)
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("요청 정보를 찾을 수 없습니다.")
                                    }
                                }
                            }

                            // 채팅 상세 화면 경로 추가
                            composable(
                                route = "chat_detail/{chatRoomId}",
                                arguments = listOf(navArgument("chatRoomId") {
                                    type = NavType.LongType
                                    // ⚠️ defaultValue = 0L 을 제거하거나,
                                    // 문제가 발생할 경우를 대비하여 여기서 유효성 검사
                                })
                            ) { backStackEntry ->
                                // 💡 [수정] null일 경우 에러 처리 대신 -1L 등의 명확한 값 사용
                                val chatRoomId = backStackEntry.arguments?.getLong("chatRoomId") ?: -1L

                                // 💡 [추가] chatRoomId가 유효하지 않으면 화면을 표시하지 않음
                                if (chatRoomId > 0L) {
                                    ChatDetailScreen(
                                        chatRoomId = chatRoomId,
                                        onBackClick = { navController.popBackStack() }
                                    )
                                } else {
                                    // 유효하지 않은 ID 처리 (예: 텍스트 표시)
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("유효하지 않은 채팅방 ID입니다.")
                                    }
                                }
                            }

                            // 동행자 홈화면 - 요청 상세 화면
                            composable(
                                route = "companion_request_detail/{requestId}",
                                arguments = listOf(navArgument("requestId") { type = NavType.LongType })
                            ) { backStackEntry ->
                                val requestId = backStackEntry.arguments?.getLong("requestId") ?: -1L

                                CompanionRequestDetailScreen(
                                    requestId = requestId,
                                    onBackClick = { navController.popBackStack() },

                                    // ✅ 매칭 성공 시 채팅방으로 이동하는 로직
                                    onMatchSuccess = { chatRoomId ->
                                        // chatRoomId를 가지고 채팅 상세 화면으로 이동
                                        navController.navigate("chat_detail/$chatRoomId") {
                                            // 뒤로가기 했을 때 다시 요청 상세 화면이 나오지 않도록 스택 정리
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