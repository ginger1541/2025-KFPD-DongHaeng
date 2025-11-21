package com.kfpd_donghaeng_fe.ui.auth.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.auth.LoginPageButton
import com.kfpd_donghaeng_fe.ui.theme.MainOrange
import com.kfpd_donghaeng_fe.viewmodel.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.viewmodel.auth.MakeAccountUiState

//TODO: 전역 mainorange -> color.kt 로 변환하기 111 줄 텍스트 클릭시 넘어가기
//TODO : 계정만들기 <-> 로그인 바꾸기 회의

// page =0 onboarding
// page =1 로그인 도입 화면
// 계정만들기 버튼 <- loginbutton.kt 에 따로 빼놓음
//LoginPageButton("계정 만들기",{}) <- 사용예시




@Composable
fun OnboardingScreen(uiState: LoginAccountUiState,
                     onNextClick: () -> Unit,
                     MovetoMakeAccount: () -> Unit,
                     page: Int ){
    // int page =0 일 때 : onboding
    var LogoImg=painterResource(id = R.drawable.ic_logo_white)
    var backColor=MainOrange
    var button = false
    var FontColor=Color.White
    if ( page == 0){
        LogoImg = painterResource(id = R.drawable.ic_logo_white)
        backColor = MainOrange
    }
    // int page ==1  일떄 : 로그인 화면 입성
    else if (page ==1){
        LogoImg = painterResource(id = R.drawable.ic_logo_orange)
        backColor=Color.White
        button = true
        FontColor=MainOrange
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backColor),
        contentAlignment = Alignment.Center
    ) {

        Column(
            modifier = Modifier.offset(y = (-80).dp), // Y축으로 -40dp 이동 (위로)
            horizontalAlignment = Alignment.CenterHorizontally // 수평 가운데 정렬
        ) {

                // 로고 이미지
                Image(
                    painter = LogoImg,
                    contentDescription = "Logo",
                    modifier = Modifier.size(140.dp)
                )

                // 로고와 텍스트 간격
                Spacer(modifier = Modifier.height(8.dp)) // 로고 옆에 8dp 간격 추가
                Text(
                    text = "동행",
                    color = FontColor, // 변경된 텍스트 색상 적용
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.ExtraBold
                )
                // 텍스트
                Text(
                    text = "믿을 수 있는 동행, 따듯한 연결",
                    color = FontColor, // 변경된 텍스트 색상 적용
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

            }
        if (button) {
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter) // 화면 하단 기준 정렬
                    .padding(horizontal = 25.dp)
                    .padding(bottom = 200.dp), // 하단에서 50dp 띄움
                horizontalAlignment = Alignment.CenterHorizontally
            ){
            Box(
                modifier = Modifier
                    .offset(y = -20.dp)
                    .padding(horizontal = 20.dp),
            ){ LoginPageButton("계정 만들기", MovetoMakeAccount)}
            Row( modifier = Modifier
                .offset(y = 0.dp)) {
                Text(
                    text = "계정이 이미 있으시다면?  ",
                    color = Color.Gray,
                    style = MaterialTheme.typography.titleSmall,
                )
                Text(
                    modifier = Modifier
                        .clickable {
                            //TODO:click 시 페이지 넘어가기
                            onNextClick()
                        },
                    text = "로그인",
                    color = MainOrange,
                    style = MaterialTheme.typography.titleSmall,
                )
            }

        }}
    }

}

/*

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview2() {
    OnboardingScreen(page = 1)
}
*/