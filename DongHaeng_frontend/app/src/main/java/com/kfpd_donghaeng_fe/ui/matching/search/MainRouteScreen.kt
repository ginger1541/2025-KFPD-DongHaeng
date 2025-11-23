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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.kfpd_donghaeng_fe.viewmodel.matching.BookingViewModel // 💡 추가된 ViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainRouteScreen(
    onClose: () -> Unit,
    onNavToHome: () -> Unit,
    // 💡 [변경] MatchingViewModel -> BookingViewModel
    bookingViewModel: BookingViewModel = hiltViewModel(),
    placeSearchViewModel: PlaceSearchViewModel = hiltViewModel(),
    startSearch: Boolean = false,
    mapViewModel: MapViewModel = hiltViewModel()
) {
    // 상태 관찰
    val startLocation by placeSearchViewModel.startLocation.collectAsState()
    val endLocation by placeSearchViewModel.endLocation.collectAsState()
    val isSelectingStart by placeSearchViewModel.isSelectingStart.collectAsState()
    val mapUiState by mapViewModel.uiState.collectAsState()

    // 💡 [변경] BookingViewModel에서 단계(Phase) 상태 가져오기
    val currentPhase by bookingViewModel.currentPhase.collectAsState()

    var showPlaceSearch by remember { mutableStateOf(startSearch) }
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
            // 💡 [변경] BookingViewModel 함수 사용
            bookingViewModel.navigateToServiceType()
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
            // 💡 [연결] 출발/도착지 교체 기능
            onSwapClick = { placeSearchViewModel.swapLocations() },
            onClear = {
                placeSearchViewModel.clearAllLocations()
                onClose()
            },
            modifier = Modifier.align(Alignment.TopCenter)
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

                    if (currentPhase == MatchingPhase.BOOKING) {
                        if (routeReady) {
                            ServiceTypeSelectionContent(
                                routeReady = true,
                                onSelect = { bookingViewModel.navigateToTimeSelection() }
                            )
                        } else {
                            Column(
                                modifier = Modifier.fillMaxWidth().height(100.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("출발지와 도착지를 입력해주세요.", fontSize = 16.sp, color = Color.Gray)
                            }
                        }
                    }
                    else if (currentPhase == MatchingPhase.SERVICE_TYPE) {
                        ServiceTypeSelectionContent(
                            routeReady = true,
                            onSelect = { bookingViewModel.navigateToTimeSelection() }
                        )
                    }
                    else {
                        when (currentPhase) {
                            MatchingPhase.TIME_SELECTION -> RequestTimePicker(
                                // 💡 [변경] BookingViewModel 상태 사용
                                currentDateTime = bookingViewModel.selectedDateTime.collectAsState().value,
                                onConfirm = { newDateTime ->
                                    bookingViewModel.updateSelectedTime(newDateTime)
                                    bookingViewModel.navigateToRequestDetail()
                                },
                                onCancel = { bookingViewModel.navigateToServiceType() }
                            )
                            MatchingPhase.REQUEST_DETAIL -> RequestDetailContent(
                                onNext = bookingViewModel::navigateToPayment,
                                onBack = bookingViewModel::navigateToTimeSelection
                            )
                            MatchingPhase.PAYMENT -> PaymentContent(
                                // ⭐️⭐️⭐️ [핵심] 결제하기 버튼 클릭 시 API 호출 ⭐️⭐️⭐️
                                onPaymentClick = {
                                    val start = placeSearchViewModel.startLocation.value
                                    val end = placeSearchViewModel.endLocation.value

                                    if (start != null && end != null) {
                                        bookingViewModel.createRequest(
                                            start = start,
                                            end = end,
                                            description = "조심해서 와주세요", // TODO: 입력값 연동 필요
                                            onSuccess = {
                                                showPaymentDialog = true // 성공하면 다이얼로그 띄움
                                            },
                                            onError = { msg ->
                                                // 실패 처리 (로그나 토스트)
                                            }
                                        )
                                    }
                                },
                                onEdit = bookingViewModel::navigateToRequestDetail
                            )
                            else -> Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            },
            content = { padding ->
                Box(modifier = Modifier.fillMaxSize().padding(padding))
            }
        )
    }

    // 예약 성공 다이얼로그
    if (showPaymentDialog) {
        CommonDialog(
            title = "예약 완료",
            message = "동행 예약이 완료되었습니다.\n근처 동행자에게 알림 메시지를 보냅니다.\n\n[확인] 버튼을 누르면 홈 화면으로 이동합니다.",
            onDismiss = {
                showPaymentDialog = false
                bookingViewModel.navigateToOverview() // 초기화
                onNavToHome() // 홈으로 이동
            },
            cancelText = "확인",
            onConfirm = null
        )
    }

    // 장소 검색 화면
    if (showPlaceSearch) {
        PlaceSearchScreen(
            searchType = if (isSelectingStart) "출발지" else "도착지",
            onPlaceSelected = { place ->
                placeSearchViewModel.selectPlace(place)
                showPlaceSearch = false
                isInitialSearch = false
            },
            onBackPressed = {
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