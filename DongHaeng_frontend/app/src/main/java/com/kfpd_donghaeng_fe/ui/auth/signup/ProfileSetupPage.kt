package com.kfpd_donghaeng_fe.ui.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.theme.*

/** 페이지 2: 프로필 설정 Composable */
@Composable
fun ProfileSetupPage(
    // ViewModel로부터 받을 상태
    nickname: String,
    bio: String,
    // ViewModel로 전달할 이벤트
    onNicknameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "프로필 설정",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBlack // 적용
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "다른 사용자들이 볼 수 있는 기본 정보를 입력해 주세요.",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkGray // 적용
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- 프로필 사진 ---
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape) // 원형으로 자르기
                .background(color=Color.White) // 적용
                .align(Alignment.CenterHorizontally) // 컬럼 내에서 수평 중앙 정렬
                .clickable { /* TODO: 갤러리/카메라 열기 */ },
            contentAlignment = Alignment.Center // 내용물(아이콘)을 중앙에
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_camera),
                contentDescription = "프로필 사진 추가",
                modifier = Modifier.size(40.dp),
                tint = BrandOrange // 아이콘 색상 적용
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "* 얼굴이 보이는 사진을 업로드 해주세요.",
            style = MaterialTheme.typography.bodySmall,
            color = DarkGray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- 닉네임 입력 ---
        Text("닉네임", style = MaterialTheme.typography.labelLarge, color = TextBlack)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            placeholder = { Text("닉네임을 입력하세요") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            // TODO: TextField 스타일 커스텀 (색상 등)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "사용 가능한 이름입니다.", // TODO: 닉네임 유효성 검사 로직 필요
            style = MaterialTheme.typography.bodySmall,
            color = DarkGray // (유효하면 BrandOrange, 아니면 다른색)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- 자기소개 입력 ---
        Text("자기소개 (100자 이내)", style = MaterialTheme.typography.labelLarge, color = TextBlack)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = bio,
            onValueChange = onBioChange, // 100자 제한은 ViewModel에서 이미 처리
            placeholder = { Text("자신을 소개해주세요") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp), // 높이 지정
            maxLines = 5
        )

        // --- '다음' 버튼 (하단 고정) ---
        Spacer(modifier = Modifier.weight(1f)) // 버튼 하단으로 밀기

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange), // 적용
            shape = RoundedCornerShape(8.dp),
            // TODO: 닉네임이 비어있으면 버튼 비활성화
            // enabled = nickname.isNotBlank()
        ) {
            Text("다음", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp)) // 하단 여백
    }
}