// main/java/com/kfpd_donghaeng_fe/ui/matching/search/MainRouteScreen.kt (수정됨)
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.ui.common.CommonDialog
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.matching.MatchingPhase
import com.kfpd_donghaeng_fe.ui.matching.components.SheetHandleBar
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
    onNavToHome: () -> Unit, // 💡 최종 완료 후 Home 화면으로 이동 요청
    matchingViewModel: MatchingViewModel = hiltViewModel(),
    placeSearchViewModel: PlaceSearchViewModel = hiltViewModel(),
    startSearch: Boolean = false,
    mapViewModel: MapViewModel = hiltViewModel()
) {
    // ViewModel 상태 수집
    val startLocation by placeSearchViewModel.startLocation.collectAsState()
    val endLocation by placeSearchViewModel.endLocation.collectAsState()
    val isSelectingStart by placeSearchViewModel.isSelectingStart.collectAsState()
    val mapUiState by mapViewModel.uiState.collectAsState()
    val currentPhase by matchingViewModel.currentPhase.collectAsState()

    // 경로 설정 메인 화면에서 사용할 내부 UI 상태 (검색 화면 표시 여부)
    var showPlaceSearch by remember { mutableStateOf(startSearch) }

    val routeReady = startLocation != null && endLocation != null

    var showPaymentDialog by remember { mutableStateOf(false) }

    // 최종 예약 완료 로직 Phase 리셋 후 홈으로 이동 요청
    val handleBookingCompletion = {
        // TODO: 여기서 서버로 최종 예약 API 호출 로직이 들어갑니다. (현재 주석 처리)
        println("// TODO: 요청 생성 및 채팅방 생성 API 호출")
        matchingViewModel.navigateToOverview() // 1. Phase를 OVERVIEW로 리셋
        onNavToHome()                        // 2. NavController를 사용해 Home으로 이동
    }

    LaunchedEffect(Unit) {
        if (startSearch) {
            placeSearchViewModel.setSelectingTarget(isStart = true)
        }
    }

    // MapViewModel과 PlaceSearchViewModel 상태 연동 (경로 요청 트리거)
    LaunchedEffect(startLocation, endLocation) {
        if (routeReady && currentPhase == MatchingPhase.BOOKING) {
            // 경로 요청을 MapViewModel로 위임
            mapViewModel.requestWalkingRoute(startLocation!!, endLocation!!)

            // 출발/도착지 선택 완료 후, 다음 단계(서비스 유형 선택)로 자동 전환
            matchingViewModel.navigateToServiceType()
        }
    }


    // ==========================================================
    // 메인 경로 설정 화면 (Map + Bottom Sheet/Input)
    // ==========================================================

    Box(modifier = Modifier.fillMaxSize()) {
        // 지도 뷰
        KakaoMapView(
            modifier = Modifier.fillMaxSize(),
            // 현재 위치 또는 경로의 중심 좌표를 지도 중심 좌표로 사용
            locationX = mapUiState.centerLocation?.longitude ?: 126.9780,
            locationY = mapUiState.centerLocation?.latitude ?: 37.5665,
            // 💡 [수정] ViewModel에서 받아온 경로 데이터를 전달합니다.
            route = mapUiState.route,
            enabled = true
        )

        // 상단 경로 입력
        PathInputBox(
            startLocation = startLocation,
            endLocation = endLocation,
            isSelectingStart = isSelectingStart,
            onLocationClick = { isStart ->
                placeSearchViewModel.setSelectingTarget(isStart) // 출발지/도착지 선택 상태 업데이트
                showPlaceSearch = true                          // 검색 화면 띄우기
            },
            onClose = onClose, // 상위 컴포저블(MatchingScreen)에게 닫기 요청
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )

        // 하단 바텀 시트
        BottomSheetScaffold(
            sheetContainerColor = Color.White,
            sheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            sheetPeekHeight = 120.dp,

            sheetContent = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // 단계별 컨텐츠 표시
                    if (currentPhase == MatchingPhase.BOOKING || currentPhase == MatchingPhase.SERVICE_TYPE) {
                        ServiceTypeSelectionContent(
                            routeReady = routeReady,
                            onSelect = { matchingViewModel.navigateToTimeSelection() }
                        )
                    } else {
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
                                // 버튼 누르면 -> 다이얼로그 상태 True
                                onPaymentClick = { showPaymentDialog = true },
                                onEdit = matchingViewModel::navigateToRequestDetail
                            )
                            else -> Spacer(modifier = Modifier.height(100.dp))
                        }
                    }
                }
            },

            // 2️⃣ [메인 화면 내용] (지도 + 검색창)
            content = { padding ->
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)) { // 시트 높이만큼 패딩 자동 적용 안 하려면 padding 제거해도 됨

                    // (1) 지도
                    KakaoMapView(
                        modifier = Modifier.fillMaxSize(),
                        locationX = mapUiState.centerLocation?.longitude ?: 126.9780,
                        locationY = mapUiState.centerLocation?.latitude ?: 37.5665,
                        route = mapUiState.route,
                        enabled = true
                    )

                    // (2) 상단 입력창 (지도 위에 뜸)
                    PathInputBox(
                        startLocation = startLocation,
                        endLocation = endLocation,
                        isSelectingStart = isSelectingStart,
                        onLocationClick = { isStart ->
                            placeSearchViewModel.setSelectingTarget(isStart)
                            showPlaceSearch = true
                        },
                        onClose = onClose,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                    )
                }
            }
        )
    }


    if (showPaymentDialog) {
        CommonDialog(
            title = "예약 완료",
            message = "동행 예약이 완료되었습니다.\n근처 동행자에게 알림 메시지를 보냅니다.\n\n" +
                    "[확인] 버튼을 누르면 홈 화면으로 이동합니다.",
            onDismiss = {
                showPaymentDialog = false
                // 팝업 닫히면서 최종 완료 로직 실행
                matchingViewModel.navigateToOverview()
                onNavToHome()
            },
            cancelText = "확인",
            onConfirm = null // 확인 버튼 하나만 쓸 거면 null, 취소/확인 둘 다 필요하면 함수 전달
        )
    }

    // ==========================================================
    // 💡 B. 장소 검색 화면 (Full Screen Overlay)
    // ==========================================================
    if (showPlaceSearch) {
        PlaceSearchScreen(
            searchType = if (isSelectingStart) "출발지" else "도착지",
            onPlaceSelected = { place ->
                placeSearchViewModel.selectPlace(place)
                showPlaceSearch = false
            },
            onBackPressed = {
                // 검색 취소 시 지도 화면(입력 박스)만 보이게 됨
                showPlaceSearch = false
                placeSearchViewModel.clearSearchQuery()
            }
        )
    }
}