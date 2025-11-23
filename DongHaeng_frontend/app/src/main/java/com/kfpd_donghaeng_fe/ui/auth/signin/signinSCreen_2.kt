package com.kfpd_donghaeng_fe.ui.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.domain.entity.auth.MakeAccountUiState
import com.kfpd_donghaeng_fe.ui.theme.BrandOrange
import com.kfpd_donghaeng_fe.ui.theme.DarkGray
import com.kfpd_donghaeng_fe.ui.theme.MainOrange



var toptext="장애인증 인증 안내"
var middleimg=R.drawable.ic_card_ex

@Composable
fun SignInScreen_2 (uiState: MakeAccountUiState,
                    onNextClick: () -> Unit,
                   ){
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp ,top = 40.dp), // 상단 여백
        contentAlignment = Alignment.Center
    ) {
        StepCircle(2)
    }
    // 💡 전체 화면을 Box로 감싸서 콘텐츠와 하단 버튼을 분리합니다.
    Box(modifier = Modifier.fillMaxSize()) {
        // 1️⃣ [콘텐츠 영역]: 제목, 설명, 카드들 (스크롤 가능한 영역)
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 카드 내부와 동일한 좌우 여백을 주어 정렬을 맞춥니다.
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally// 하단 버튼 공간 확보
        ) {
            Text(
                modifier = Modifier
                    .padding(top = 70.dp),
                text = toptext,
                style = MaterialTheme.typography.headlineLarge,
                color = MainOrange,
                fontWeight = FontWeight.ExtraBold

            )
            Spacer(modifier = Modifier.height(5.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "본인확인을 위해 신분증을 준비해 주세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "다음 화면에서 촬영해 주세요.",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Image(
                painter = painterResource(id = middleimg),
                contentDescription = "pic_ex",
                modifier = Modifier.size(350.dp)
            )
            Text(
                modifier = Modifier
                    .padding(5.dp),
                text="•  등록증 인증은 고객의 실명을 확인하기 위한 용도입니다.\n " +
                        "•  흰색 또는 검정색 바탕에 올려놓고 빛반사에 주의하여 촬영해주세요.\n" +
                        "(장애인 등록증 사진에 빛 반사가 되어 촬영된 경우 실명확인을 처음부터 다시 진행해야 함으로 유의해주세요.)",
                style = MaterialTheme.typography.bodySmall,
                color = DarkGray,
                fontWeight = FontWeight.ExtraBold
            )

        }
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Box 하단 중앙에 고정
                .fillMaxWidth()
                .padding(horizontal = 25.dp) // 콘텐츠와 동일한 좌우 패딩
                .padding(bottom = 24.dp) // 하단 여백
        ) {
            // LoginPageButton 컴포넌트를 사용합니다. (이름이 "다음" 대신 "완료"로 가정)

            // 💡 버튼은 enabled 속성으로 선택 상태에 따라 활성화/비활성화됩니다.
            Button(
                onClick =  onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(8.dp),
                // 사용자 유형이 선택되지 않으면 버튼 비활성화

            ) {
                Text("완료", color = Color.White)
            }
        }
    }
}


/*
@Preview(showBackground = true)
@Composable
fun singupPreview3() {
    SignInScreen_2()
}*/