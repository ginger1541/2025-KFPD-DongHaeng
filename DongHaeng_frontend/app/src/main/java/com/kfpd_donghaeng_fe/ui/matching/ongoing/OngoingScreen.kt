package com.kfpd_donghaeng_fe.ui.matching.ongoing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kfpd_donghaeng_fe.GlobalApplication
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute
import com.kfpd_donghaeng_fe.domain.entity.auth.UserType
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingRequestEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanEndEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.domain.service.AppSettingsNavigator
import com.kfpd_donghaeng_fe.domain.service.PermissionChecker
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.common.permission.rememberLocationPermissionRequester
import com.kfpd_donghaeng_fe.util.AppScreens
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingUiEvent
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingViewModel
import com.kfpd_donghaeng_fe.viewmodel.matching.QRViewModel

// =========================================================================================
// 1. Map Composable
// =========================================================================================

@Composable
fun Background_Map(
    markers: List<RouteLocation>, // 👈 추가: 마커 리스트 받기
    route: WalkingRoute?          // 👈 추가: 경로 정보 받기
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White), // 지도 로딩 전 흰색 배경
    ) {
        KakaoMapView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            // 중심 좌표는 내 위치(REQUESTER or COMPANION)가 있으면 거기로, 없으면 서울 시청 등 기본값
            locationX = markers.firstOrNull()?.longitude ?: 126.9780,
            locationY = markers.firstOrNull()?.latitude ?: 37.5665,

            // 💡 ViewModel에서 받은 데이터 연결
            route = route,
            markers = markers,

            enabled = GlobalApplication.isMapLoaded
        )
    }
}

// =========================================================================================
// 2. Screen (UI 렌더링 전용)
// 💡 오류 수정: ViewModel 인자를 제거하고, 필요한 이벤트 핸들러만 받습니다.
// =========================================================================================

var user: Int = 2// 테스트용 1 = 요청자 2 = 동행자


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OngoingScreen(
    uiState: OngoingEntity,
    uiState2: OngoingRequestEntity,
    uiState3:QREntity,
    resultUiState: QRScanResultEntity, // 여기에 스캔 시간
    locateUiState : QRScandEntity, // 스캔 시작 장소
    onScanRequest: (QRScandEntity, QRTypes, Long) -> Unit,
    nextPage:()->Unit,
    NavigateToReview: () -> Unit // 리뷰 화면 이동 함수를 인자로 받음
    ,
    mapMarkers: List<RouteLocation>,
    routePath: WalkingRoute?,
) {


    // Box 안의 컴포넌트들은 순서대로 쌓입니다 (1 -> 2 -> 3 -> 4)
    Box(modifier = Modifier.fillMaxSize()) {

        Background_Map(
            markers = mapMarkers,
            route = routePath
        )
        // 동행자(user=2)일 경우 QR 코드 시트로 시작
        if (uiState.userType == UserType.NEEDY) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                TopSheet(uiState,uiState2)
            }
            val page = uiState.OngoingPage
            // 배경 오버레이
            when(page){
               0,2-> {Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.6f))
                )
                   // QR 코드 시트
                   Box(
                       modifier = Modifier
                           .fillMaxSize(),
                       contentAlignment = Alignment.Center
                   ) {
                       QRSheet(uiState,uiState3,onScanRequest)
                   }

               }
                else->BottomSheet(

                    uiState = uiState, // BottomSheet이 필요한 경우 상태 전달
                    resultUiState = resultUiState,
                    locateUiState = locateUiState,
                    onScanRequest = onScanRequest,
                    nextPage = nextPage,
                    NavigateToReview = NavigateToReview
                )

            }

        }

        // 요청자(user=1)일 경우 하단 시트
        if (uiState.userType == UserType.HELPER) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
            ) {
                TopSheet(uiState,uiState2)
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {

                BottomSheet(
                    uiState = uiState, // BottomSheet이 필요한 경우 상태 전달
                    resultUiState = resultUiState,
                    locateUiState = locateUiState,
                    onScanRequest = onScanRequest,
                    nextPage = nextPage,
                    NavigateToReview = NavigateToReview
                )
            }

        }
    }
}

// =========================================================================================
// 3. Route (ViewModel/Hilt 연결)
// =========================================================================================

