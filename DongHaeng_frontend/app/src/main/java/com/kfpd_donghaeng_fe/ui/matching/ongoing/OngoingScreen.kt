package com.kfpd_donghaeng_fe.ui.matching.ongoing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.GlobalApplication
import com.kfpd_donghaeng_fe.domain.entity.auth.UserType
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingRequestEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingViewModel
import com.kfpd_donghaeng_fe.viewmodel.matching.QRViewModel

// =========================================================================================
// 1. Map Composable
// =========================================================================================

@Composable
fun Background_Map() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue.copy(0.9f)),
    ) {
        KakaoMapView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            locationX = 126.97796919,
            locationY = 37.56661209,
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
) {


    // Box 안의 컴포넌트들은 순서대로 쌓입니다 (1 -> 2 -> 3 -> 4)
    Box(modifier = Modifier.fillMaxSize()) {

        Background_Map()
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
    LaunchedEffect(isScanned) {
        if (isScanned) {
            // 스캔이 완료시  다음 페이지!
            viewModel.nextPage()
            // EndCompanionSheet(resultUiState) <- 데이터 넘기기용
        }
    }

    OngoingScreen(
        uiState = uiState,
        uiState2 = uiState2,
        uiState3=uiState3,
        resultUiState = resultUiState,
        locateUiState=locateUiState,
        onScanRequest= viewModel2::scanQR,
        nextPage=viewModel::nextPage,
        NavigateToReview = viewModel::NavigateToReview
    )
}


