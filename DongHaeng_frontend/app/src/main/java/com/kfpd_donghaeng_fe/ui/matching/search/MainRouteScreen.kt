// main/java/com/kfpd_donghaeng_fe/ui/matching/search/MainRouteScreen.kt
package com.kfpd_donghaeng_fe.ui.matching.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.ui.common.CommonDialog
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.matching.MatchingPhase
import com.kfpd_donghaeng_fe.ui.matching.components.ServiceTypeSelectionContent
import com.kfpd_donghaeng_fe.ui.matching.components.RequestDetailContent
import com.kfpd_donghaeng_fe.ui.matching.components.PaymentContent
import com.kfpd_donghaeng_fe.ui.matching.components.PathInputBox
import com.kfpd_donghaeng_fe.ui.matching.components.RequestTimePicker
import com.kfpd_donghaeng_fe.viewmodel.matching.MapViewModel
import com.kfpd_donghaeng_fe.viewmodel.matching.PlaceSearchViewModel
import com.kfpd_donghaeng_fe.viewmodel.matching.MatchingViewModel


@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainRouteScreen(
    onClose: () -> Unit,
    onNavToHome: () -> Unit,
    matchingViewModel: MatchingViewModel = hiltViewModel(),
    placeSearchViewModel: PlaceSearchViewModel = hiltViewModel(),
    startSearch: Boolean = false,
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val startLocation by placeSearchViewModel.startLocation.collectAsState()
    val endLocation by placeSearchViewModel.endLocation.collectAsState()
    val isSelectingStart by placeSearchViewModel.isSelectingStart.collectAsState()
    val mapUiState by mapViewModel.uiState.collectAsState()
    val currentPhase by matchingViewModel.currentPhase.collectAsState()

    var showPlaceSearch by remember { mutableStateOf(startSearch) }
    // 💡 [추가] 홈에서 바로 검색으로 들어왔는지 추적하는 변수
    var isInitialSearch by remember { mutableStateOf(startSearch) }

    val routeReady = startLocation != null && endLocation != null
    var showPaymentDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (startSearch) {
            placeSearchViewModel.setSelectingTarget(isStart = true)
        }
    }

    LaunchedEffect(startLocation, endLocation) {
        if (routeReady && currentPhase == MatchingPhase.BOOKING) {
            mapViewModel.requestWalkingRoute(startLocation!!, endLocation!!)
            matchingViewModel.navigateToServiceType()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KakaoMapView(
            modifier = Modifier.fillMaxSize(),
            locationX = mapUiState.centerLocation?.longitude ?: 126.9780,
            locationY = mapUiState.centerLocation?.latitude ?: 37.5665,
            route = mapUiState.route,
            enabled = true
        )

        PathInputBox(
            startLocation = startLocation,
            endLocation = endLocation,
            isSelectingStart = isSelectingStart,
            onLocationClick = { isStart ->
                placeSearchViewModel.setSelectingTarget(isStart)
                showPlaceSearch = true
            },
            onClose = onClose,
            onClear = {
                placeSearchViewModel.clearAllLocations()
                onClose()
            },
            modifier = Modifier
                .align(Alignment.TopCenter)
            // 💡 [수정] 기존 padding 제거 -> 전체 너비 및 상단 밀착
            // .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )

        BottomSheetScaffold(
            sheetContainerColor = Color.White,
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            sheetPeekHeight = 120.dp,
            sheetShadowElevation = 10.dp,

            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 💡 [수정] 경로 입력 중일 때 안내 문구 처리 (이전 답변 내용 포함)
                    if (currentPhase == MatchingPhase.BOOKING) {
                        if (routeReady) {
                            ServiceTypeSelectionContent(
                                routeReady = true,
                                onSelect = { matchingViewModel.navigateToTimeSelection() }
                            )
                        } else {
                            // 경로 미완성 시 안내 문구
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "출발지와 도착지를 입력해주세요.",
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                    else if (currentPhase == MatchingPhase.SERVICE_TYPE) {
                        ServiceTypeSelectionContent(
                            routeReady = true,
                            onSelect = { matchingViewModel.navigateToTimeSelection() }
                        )
                    }
                    else {
                        when (currentPhase) {
                            MatchingPhase.TIME_SELECTION -> RequestTimePicker(
                                currentDateTime = matchingViewModel.selectedDateTime.value,
                                onConfirm = { newDateTime ->
                                    matchingViewModel.updateSelectedTime(newDateTime)
                                    matchingViewModel.navigateToRequestDetail()
                                },
                                onCancel = { matchingViewModel.navigateToServiceType() }
                            )
                            MatchingPhase.REQUEST_DETAIL -> RequestDetailContent(
                                onNext = matchingViewModel::navigateToPayment,
                                onBack = matchingViewModel::navigateToTimeSelection
                            )
                            MatchingPhase.PAYMENT -> PaymentContent(
                                onPaymentClick = { showPaymentDialog = true },
                                onEdit = matchingViewModel::navigateToRequestDetail
                            )
                            else -> Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            },
            content = { padding ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)) {

                    KakaoMapView(
                        modifier = Modifier.fillMaxSize(),
                        locationX = mapUiState.centerLocation?.longitude ?: 126.9780,
                        locationY = mapUiState.centerLocation?.latitude ?: 37.5665,
                        route = mapUiState.route,
                        enabled = true
                    )

                    PathInputBox(
                        startLocation = startLocation,
                        endLocation = endLocation,
                        isSelectingStart = isSelectingStart,
                        onLocationClick = { isStart ->
                            placeSearchViewModel.setSelectingTarget(isStart)
                            showPlaceSearch = true
                        },
                        onClose = onClose,
                        onClear = {
                            placeSearchViewModel.clearAllLocations()
                            onClose()
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                    )
                }
            }
        )
    }

    if (showPaymentDialog) {
        CommonDialog(
            title = "예약 완료",
            message = "동행 예약이 완료되었습니다.\n근처 동행자에게 알림 메시지를 보냅니다.\n\n[확인] 버튼을 누르면 홈 화면으로 이동합니다.",
            onDismiss = {
                showPaymentDialog = false
                matchingViewModel.navigateToOverview()
                onNavToHome()
            },
            cancelText = "확인",
            onConfirm = null
        )
    }

    if (showPlaceSearch) {
        PlaceSearchScreen(
            searchType = if (isSelectingStart) "출발지" else "도착지",
            onPlaceSelected = { place ->
                placeSearchViewModel.selectPlace(place)
                showPlaceSearch = false
                isInitialSearch = false // 💡 장소를 선택했으므로 초기 진입 상태 해제
            },
            onBackPressed = {
                // 💡 [핵심] 초기 검색 상태라면 홈으로 이동, 아니면 검색창만 닫기
                if (isInitialSearch) {
                    onNavToHome()
                } else {
                    showPlaceSearch = false
                    placeSearchViewModel.clearSearchQuery()
                }
            }
        )
    }
}