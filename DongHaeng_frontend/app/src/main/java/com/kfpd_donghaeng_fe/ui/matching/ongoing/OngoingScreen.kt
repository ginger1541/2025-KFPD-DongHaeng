package com.kfpd_donghaeng_fe.ui.matching.ongoing

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kfpd_donghaeng_fe.GlobalApplication
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingViewModel




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

var user: Int =1 //테스트용  1 = 요청자 2 = 동행자
var alpha_user2 =0.9f
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OngoingScreen(viewModel: OngoingViewModel = viewModel(), onNavigateToReview: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    // Box 안의 컴포넌트들은 순서대로 쌓입니다 (1 -> 2 -> 3 -> 4)
    Box(modifier = Modifier.fillMaxSize()) {

        Background_Map()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        ) {
            TopSheet(uiState.OngoingPage)
        }

        if (user == 2) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
            )
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
                BottomSheet(viewModel, onNavigateToReview)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun OngoingScreenPreview() {
    // 뷰모델은 기본값(viewModel())을 사용하고,
    // onNavigateToReview는 더미 함수 {}를 사용합니다.
    OngoingScreen(onNavigateToReview = {})
}


