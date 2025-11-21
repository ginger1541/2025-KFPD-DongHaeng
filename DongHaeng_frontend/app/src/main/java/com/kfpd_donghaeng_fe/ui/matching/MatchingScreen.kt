package com.kfpd_donghaeng_fe.ui.matching

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.DropdownMenu
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.TextButton
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import com.kfpd_donghaeng_fe.domain.entity.LocationAccuracy
import com.kfpd_donghaeng_fe.domain.entity.LocationPermissionState
import com.kfpd_donghaeng_fe.domain.service.AppSettingsNavigator
import com.kfpd_donghaeng_fe.domain.service.PermissionChecker
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.common.permission.rememberLocationPermissionRequester
import com.kfpd_donghaeng_fe.ui.matching.components.SearchBar
import com.kfpd_donghaeng_fe.ui.matching.components.SheetHandleBar
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kfpd_donghaeng_fe.ui.auth.UserType
import com.kfpd_donghaeng_fe.ui.matching.componentes.BottomMatchingSheetContent
import com.kfpd_donghaeng_fe.util.navigateToOngoingScreen
import com.kfpd_donghaeng_fe.viewmodel.matching.MatchingViewModel
import com.kfpd_donghaeng_fe.ui.matching.search.MainRouteScreen
import com.kfpd_donghaeng_fe.util.AppScreens

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MatchingScreen(
    userType: UserType,
    navController: NavHostController,
    checker: PermissionChecker,
    navigator: AppSettingsNavigator,
    matchingViewModel: MatchingViewModel = hiltViewModel(),
    startSearch: Boolean = false
) {
    val bottomSheetState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
            initialValue = BottomSheetValue.Expanded
        )
    )

    LaunchedEffect(Unit) {
        // startSearch=true이면 즉시 BOOKING Phase로 전환
        if (startSearch) {
            matchingViewModel.navigateToBooking(isDirectSearch = startSearch)
        }
    }

    var showPermissionAlert by remember { mutableStateOf(false) }

    val requester = rememberLocationPermissionRequester(checker, navigator)
    val permissionState = requester.state.value
    val rationaleNeeded = requester.shouldShowRationale.value

    val currentPhase by matchingViewModel.currentPhase.collectAsState()

    // 💡 MainRouteScreen이 지도와 하단 시트 모두를 관리하므로,
    //    BOOKING/SERVICE_TYPE 등의 경로 설정 플로우는 MainRouteScreen이 Full Screen으로 덮습니다.
    if (userType == UserType.NEEDY && (currentPhase != MatchingPhase.OVERVIEW)) {
        MainRouteScreen(
            onClose = matchingViewModel::navigateToOverview,
            onNavToHome = {
                // 💡 최종 예약 완료 후 Home 화면으로 이동 (MatchingScreen을 스택에서 제거)
                val homeRoute = AppScreens.HOME_ROUTE.replace("{userType}", userType.name)

                // MatchingScreen을 pop하고 Home으로 이동
                navController.navigate(homeRoute) {
                    // 현재 스택의 MATCHING_ROUTE를 pop하여 제거
                    popUpTo(AppScreens.MATCHING_ROUTE.replace("{userType}", userType.name)) {
                        inclusive = true
                    }
                }
            }
        )
        return
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val fullHeight = maxHeight
        val peekHeight = 150.dp
        val expandedHeight = fullHeight * (2f / 3f)

        BottomSheetScaffold(
            scaffoldState = bottomSheetState,
            sheetShape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
            sheetPeekHeight = peekHeight,
            sheetBackgroundColor = AppColors.CardBackground,
            sheetElevation = 4.dp,

            sheetContent = {
                Column(
                    modifier = Modifier
                        .heightIn(max = expandedHeight)
                        .fillMaxWidth()
                ) {
                    SheetHandleBar()
                    BottomMatchingSheetContent(
                        modifier = Modifier.fillMaxWidth(),
                        role = userType,
                        navController = navController,
                        onNavigateToOngoing = {
                            // TODO: 온고잉이 아닐걸~
                            navController.navigateToOngoingScreen()
                        }
                    )
                }
            },

            content = { paddingValues ->

                when {
                    permissionState.isGranted -> {
                        MapContent(
                            paddingValues = paddingValues,
                            enabled = true
                        )
                    }

                    rationaleNeeded -> {

                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("지도를 사용하려면 위치 권한이 필요합니다.")
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = requester.request) { Text("권한 허용") }
                        }
                    }

                    else -> {
                        // 3. ✅ 처음 권한 요청이 필요한 상태
                        // 화면이 처음 로드되면 바로 권한 요청 다이얼로그를 띄우도록 설정
                        LaunchedEffect(Unit) {
                            showPermissionAlert = true
                        }

                        // 지도가 아닌 배경 화면만 표시
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("위치 권한 요청을 기다리는 중...", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        )
    }

    if (showPermissionAlert && !permissionState.isGranted && !rationaleNeeded) {
        AlertDialog(
            onDismissRequest = {
                // 취소 시, 설정으로 이동할지 여부 등을 고려할 수 있으나, 일단 닫고 대기
                showPermissionAlert = false
            },
            title = { Text("위치 권한 필요") },
            text = { Text("동행 요청 및 주변 요청 확인을 위해 위치 권한이 필요합니다. 지금 권한을 요청합니다.") },
            confirmButton = {
                Button(
                    onClick = {
                        showPermissionAlert = false
                        requester.request()
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showPermissionAlert = false
                        navigator.openAppSettings()
                    }
                ) {
                    Text("설정으로 이동")
                }
            }
        )
    }
}

@Composable
fun MapContent(
    paddingValues: PaddingValues,
    enabled: Boolean,
    locationX: Double = 126.9780,
    locationY: Double = 37.5665
) {
    // 정렬 상태 관리
    var selectedSort by remember { mutableStateOf("거리순") }
    var showSortMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        // 지도는 맨 뒤
        KakaoMapView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            locationY = locationY,
            locationX = locationX,
            enabled = enabled
        )

        // 오버레이: 검색바 + 정렬 버튼
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 8.dp)
                .zIndex(1f)
        ) {
            // 검색바 (상단 중앙, 전체폭)
            SearchBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 정렬 버튼 (검색바 바로 아래, 오른쪽 정렬)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 16.dp)
                    .wrapContentSize(Alignment.TopEnd) // 이 부분 추가
            ) {
                Row(
                    modifier = Modifier
                        .background(
                            color = AppColors.CardBackground,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { showSortMenu = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = selectedSort,
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.PrimaryDarkText
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.Default.KeyboardArrowDown,
                        contentDescription = "정렬 선택",
                        tint = AppColors.PrimaryDarkText.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // 드롭다운
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("거리순") },
                        onClick = {
                            selectedSort = "거리순"
                            showSortMenu = false
                            // TODO: 거리순 정렬 로직 추가
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("시간순") },
                        onClick = {
                            selectedSort = "시간순"
                            showSortMenu = false
                            // TODO: 시간순 정렬 로직 추가
                        }
                    )
                }
            }
        }
    }
}