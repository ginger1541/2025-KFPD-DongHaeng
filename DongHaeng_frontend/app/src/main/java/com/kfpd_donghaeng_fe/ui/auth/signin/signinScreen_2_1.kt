package com.kfpd_donghaeng_fe.ui.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.auth.LoginPageButton
import com.kfpd_donghaeng_fe.viewmodel.auth.MakeAccountUiState

@Composable
fun SingInScreen_4( uiState: MakeAccountUiState,
                    onNextClick: () -> Unit,
                    onPreviousClick: () -> Unit){
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top=0.dp)// 배경색은 흰색으로 가정
            .padding(horizontal = 25.dp), // 좌우 전체 패딩
     contentAlignment = Alignment.Center

    ){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Image(
                painter = painterResource(id = R.drawable.ic_logo_orange),
                contentDescription = "Logo",
                modifier = Modifier.size(220.dp)
                    .offset(y=-100.dp)
            )
            Text(
                text = "등록 완료!",
                fontWeight = FontWeight.ExtraBold,
                style = MaterialTheme.typography.displaySmall,
                color = MainOrange,
                modifier = Modifier.padding(top=24.dp,bottom = 24.dp)
                    .offset(y=-100.dp)
            )
            Text(
                text = "본인확인이 완료 됐어요!",

                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
                    .offset(y=-100.dp)
            )
            Row {
                // 1. buildAnnotatedString 사용
                val annotatedText = buildAnnotatedString {
                    // 기본 텍스트 스타일 설정 (원래 텍스트의 스타일: Gray)
                    // withStyle 블록을 사용하여 굵게 처리할 부분만 별도 스타일 적용
                    withStyle(
                        style = SpanStyle(
                            // "동행" 외의 텍스트는 원본 코드에서 지정한 Color.Gray
                            color = Color.Gray
                        )
                    ) {
                        append("선의의 새로운 시작, ")
                    }

                    // 굵게(Bold) 처리할 "동행" 부분
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray // 원본 텍스트의 색상은 유지
                        )
                    ) {
                        append("동행")
                    }

                    // 나머지 텍스트
                    withStyle(
                        style = SpanStyle(
                            color = Color.Gray
                        )
                    ) {
                        append("을 이용하러 가보실까요?")
                    }
                }

                Text(
                    // 2. Text 컴포넌트에 AnnotatedString 전달
                    text = annotatedText,
                    // 3. Text 컴포넌트 자체에는 별도의 FontWeight.Bold를 지정할 필요가 없음
                    //    AnnotatedString 내에서 스타일을 정의했기 때문입니다.
                    //    원본 코드에서 지정했던 Gray 색상과 Modifier는 여기에 적용합니다.
                    // fontWeight = FontWeight.Bold, // 주석 처리 또는 제거
                    // color = Color.Gray, // AnnotatedString 내 SpanStyle에서 이미 적용됨.
                    // 여기에서 지정하면 AnnotatedString 내의 color를 덮어씁니다.
                    // 일관성을 위해 AnnotatedString 내에서 처리하는 것을 추천합니다.
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .offset(y = (-100).dp)
                )
            }

        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Box의 하단 중앙에 배치
                .padding(bottom = 200.dp), // 하단 여백 조절
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .offset(y = 0.dp)
                    .padding(horizontal = 10.dp),
            ){LoginPageButton("프로필 작성하기", {})}
        }

}
}
/*
@Preview(showBackground = true, heightDp = 800)
@Composable
fun SignUpPreview5() {
    SingInScreen_4()
}*/