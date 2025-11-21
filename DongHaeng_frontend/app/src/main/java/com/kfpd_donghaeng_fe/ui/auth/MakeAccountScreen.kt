package com.kfpd_donghaeng_fe.ui.auth


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.ui.auth.signin.SignInScreen_2
import com.kfpd_donghaeng_fe.ui.auth.signin.SignInScreen_3
import com.kfpd_donghaeng_fe.ui.auth.signin.SignIngScreen_0
import com.kfpd_donghaeng_fe.ui.auth.signin.SingInScreen_4
import com.kfpd_donghaeng_fe.ui.auth.signin.UserTypePage
import com.kfpd_donghaeng_fe.viewmodel.auth.LoginViewModel
import com.kfpd_donghaeng_fe.viewmodel.auth.MakeAccountUiState
import com.kfpd_donghaeng_fe.viewmodel.auth.MakeAccountViewModel


@Composable
fun MakeAccountRoute(
    viewModel: MakeAccountViewModel = hiltViewModel() // Hilt로 주입
) {
    // 1. 상태 관찰 (State 수집)
    val uiState by viewModel.uiState.collectAsState()

    // 2. 껍데기 UI(Screen)에 데이터와 함수 전달
    MakeAccountScreen(
        uiState = uiState,
        onNextClick = viewModel::nextPage, // 함수 참조 전달
        onPreviousClick = viewModel::previousPage
    )
}




@Composable
fun MakeAccountScreen(
    uiState: MakeAccountUiState,
    onNextClick: () -> Unit,
    onPreviousClick: () -> Unit
) {
    when (uiState.currentPage) {
        0,1 ->SignIngScreen_0(
            uiState = uiState,
            onNextClick = onNextClick,
            onPreviousClick = onPreviousClick
        )
        /*
        1->UserTypePage(){
            selectedType: UserType?,
            onUserTypeSelect: (UserType) -> Unit,
            onNextClick: () -> Unit,
            modifier: Modifier = Modifier
        }*/
        2 ->SignInScreen_2(
            uiState = uiState,
            onNextClick = onNextClick,
            onPreviousClick = onPreviousClick
        )
        3 ->SignInScreen_3(
            uiState = uiState,
            onNextClick = onNextClick,
            onPreviousClick = onPreviousClick
        )
        4 ->SingInScreen_4(
            uiState = uiState,
            onNextClick = onNextClick,
            onPreviousClick = onPreviousClick
        )
        else -> Text("가입 완료!")
    }
}



/*
@Preview(showBackground = true) // 배경을 흰색으로 보여줌
@Composable
fun MakeAccountScreenPreview_Page0() {
    MakeAccountScreen(
        // 1. 가짜 데이터(State)를 넣어줍니다.
        uiState = MakeAccountUiState(currentPage = 3),

        // 2. 이벤트는 그냥 "아무것도 안 함" 처리합니다.
        onNextClick = {},
        onPreviousClick = {}
    )
}*/