@Composable
fun OngoingRoute(
    viewModel: OngoingViewModel = hiltViewModel(),
    viewModel2: QRViewModel = hiltViewModel(),
    matchId: Long,
    appSettingsNavigator: AppSettingsNavigator,
    permissionChecker: PermissionChecker,
    navController: NavHostController,
) {

    val uiState by viewModel.uiState.collectAsState()
    val uiState2 by viewModel.uiState2.collectAsState()
    val uiState3 by viewModel2.uiState3.collectAsState()
    //val uiState3: QREntity = viewModel2.uiState3.collectAsState().value
    val locateUiState by viewModel2.locateUiState.collectAsState()

    val resultUiState by viewModel2.resultUiState.collectAsState()
    // 2. 스캔 상태 플래그 추출 (QREntity에 qrScanned 필드가 있다고 가정)
    val isScanned = uiState3.qrScanned

    // 💡 3. LaunchedEffect를 사용하여 스캔 상태를 관찰하고 페이지 전환을 수행
//    LaunchedEffect(isScanned) {
//        if (isScanned) {
//            // 스캔이 완료시  다음 페이지!
//            viewModel.nextPage()
//            // EndCompanionSheet(resultUiState) <- 데이터 넘기기용
//        }
//    }

    LaunchedEffect(uiState3.qrScanned) {
        if (uiState3.qrScanned) {
            // 1. 페이지 넘기기 (기존 로직)
            viewModel.nextPage()

            // 2. 만약 '종료 QR(END)'을 찍은 것이라면 리뷰 화면으로 이동 준비!
            if (uiState3.qrType == QRTypes.END && resultUiState is QRScanEndEntity) {
                val result = resultUiState as QRScanEndEntity

                // 🚀 ViewModel의 함수 호출 (거리 정보는 VM이 내부적으로 가지고 있음)
                viewModel.NavigateToReview(
                    timeMin = result.actualDurationMinutes,
                    earnedPoints = result.earnedPoints
                )
            }
        }
    }

    /*
    지도
     */
    val requester = rememberLocationPermissionRequester(permissionChecker, appSettingsNavigator)
    val permissionState = requester.state.value
    val mapMarkers by viewModel.mapMarkers.collectAsState()
    val routePath by viewModel.routePath.collectAsState()


    LaunchedEffect(Unit) {
        if (!permissionState.isGranted) {
            requester.request()
        }
    }

    // 권한 상태를 감시하다가 '승인됨(true)'이면 위치 추적 시작
    LaunchedEffect(permissionState.isGranted) {
        if (permissionState.isGranted) {
            viewModel.startLocationTracking()
        }
    }

    // 데이터 로드는 권한과 상관없이 진행
    LaunchedEffect(matchId) {
        viewModel.loadMatchData(matchId)
    }

    // 리뷰
    LaunchedEffect(Unit) {
        viewModel.eventFlow.collect { event ->
            when (event) {
                is OngoingUiEvent.NavigateToReview -> {
                    // URL에 데이터 붙여서 이동
                    // 예: review_route/123/456?time=18분&dist=1.2km&points=200
                    val route = "${AppScreens.REVIEW_BASE}/${event.matchId}/${event.partnerId}" +
                            "?time=${event.totalTime}&dist=${event.distance}"

                    navController.navigate(route) {
                        popUpTo(AppScreens.HOME_BASE) { inclusive = false }
                    }
                }
            }
        }
    }

    if (permissionState.isGranted) {
        // ✅ 권한이 있으면 정상 화면 표시
        OngoingScreen(
            uiState = uiState,
            uiState2 = uiState2,
            uiState3 = uiState3,
            resultUiState = resultUiState,
            locateUiState = locateUiState,
            mapMarkers = mapMarkers,
            routePath = routePath,
            onScanRequest = viewModel2::scanQR,
            nextPage = viewModel::nextPage,

            // 람다식({ })으로 변경하여 인자(0, 0) 전달
            NavigateToReview = {
                viewModel.NavigateToReview(0, 0)
            }
        )
    } else {
        // 🚫 권한이 없으면 안내 문구 표시 (간단하게 처리)
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("위치 권한이 있어야 동행을 시작할 수 있습니다.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { requester.request() }) {
                    Text("권한 허용하기")
                }
            }
        }
    }

//    OngoingScreen(
//        uiState = uiState,
//        uiState2 = uiState2,
//        uiState3=uiState3,
//        resultUiState = resultUiState,
//        locateUiState=locateUiState,
//        onScanRequest= viewModel2::scanQR,
//        nextPage=viewModel::nextPage,
//        NavigateToReview = viewModel::NavigateToReview,
//        mapMarkers = mapMarkers,
//        routePath = routePath,
//    )
}



