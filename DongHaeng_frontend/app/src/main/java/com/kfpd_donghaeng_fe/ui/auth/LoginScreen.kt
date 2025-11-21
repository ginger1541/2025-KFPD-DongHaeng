package com.kfpd_donghaeng_fe.ui.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.ui.auth.onboarding.LoginPage
import com.kfpd_donghaeng_fe.ui.auth.onboarding.OnboardingScreen
import com.kfpd_donghaeng_fe.ui.auth.signin.SignInScreen_2
import com.kfpd_donghaeng_fe.ui.auth.signin.SignInScreen_3
import com.kfpd_donghaeng_fe.ui.auth.signin.SignIngScreen_0
import com.kfpd_donghaeng_fe.ui.auth.signin.SingInScreen_4
import com.kfpd_donghaeng_fe.viewmodel.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.viewmodel.auth.LoginViewModel
import com.kfpd_donghaeng_fe.viewmodel.auth.MakeAccountUiState
import com.kfpd_donghaeng_fe.viewmodel.auth.MakeAccountViewModel



/*
data class LoginAccountUiState(
    val currentPage: Int = 0,          // 페이지 번호 0 : 로그인 전 :1 로그인 후 메인 화면 진입
    val id: String = "",  //아이디
    val pw:String="", // 비번
    val userType: UserType? = null,    // 유저 타입
)*/
/*
@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel() // Hilt로 주입
) {
    // 1. 상태 관찰 (State 수집)
    val uiState by viewModel.uiState.collectAsState()
    // 2. 껍데기 UI(Screen)에 데이터와 함수 전달
    LoginScreen(
        uiState = uiState,
        onNextClick = viewModel::login, // 함수 참조 전달
    )
}
*/
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
        1->LoginPage(uiState = uiState,
            onNextClick = onNextClick)

        /*
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
        else -> Text("가입 완료!")*/
    }
}