package com.kfpd_donghaeng_fe.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.ui.auth.onboarding.LoginPage
import com.kfpd_donghaeng_fe.ui.auth.onboarding.OnboardingScreen
import com.kfpd_donghaeng_fe.viewmodel.auth.LoginViewModel


@Composable
fun LoginRoute(
    // ✅ 부모로부터 메인 화면으로 이동하는 함수를 전달받습니다.
    onNavigateToMakeAccount: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // 💡 상태를 감시하는 LaunchedEffect
    LaunchedEffect(uiState.currentPage) {
        // currentPage가 2가 되었을 때,
        if (uiState.currentPage == 2) {
            // 전달받은 내비게이션 실행 함수를 호출합니다.
            onNavigateToMakeAccount()
        }
    }

    LoginScreen(
        uiState = uiState,
        onNextClick = viewModel::login,
        MovetoMakeAccount = viewModel :: MovetoMakeAccount


    )
}


@Composable
fun LoginScreen(
    uiState: LoginAccountUiState,
    onNextClick: () -> Unit,
    MovetoMakeAccount : () -> Unit,

    ) {
    when (uiState.currentPage) {
        0 ->OnboardingScreen(
            uiState = uiState,
            onNextClick = onNextClick,
            MovetoMakeAccount = MovetoMakeAccount,
            page = 1,
        )
        1->LoginPage(
            uiState = uiState,
            onNextClick = onNextClick
        )

    }
}