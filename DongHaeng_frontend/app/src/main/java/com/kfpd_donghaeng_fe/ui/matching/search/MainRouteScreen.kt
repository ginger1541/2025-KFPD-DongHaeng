// main/java/com/kfpd_donghaeng_fe/ui/matching/search/MainRouteScreen.kt (수정됨)
package com.kfpd_donghaeng_fe.ui.matching.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainRouteScreen(
    onClose: () -> Unit,
    onNavToHome: () -> Unit, // 💡 최종 완료 후 Home 화면으로 이동 요청
    matchingViewModel: MatchingViewModel = hiltViewModel(),
    placeSearchViewModel: PlaceSearchViewModel = hiltViewModel(),
    mapViewModel: MapViewModel = hiltViewModel()
) {
    // 1. ViewModel 상태 수집
    val startLocation by placeSearchViewModel.startLocation.collectAsState()
    val endLocation by placeSearchViewModel.endLocation.collectAsState()
    val isSelectingStart by placeSearchViewModel.isSelectingStart.collectAsState()
    val mapUiState by mapViewModel.uiState.collectAsState()
    val currentPhase by matchingViewModel.currentPhase.collectAsState()

    // 2. 경로 설정 메인 화면에서 사용할 내부 UI 상태 (검색 화면 표시 여부)
    var showPlaceSearch by remember { mutableStateOf(false) }

    val routeReady = startLocation != null && endLocation != null

    // 💡 최종 예약 완료 로직: Phase 리셋 후 홈으로 이동 요청
    val handleBookingCompletion = {
        // TODO: 여기서 서버로 최종 예약 API 호출 로직이 들어갑니다. (현재 주석 처리)
        println("// TODO: 요청 생성 및 채팅방 생성 API 호출")
        matchingViewModel.navigateToOverview() // 1. Phase를 OVERVIEW로 리셋
        onNavToHome()                        // 2. NavController를 사용해 Home으로 이동
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
    // 💡 A. 메인 경로 설정 화면 (Map + Bottom Sheet/Input)
    // ==========================================================

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. 지도 뷰 (MapViewModel 상태 참조)
        KakaoMapView(
            modifier = Modifier.fillMaxSize(),
            // 현재 위치 또는 경로의 중심 좌표를 지도 중심 좌표로 사용
            locationX = mapUiState.centerLocation?.longitude ?: 126.9780,
            locationY = mapUiState.centerLocation?.latitude ?: 37.5665,
            enabled = true
            // TODO: mapUiState.markers, mapUiState.route 정보를 KakaoMapView에 전달하여 마커 및 경로를 그리도록 수정 필요
        )

        // 2. 상단 경로 입력 필드 (Map 위에 고정)
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

        // 3. 하단 바텀 시트 (MatchingPhase에 따른 콘텐츠)
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White, RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp))
        ) {
            SheetHandleBar()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(top = 8.dp)
            ) {
                // BOOKING Phase (경로 입력 초기 상태) 또는 SERVICE_TYPE 상태일 때
                if (currentPhase == MatchingPhase.BOOKING || currentPhase == MatchingPhase.SERVICE_TYPE) {
                    ServiceTypeSelectionContent(
                        routeReady = routeReady,
                        onSelect = { matchingViewModel.navigateToTimeSelection() }
                    )
                } else {
                    when (currentPhase) {
                        // Phase 2: 예약 시간 선택 (6번)
                        MatchingPhase.TIME_SELECTION -> RequestTimePicker(
                            currentDateTime = matchingViewModel.selectedDateTime.value,
                            onConfirm = { newDateTime ->
                                matchingViewModel.updateSelectedTime(newDateTime)
                                matchingViewModel.navigateToRequestDetail()
                            },
                            onCancel = { matchingViewModel.navigateToServiceType() }
                        )

                        // Phase 3: 요청 사항 입력 (7번)
                        MatchingPhase.REQUEST_DETAIL -> RequestDetailContent(
                            onNext = matchingViewModel::navigateToPayment,
                            onBack = matchingViewModel::navigateToTimeSelection
                        )

                        // Phase 4: 결제 화면 (8번)
                        MatchingPhase.PAYMENT -> PaymentContent(
                            onConfirm = handleBookingCompletion, // 💡 최종 완료 로직 호출
                            onEdit = matchingViewModel::navigateToRequestDetail
                        )

                        else -> Spacer(modifier = Modifier.height(200.dp))
                    }
                }
            }
        }
    }


    // ==========================================================
    // 💡 B. 장소 검색 화면 (Full Screen Overlay)
    // ==========================================================
    if (showPlaceSearch) {
        PlaceSearchScreen(
            searchType = if (isSelectingStart) "출발지" else "도착지",
            onPlaceSelected = { place ->
                // 1. ViewModel에 선택된 장소 업데이트
                placeSearchViewModel.selectPlace(place)
                // 2. 검색 화면 닫기
                showPlaceSearch = false
            },
            onBackPressed = {
                showPlaceSearch = false
                placeSearchViewModel.clearSearchQuery()
            }
        )
    }
}