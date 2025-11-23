package com.kfpd_donghaeng_fe.ui.dashboard


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
/*----------ongoing import ------------*/
//import com.kfpd_donghaeng_fe.ui.matching.ongoing.OngoingScreen
// 💡 필요한 import 구문들을 추가합니다.
import com.kfpd_donghaeng_fe.domain.entity.auth.UserType


/**
 * 하단바와 그에 연결된 화면들을 포함하는 메인 '틀'
 * @param userType 로그인한 사용자의 유형 (NEEDY or HELPER)
 */
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
            // '홈' 화면
            composable("home") {
                HomeScreen(
                    userType = userType,
                    navController = mainNavController,
                )
            }

            // '동행(미션)' 화면
            composable("mission") {
                OngoingScreen(
                    onNavigateToReview = {
                        // ReviewScreen으로 이동합니다. (스택 정리 로직은 navigateToReviewScreen 내부에 있을 수 있음)
                        bottomNavController.navigateToReviewScreen()
                    }
                )
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
        BottomNavItem("mission", R.drawable.ic_logo_gray, "동행"),
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



