package com.kfpd_donghaeng_fe.ui.matching.search

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.kfpd_donghaeng_fe.viewmodel.matching.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainRouteScreen(
    onClose: () -> Unit,
    onNavToHome: () -> Unit,
    bookingViewModel: BookingViewModel = hiltViewModel(),
    placeSearchViewModel: PlaceSearchViewModel = hiltViewModel(),
    startSearch: Boolean = false,
    mapViewModel: MapViewModel = hiltViewModel()
) {
    val startLocation by placeSearchViewModel.startLocation.collectAsState()
    val endLocation by placeSearchViewModel.endLocation.collectAsState()
    val isSelectingStart by placeSearchViewModel.isSelectingStart.collectAsState()
    val mapUiState by mapViewModel.uiState.collectAsState()
    val currentPhase by bookingViewModel.currentPhase.collectAsState()

    var showPlaceSearch by remember { mutableStateOf(startSearch) }
    var isInitialSearch by remember { mutableStateOf(startSearch) }
    val routeReady = startLocation != null && endLocation != null
    var showPaymentDialog by remember { mutableStateOf(false) }

    fun isValidLocation(loc: com.kfpd_donghaeng_fe.domain.entity.RouteLocation?): Boolean {
        return loc != null && (loc.latitude ?: 0.0) != 0.0 && (loc.longitude ?: 0.0) != 0.0
    }
    // 1. 화면 진입 시 초기화
    LaunchedEffect(Unit) {
        bookingViewModel.navigateToBooking()
        if (startSearch) {
            placeSearchViewModel.setSelectingTarget(isStart = true)
        }
    }

    LaunchedEffect(mapUiState.route) {
        mapUiState.route?.let { route ->
            // 💡 [연결] MapViewModel의 경로를 BookingViewModel에 저장
            bookingViewModel.setCalculatedRoute(route)
        }
    }

    // 2. 경로 요청 및 화면 이동
    LaunchedEffect(startLocation, endLocation) {
        if (isValidLocation(startLocation) && isValidLocation(endLocation)) {
            mapViewModel.requestWalkingRoute(startLocation!!, endLocation!!)
            bookingViewModel.navigateToServiceType()
        }
    }

    // 💡 [수정] Box 안에 Scaffold가 있는 게 아니라, Scaffold 안에 지도를 넣어야 합니다!
    BottomSheetScaffold(
        sheetContainerColor = Color.White,
        sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        sheetPeekHeight = 120.dp,
        sheetShadowElevation = 10.dp,
        // 바텀 시트 내용 (단계별 화면)
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
                } else if (currentPhase == MatchingPhase.SERVICE_TYPE) {
                    ServiceTypeSelectionContent(
                        routeReady = true,
                        onSelect = { bookingViewModel.navigateToTimeSelection() }
                    )
                } else {
                    when (currentPhase) {
                        MatchingPhase.TIME_SELECTION -> RequestTimePicker(
                            currentDateTime = bookingViewModel.selectedDateTime.collectAsState().value,
                            onConfirm = { newDateTime ->
                                bookingViewModel.updateSelectedTime(newDateTime)
                                bookingViewModel.navigateToRequestDetail()
                            },
                            onCancel = { bookingViewModel.navigateToServiceType() }
                        )
                        MatchingPhase.REQUEST_DETAIL -> {
                            // 💡 [추가] ViewModel의 description 상태를 가져옴
                            val description by bookingViewModel.requestDescription.collectAsState()

                            RequestDetailContent(
                                // 💡 [수정] 기존 컴포넌트에 text와 onTextChange 파라미터가 필요함
                                // 만약 없다면 RequestDetailContent 컴포넌트 수정 필요 (아래 참고)
                                initialDescription = description,
                                onDescriptionChange = bookingViewModel::updateDescription,

                                onNext = bookingViewModel::navigateToPayment,
                                onBack = bookingViewModel::navigateToTimeSelection
                            )
                        }
                        MatchingPhase.PAYMENT -> PaymentContent(
                            onPaymentClick = {
                                val start = placeSearchViewModel.startLocation.value
                                val end = placeSearchViewModel.endLocation.value

                                if (start != null && end != null) {
                                    // 💡 [수정] 인자 간소화 (description은 이미 ViewModel에 있음)
                                    bookingViewModel.createRequest(
                                        start = start,
                                        end = end,
                                        onSuccess = {
                                            showPaymentDialog = true
                                        },
                                        onError = { msg ->
                                            // 에러 로그 확인
                                            android.util.Log.e("BookingError", msg)
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
        // 💡 [핵심 수정] Scaffold의 content 안에 지도와 입력창을 배치합니다.
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                // 지도가 시트 뒤에도 보이게 하려면 padding을 주지 않거나 bottom만 제외할 수 있습니다.
                // 여기서는 전체 화면을 쓰도록 padding을 무시하거나 필요한 만큼만 적용합니다.
            ) {
                val targetLocation = endLocation ?: startLocation ?: mapUiState.centerLocation
                val targetLat = targetLocation?.latitude ?: 37.5665
                val targetLng = targetLocation?.longitude ?: 126.9780
                // 1. 지도 (가장 뒤)
                KakaoMapView(
                    modifier = Modifier.fillMaxSize(),
                    locationX = targetLng, // ✅ 수정된 좌표 전달
                    locationY = targetLat, // ✅ 수정된 좌표 전달
                    route = mapUiState.route,
                    enabled = true
                )

                // 2. 상단 입력창 (지도 위)
                // Scaffold의 상단 패딩(paddingValues.calculateTopPadding()) 만큼 내려서 그립니다.
                PathInputBox(
                    startLocation = startLocation,
                    endLocation = endLocation,
                    isSelectingStart = isSelectingStart,
                    onLocationClick = { isStart ->
                        placeSearchViewModel.setSelectingTarget(isStart)
                        showPlaceSearch = true
                    },
                    onClose = onClose,
                    onSwapClick = { placeSearchViewModel.swapLocations() },
                    onClear = {
                        placeSearchViewModel.clearAllLocations()
                        onClose()
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = paddingValues.calculateTopPadding()) // 상단 시스템 바 겹침 방지
                )
            }
        }
    )

    // 다이얼로그 및 검색 화면 (Scaffold 위에 뜸)
    if (showPaymentDialog) {
        CommonDialog(
            title = "예약 완료",
            message = "동행 예약이 완료되었습니다.\n근처 동행자에게 알림 메시지를 보냅니다.\n\n[확인] 버튼을 누르면 홈 화면으로 이동합니다.",
            onDismiss = {
                showPaymentDialog = false
                bookingViewModel.navigateToOverview()
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