package com.kfpd_donghaeng_fe.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kfpd_donghaeng_fe.ui.auth.signup.ProfileSetupPage
import com.kfpd_donghaeng_fe.ui.auth.signup.UserTypePage
import com.kfpd_donghaeng_fe.ui.auth.signup.WelcomePage
import com.kfpd_donghaeng_fe.ui.auth.signup.TermsPage
import com.kfpd_donghaeng_fe.ui.theme.*
import com.kfpd_donghaeng_fe.viewmodel.auth.SignUpViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel = viewModel(),

    // 화면 이동
    onNavigateBack: () -> Unit,
    onSignUpComplete: (UserType) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    // 화면의 기본 구조 (상단바, 본문, 하단바 등)
    Scaffold(
        topBar = {
            // 상단바 (TopAppBar)
            SignUpTopAppBar(
                currentPage = uiState.currentPage, // 현재 페이지 번호를 전달
                onBackClicked = {
                    // 뒤로가기 버튼 클릭 시
                    if (uiState.currentPage == 1) {
                        onNavigateBack() // 첫 페이지면 아예 화면 나가기
                    } else {
                        viewModel.previousPage() // 아니면 ViewModel에 이전 페이지 요청
                    }
                }
            )
        },
        // Scaffold의 content 영역 (상단바를 제외한 나머지)
        content = { paddingValues -> // paddingValues가 상단바 영역을 알려줌
            Column(
                modifier = Modifier
                    .fillMaxSize() // 화면 꽉 채우기
                    .padding(paddingValues) // 상단바 영역 피해서 패딩
                    .padding(horizontal = 24.dp) // 좌우 공통 패딩
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // 1. 진행도 표시기
                SignUpProgressIndicator(currentPage = uiState.currentPage)

                Spacer(modifier = Modifier.height(40.dp)) // 진행도 표시기와 내용 사이 여백

                // 2. 페이지 내용 (when으로 분기)
                when (uiState.currentPage) {
                    1 -> WelcomePage(
                        // "다음" 버튼을 누르면 viewModel의 nextPage() 함수를 호출
                        onNextClick = { viewModel.nextPage() }
                    )

                    2 -> ProfileSetupPage(
                        // ViewModel의 상태와 이벤트 핸들러를 넘겨줌
                        nickname = uiState.nickname,
                        bio = uiState.bio,
                        onNicknameChange = viewModel::onNicknameChange,
                        onBioChange = viewModel::onBioChange,
                        onNextClick = { viewModel.nextPage() }
                    )

                    3 -> UserTypePage(
                        // ViewModel의 상태와 이벤트 핸들러 연결
                        selectedType = uiState.userType,
                        onUserTypeSelect = viewModel::onUserTypeSelect,
                        onNextClick = { viewModel.nextPage() }
                    )

                    4 -> TermsPage(
                        // (1) 상태 연결
                        allAgreed = uiState.termsAllAgreed,
                        serviceAgreed = uiState.termServiceAgreed,
                        privacyAgreed = uiState.termPrivacyAgreed,
                        locationAgreed = uiState.termLocationAgreed,

                        // (2) 이벤트 핸들러
                        onAllAgreeChange = viewModel::onAllAgreeChange,
                        onServiceAgreeChange = viewModel::onServiceAgreeChange,
                        onPrivacyAgreeChange = viewModel::onPrivacyAgreeChange,
                        onLocationAgreeChange = viewModel::onLocationAgreeChange,

                        // (3) 가입 완료 버튼 클릭
                        onCompleteClick = {
                            viewModel.submitSignUp() // 1. ViewModel에 가입 요청
                            onSignUpComplete(uiState.userType!!)     // 2. MainActivity에 가입 완료 알림
                        }
                    )
                }
            }
        }
    )
}


/** 상단바 Composable (SignUpScreen 내부에서만 사용) */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignUpTopAppBar(
    currentPage: Int,
    onBackClicked: () -> Unit
) {
    TopAppBar(
        title = {
            if (currentPage < 4) {
                Text(
                    text = "SignUpView",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextBlack
                // TODO: ui/theme/Type.kt 에 정의된 커스텀 폰트 적용
                )
            }
        },
        navigationIcon = {
            if (currentPage < 4) {
                IconButton(onClick = onBackClicked) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = TextBlack
                    )
                }
            }
        },
        actions = {
            if (currentPage < 4) {
                Spacer(modifier = Modifier.padding(32.dp))
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White, // 배경색
            titleContentColor = Color.Black // 글자색
        )
    )
}

@Composable
private fun SignUpProgressIndicator(
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp), // 좌우에 좀 더 여백을 줌
        horizontalArrangement = Arrangement.SpaceBetween // 점들 사이에 동일한 간격
    ) {
        for (page in 1..4) {
            IndicatorDot(
                // 현재 페이지(currentPage)보다 작거나 같으면 활성화(오렌지색)
                isActive = (page <= currentPage)
            )
        }
    }
}

/** 진행도 표시기의 '점' 하나 Composable (새로 추가) */
@Composable
private fun IndicatorDot(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val color = if (isActive) BrandOrange else MediumGray

    Box(
        modifier = modifier
            .size(width = 70.dp, height = 8.dp) // 점의 크기 (디자인에 맞게 조절)
            .background(color, shape = RoundedCornerShape(4.dp)) // 둥근 모서리
    )
}
