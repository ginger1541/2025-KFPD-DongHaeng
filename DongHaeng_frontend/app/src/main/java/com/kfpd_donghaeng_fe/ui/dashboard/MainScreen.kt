package com.kfpd_donghaeng_fe.ui.dashboard


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.graphics.Color // Bar 배경색 상용
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.auth.UserType
import com.kfpd_donghaeng_fe.ui.theme.*
/*----------ongoing import ------------*/
import com.kfpd_donghaeng_fe.ui.matching.ongoing.OngoingScreen
import com.kfpd_donghaeng_fe.ui.matching.home.MatchingHomeRoute
import com.kfpd_donghaeng_fe.ui.theme.BrandOrange // 💡 테마 색상
import com.kfpd_donghaeng_fe.ui.theme.MediumGray  // 💡 테마 색상

import com.kfpd_donghaeng_fe.ui.matching.ongoing.ChattingScreen
import com.kfpd_donghaeng_fe.util.AppScreens
import com.kfpd_donghaeng_fe.util.navigateToNewSearchFlow
import com.kfpd_donghaeng_fe.util.navigateToRequestDetail
import com.kfpd_donghaeng_fe.util.navigateToReviewScreen
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingViewModel


/**
 * 하단바와 그에 연결된 화면들을 포함하는 메인 '틀'
 * @param userType 로그인한 사용자의 유형 (NEEDY or HELPER)
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(userType: UserType, mainNavController: NavHostController) {
/*
    // 1. 하단바 전용 내부 네비게이션 컨트롤러
    val bottomNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            // 2. 하단 네비게이션 바
            BottomNavBar(
                navController = bottomNavController,
                onItemClick = { route ->
                    bottomNavController.navigate(route) {
                        // 백스택 관리: 시작 목적지까지 pop
                        popUpTo(bottomNavController.graph.findStartDestination().id)
                        launchSingleTop = true // 같은 화면 중복 실행 방지
                    }
                }
            )
        }
    ) { innerPadding ->

        // 3. 하단바 '내부' 화면들
        NavHost(
            navController = bottomNavController,
            startDestination = "home", // 시작은 '홈' 화면
            modifier = Modifier.padding(innerPadding) // Scaffold의 패딩 적용
        ) {

            composable("home") {
                // Hilt ViewModel은 자동으로 주입됩니다.

                MatchingHomeRoute(
                    userType = userType, // 상위 MainScreen의 userType 인자 사용

                    // 💡 FIX: 검색 바 클릭 시 레거시 화면을 건너뛰고 새 검색 플로우로 바로 이동합니다.
                    onNavigateToSearch = { userTypeForSearch ->
                        mainNavController.navigateToNewSearchFlow(userTypeForSearch)
                    },

                    // TODO: 위치 변경 화면으로 이동 로직 구현
                    onNavigateToChangeLocation = {
                        // (예: 지도에서 위치 설정 화면으로 이동)
                        // mainNavController.navigateToChangeLocation()
                    },

                    // 최근 동행 내역 또는 주변 요청 항목 클릭 시 상세 화면으로 이동
                    onNavigateToRequestDetail = { requestId ->
                        mainNavController.navigateToRequestDetail(requestId)
                    }
                )
            }
//            // '홈' 화면
//            composable("home") {
//                HomeScreen(
//                    userType = userType,
//                    navController = mainNavController,
//                )
//            }

            composable("matching") {
                ScheduleScreen(
                    navController = mainNavController,
                )
            }

            // '채팅' 화면
            composable("chat") {
                ChattingScreen()
            }

            // '프로필' 화면
            composable("profile") {
                UserReveiwScreen()
            }
        }
    }
}

/**
 * 하단 네비게이션 바 Composable
 */
@Composable
private fun BottomNavBar(
    navController: NavHostController,
    onItemClick: (String) -> Unit
) {
    // 1. 현재 선택된 라우트(경로)를 실시간으로 추적
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 2. 하단바 아이템 리스트 정의
    val navItems = listOf(
        BottomNavItem("home", R.drawable.ic_home, "홈"),
        BottomNavItem("matching", R.drawable.ic_logo_gray, "동행"),
        BottomNavItem("chat", R.drawable.ic_chat, "채팅"),
        BottomNavItem("profile", R.drawable.ic_user, "내정보")
    )

    NavigationBar(
        // 💡 1. 바(Bar) 자체의 배경색 설정 (예: 흰색)
        containerColor = Color.White
        // 💡 (테마의 surface 색상을 사용하려면 MaterialTheme.colorScheme.surface)
    ) {
        navItems.forEach { item ->
            NavigationBarItem(
                selected = (currentRoute == item.route), // 현재 선택된 아이템인가?
                onClick = { onItemClick(item.route) }, // 클릭 시 이동
                icon = {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label) },

                // 💡 2. 아이템 색상 커스텀 (TODO 부분 활성화)
                colors = NavigationBarItemDefaults.colors(

                    selectedIconColor = BrandOrange,
                    selectedTextColor = BrandOrange,

                    unselectedIconColor = MediumGray,
                    unselectedTextColor = MediumGray,

                    indicatorColor = BrandOrange.copy(alpha = 0.1f)
                )
            )
        }
    }*/
}

// 하단바 아이템을 위한 데이터 클래스
private data class BottomNavItem(
    val route: String,
    val iconResId: Int,
    val label: String
)

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    KFPD_DongHaeng_FETheme {
        MainScreen(
            userType = UserType.NEEDY,
            mainNavController = rememberNavController()
        )
    }
}
