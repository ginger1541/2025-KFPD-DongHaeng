package com.kfpd_donghaeng_fe.ui.auth.signin

import androidx.compose.foundation.background

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kfpd_donghaeng_fe.ui.auth.LoginPageButton

import com.kfpd_donghaeng_fe.ui.theme.MainOrange

@Composable
fun SignIngScreen_0(){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top=40.dp)// 배경색은 흰색으로 가정
            .padding(horizontal = 25.dp) // 좌우 전체 패딩

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp ,top = 280.dp), // 상단 여백

            contentAlignment = Alignment.TopStart
        ) {
            StepCircle(0) // 이 컴포넌트가 좌측 정렬됩니다.
        }
        // 1️⃣ [상단 & 중앙 영역]: 제목, 회원가입 링크, 입력 필드
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 320.dp), // 상단 여백
            horizontalAlignment = Alignment.CenterHorizontally, // 중앙 정렬
        ) {

            Row() {
                Text(
                    text = "당신의 프로필",
                    color = MainOrange,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "을 만들어보세요",
                    color = Color.Black,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold
                )

            }
            Text(
                modifier = Modifier
                    .offset(x=-27.dp)
                    .padding(top=25.dp),
                text = "동행을 위한 당신의 프로필을 만들어주세요",
                color = Color.Gray,
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start // 텍스트 내용을 좌측 정렬
            )


        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter) // Box의 하단 중앙에 배치
                .padding(bottom = 390.dp), // 하단 여백 조절
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .offset(y = 0.dp)
                    .padding(horizontal = 20.dp),
            ){LoginPageButton("다음", {})}
        }
    }
}


@Composable
fun StepCircle(cnt:Int){
    val ColorArray: Array<Color> = arrayOf(
      Color.LightGray,Color.LightGray,Color.LightGray)
    for (i in 0..cnt-1){
        ColorArray[i] = MainOrange
    }
    Row(){
        for (color in ColorArray){
            Box(
                modifier = Modifier
                    .padding(end= 5.dp)
                    .size(10.dp) // 너비와 높이를 같게 지정 (정사각형)
                    .clip(CircleShape) // ➡️ 모양을 원형으로 자릅니다. (핵심)
                    .background(color)


            )

        }

    }


}


@Preview(showBackground = true)
@Composable
fun singupPreview2() {
    SignIngScreen_0()
}