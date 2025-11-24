package com.kfpd_donghaeng_fe.ui.matching.search

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.domain.entity.LocationType
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.domain.entity.toRouteLocation
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
import com.kfpd_donghaeng_fe.viewmodel.matching.MatchingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainRouteScreen(
    onClose: () -> Unit,
    onNavToHome: () -> Unit,
    bookingViewModel: BookingViewModel = hiltViewModel(),
    placeSearchViewModel: PlaceSearchViewModel = hiltViewModel(),
    startSearch: Boolean = false,
    mapViewModel: MapViewModel = hiltViewModel(),
    matchingViewModel: MatchingViewModel = hiltViewModel()
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
    val selectedDetailPlace by placeSearchViewModel.selectedDetailPlace.collectAsState()

    fun isValidLocation(loc: com.kfpd_donghaeng_fe.domain.entity.RouteLocation?): Boolean {
        return loc != null && (loc.latitude ?: 0.0) != 0.0 && (loc.longitude ?: 0.0) != 0.0
    }
    // 1. 화면 진입 시 초기화
    LaunchedEffect(selectedDetailPlace) {
        if (selectedDetailPlace != null) {
            // 상세 정보가 선택되면 Phase를 PLACE_DETAIL로 전환
            if (currentPhase != MatchingPhase.PLACE_DETAIL) {
                // ❌ 기존: matchingViewModel.navigateToPhase(MatchingPhase.PLACE_DETAIL)
                // ✅ 변경:
                bookingViewModel.navigateToPhase(MatchingPhase.PLACE_DETAIL)
            }
        } else if (currentPhase == MatchingPhase.PLACE_DETAIL) {
            // 상세 정보가 해제되면 (onClose), BOOKING으로 복귀
            // ❌ 기존: matchingViewModel.navigateToBooking()
            // ✅ 변경:
            bookingViewModel.navigateToBooking()
        }
    }

    LaunchedEffect(selectedDetailPlace) {
        if (selectedDetailPlace != null) {
            // [FIX] Phase가 현재 PLACE_DETAIL이 아니거나 BOOKING일 때만 전환 요청
            // 이 조건이 없으면, PLACE_DETAIL 상태에서 다시 PLACE_DETAIL로 전환 요청이 들어갈 수 있습니다.
            if (currentPhase != MatchingPhase.PLACE_DETAIL) {
                matchingViewModel.navigateToPhase(MatchingPhase.PLACE_DETAIL)
            }
        } else if (currentPhase == MatchingPhase.PLACE_DETAIL) {
            // 상세 정보가 해제되면 (모달 닫기 버튼), BOOKING으로 복귀
            matchingViewModel.navigateToBooking()
        }
    }

    LaunchedEffect(Unit) {
        if (currentPhase == MatchingPhase.OVERVIEW) {
            bookingViewModel.navigateToBooking()
        }
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
                        MatchingPhase.PLACE_DETAIL -> {
                            selectedDetailPlace?.let { place ->
                                PlaceDetailSheetContent(
                                    place = place,
                                    onClose = {
                                        // 1. ViewModel 상태 해제
                                        placeSearchViewModel.setDetailPlace(null)
                                        // 2. Phase 복귀 (LaunchedEffect에서 처리되므로 여기서 명시적 호출 제거)
                                    },
                                    onSelect = { isStart ->
                                        // 1. 장소를 선택하고 Phase를 Booking으로 복귀
                                        placeSearchViewModel.setDetailPlace(null)
                                        placeSearchViewModel.selectPlace(place)
                                        // 2. Phase 복귀 (LaunchedEffect에서 처리되므로 여기서 명시적 호출 제거)
                                    },
                                    modifier = Modifier.padding(horizontal = 0.dp)
                                )
                            } ?: Spacer(modifier = Modifier.height(100.dp))
                        }
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
                if (currentPhase != MatchingPhase.PLACE_DETAIL) {
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

            // [CRITICAL FIX: 아이템 클릭 시 홈 이동 방지]
            onBackPressed = {
                // 1. **핵심 확인:** 이 back 호출이 장소 상세 정보 선택(아이템 클릭) 때문에 발생한 것인지 확인.
                //    (selectedDetailPlace != null 이면 아이템 클릭 후 호출된 것입니다.)
                val isSelectionClose = placeSearchViewModel.selectedDetailPlace.value != null

                if (isSelectionClose) {
                    // Case 1: 아이템이 클릭되었습니다. (Detail Phase로 전환 예정)
                    // -> 검색 오버레이만 닫고, 전체 플로우(onNavToHome)를 끝내지 않습니다.
                    showPlaceSearch = false
                    // 초기 검색 상태 플래그를 해제하여, 다음 백 버튼부터는 홈으로 가지 않도록 합니다.
                    isInitialSearch = false
                }
                else if (isInitialSearch) {
                    // Case 2: 백 버튼을 눌렀고, 초기 검색 상태입니다. (아무것도 선택 안 함)
                    onNavToHome() // 홈으로 돌아가서 Matching Flow를 완전히 종료
                } else {
                    // Case 3: 백 버튼을 눌렀고, 경로 설정 중입니다.
                    showPlaceSearch = false
                    placeSearchViewModel.clearSearchQuery()
                }
            }
        )
    }
}
