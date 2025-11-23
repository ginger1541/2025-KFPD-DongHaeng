package com.kfpd_donghaeng_fe.ui.matching.ongoing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.GlobalApplication
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingEntity
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingViewModel

// =========================================================================================
// 1. Map Composable
// =========================================================================================

@Composable
fun Background_Map() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White.copy(0.9f)),
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

var user: Int = 1 // 테스트용 1 = 요청자 2 = 동행자
var alpha_user2 = 0.9f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OngoingScreen(
    uiState: OngoingEntity,
    nextPage:()->Unit,
    NavigateToReview: () -> Unit // 리뷰 화면 이동 함수를 인자로 받음
) {
    // Box 안의 컴포넌트들은 순서대로 쌓입니다 (1 -> 2 -> 3 -> 4)
    Box(modifier = Modifier.fillMaxSize()) {

        Background_Map()

        // 상단 시트
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            TopSheet(uiState.OngoingPage)
        }

        // 동행자(user=2)일 경우 QR 코드 시트
        if (user == 2) {
            // 배경 오버레이
            Box(
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
                QRSheet(page = 0)
            }
        }

        // 요청자(user=1)일 경우 하단 시트
        if (user == 1) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            ) {
                // 💡 수정: BottomSheet에 ViewModel이 아닌 onNavigateToReview 함수를 전달
                BottomSheet(
                    uiState = uiState, // BottomSheet이 필요한 경우 상태 전달
                    nextPage=nextPage,
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
    nextPage:()->Unit,
    NavigateToReview: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    OngoingScreen(
        uiState = uiState,
        nextPage=viewModel::nextPage,
        NavigateToReview = viewModel::NavigateToReview
    )
}

// =========================================================================================
// 4. Preview (테스트용)
// 💡 Preview 함수를 주석 해제하고, 더미 데이터로 OngoingScreen을 호출합니다.
// =========================================================================================

/*
// ⚠️ 주의: TopSheet, QRSheet, BottomSheet, OngoingViewModel 등이 정의되지 않아 컴파일 오류가 발생할 수 있습니다.
// 이들은 임시로 주석 처리하거나 더미 Composable로 대체해야 합니다.
@Preview(showBackground = true)
@Composable
fun OngoingScreenPreview() {
    val dummyOngoingEntity = OngoingEntity(OngoingPage = 0) // 더미 데이터 생성

    OngoingScreen(
        uiState = dummyOngoingEntity,
        onNavigateToReview = {} // 더미 함수 전달
    )
}
*/