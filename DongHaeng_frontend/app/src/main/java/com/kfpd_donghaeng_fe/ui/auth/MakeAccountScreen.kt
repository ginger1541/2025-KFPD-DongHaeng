package com.kfpd_donghaeng_fe.ui.auth


import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier

import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.domain.entity.auth.MakeAccountUiState
import com.kfpd_donghaeng_fe.domain.entity.auth.UserType
import com.kfpd_donghaeng_fe.ui.auth.signin.CertificateVerificationSheetUI
import com.kfpd_donghaeng_fe.ui.auth.signin.SignInScreen_2
import com.kfpd_donghaeng_fe.ui.auth.signin.SignInScreen_3
import com.kfpd_donghaeng_fe.ui.auth.signin.SignIngScreen_0
import com.kfpd_donghaeng_fe.ui.auth.signin.SingInScreen_4
import com.kfpd_donghaeng_fe.ui.auth.signin.UserInfoScreen
import com.kfpd_donghaeng_fe.ui.auth.signin.UserTypePage
import com.kfpd_donghaeng_fe.viewmodel.auth.MakeAccountViewModel

// ⭐ UserType enum 클래스도 임포트해야 합니다.




@Composable
fun MakeAccountRoute(
    viewModel: MakeAccountViewModel = hiltViewModel() // Hilt로 주입
) {
    // 1. 상태 관찰 (State 수집)
    val uiState by viewModel.uiState.collectAsState()


    // 2. 껍데기 UI(Screen)에 데이터와 함수 전달
    MakeAccountScreen(
        uiState = uiState,
        onNextClick = viewModel::nextPage, // 함수 참조
        selectedType = uiState.userType,
        onUserTypeSelect = viewModel::updateUserType,
        onUserIdChange = viewModel::updateUserId,
        onPasswordChange = viewModel::updatePassword,
        onPasswordConfirmChange = viewModel::updatePasswordConfirm,
        onGenderSelect = viewModel::updateGender,
        onPhoneNumberChange = viewModel::updatePhoneNumber,
        onBirthDateChange = viewModel::updateBirthDate,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onTogglePasswordConfirmVisibility = viewModel::togglePasswordConfirmVisibility,
        modifier = Modifier,
        previousPage= viewModel:: previousPage
    )
}




@Composable
fun MakeAccountScreen(
    uiState: MakeAccountUiState,
    onNextClick: () -> Unit,
    selectedType: UserType?,
    onUserTypeSelect: (UserType) -> Unit,
    previousPage: () -> Unit,


    //---
    onUserIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordConfirmChange: (String) -> Unit,
    onGenderSelect: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onTogglePasswordConfirmVisibility: () -> Unit,
    modifier: Modifier = Modifier

) {
    when (uiState.currentPage) {
        0->UserTypePage( // 사용자 선택
            uiState = uiState,
            selectedType = selectedType,
            onUserTypeSelect = onUserTypeSelect,
            onNextClick= onNextClick,
            //modifier = Modifier
        )
        1 ->SignInScreen_2( // 장애인증 인증 안내 페이지
            uiState = uiState,
            onNextClick = onNextClick,
        )
        2->CertificateVerificationSheetUI(
            uiState = uiState,
            previousPage= previousPage,
            onNextClick = onNextClick,
            modifier = Modifier
        )
        3->UserInfoScreen(
            uiState = uiState.userInfoUiState,
            onUserIdChange = onUserIdChange,
            onPasswordChange = onPasswordChange,
            onPasswordConfirmChange = onPasswordConfirmChange,
            onGenderSelect = onGenderSelect,
            onPhoneNumberChange = onPhoneNumberChange,
            onBirthDateChange = onBirthDateChange,
            onNextClick= onNextClick,
            onTogglePasswordVisibility = onTogglePasswordVisibility,
            onTogglePasswordConfirmVisibility = onTogglePasswordConfirmVisibility
        )
        4->SignIngScreen_0(
            uiState = uiState,
            onNextClick = onNextClick,
        )

        5 ->SignInScreen_3(  // 프로필 설정 (닉네임 . 자기 소개 )
            uiState = uiState,
            onNextClick = onNextClick,
        )
        6 ->SingInScreen_4( // // 등록 완료! 페이지
            uiState = uiState,
            onNextClick = onNextClick,
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

