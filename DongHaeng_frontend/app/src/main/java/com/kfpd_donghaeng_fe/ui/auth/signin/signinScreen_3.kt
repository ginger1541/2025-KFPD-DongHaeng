package com.kfpd_donghaeng_fe.ui.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.domain.entity.auth.MakeAccountUiState


// 필요한 색상 정의 (테마에 없다면 임시 사용)
val BrandOrange = Color(0xFFE67E22) // 예시 색상
val MainOrange = Color(0xFFE67E22)
val DarkGray = Color(0xFF555555)
val LightGray = Color(0xFFE0E0E0)
val ErrorRed = Color(0xFFE53935)
val SuccessGreen = Color(0xFF4CAF50)

@Composable
fun SignInScreen_3(uiState: MakeAccountUiState,
                   onNextClick: () -> Unit,
                    ) {
    // --- 상태 관리 (State) ---
    var nickname by remember { mutableStateOf("동행하는우인이") } // 예시값
    var introduction by remember { mutableStateOf("") }

    // 닉네임 유효성 검사 로직 (예시: 비어있지 않으면 성공으로 간주)
    val isNicknameValid = nickname.isNotEmpty()
   

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 32.dp ,top = 40.dp), // 상단 여백
            contentAlignment = Alignment.Center
        ) {
            StepCircle(3)
        }
        // 메인 스크롤 가능한 컨텐츠 영역
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // 스크롤 가능하게 설정
                .padding(top=70.dp,bottom = 100.dp) )// 하단 버튼 공간 확보
         {
            // 1. 상단 헤더 (뒤로가기 + 인디케이터)


            // 2. 타이틀 영역
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "프로필 설정",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MainOrange,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "다른 사용자들이 볼 수 있는 기본 정보를 입력해주세요.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // 3. 프로필 이미지 업로드 영역
            ProfileImageSection()

            Spacer(modifier = Modifier.height(40.dp))

            // 4. 입력 폼 영역
            InputFormSection(
                nickname = nickname,
                onNicknameChange = { nickname = it },
                introduction = introduction,
                onIntroChange = { introduction = it },
                isNicknameValid = isNicknameValid
            )
        }

        // 5. 하단 고정 버튼
        Button(
            onClick = onNextClick,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandOrange,
                disabledContainerColor = Color.Gray
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = isNicknameValid // 닉네임이 유효할 때만 버튼 활성화
        ) {
            Text(
                text = "다음",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun TopHeaderSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(
            imageVector = Icons.Default.ArrowBack,
            contentDescription = "Back",
            modifier = Modifier.size(24.dp)
        )

        // 인디케이터 (StepCircle 대용)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MainOrange))
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LightGray))
            Spacer(modifier = Modifier.width(4.dp))
            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LightGray))
        }

        // 오른쪽 균형을 위한 투명 박스
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
fun ProfileImageSection() {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 원형 이미지 홀더
        Box(
            modifier = Modifier
                .size(140.dp)
                .clip(CircleShape)
                .border(2.dp, LightGray, CircleShape)
                .background(Color(0xFFF5F5F5))
                .clickable { /* 이미지 피커 오픈 로직 */ },
            contentAlignment = Alignment.Center
        ) {
            // 이미지가 없을 때 아이콘 등을 표시 (여기선 비워둠)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "*얼굴이 보이는 사진을 업로드해주세요.",
            style = MaterialTheme.typography.labelMedium,
            color = ErrorRed,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun InputFormSection(
    nickname: String,
    onNicknameChange: (String) -> Unit,
    introduction: String,
    onIntroChange: (String) -> Unit,
    isNicknameValid: Boolean
) {
    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
        // --- 닉네임 필드 ---
        Text(
            text = "닉네임*",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("닉네임을 입력하세요") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if(isNicknameValid) SuccessGreen else BrandOrange,
                unfocusedBorderColor = LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 성공 메시지 (조건부 렌더링)
        if (isNicknameValid) {
            Text(
                text = "사용 가능한 이름입니다.",
                style = MaterialTheme.typography.labelMedium,
                color = SuccessGreen,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 자기소개 필드 ---
        Text(
            text = "자기소개(100자미만)",
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
            value = introduction,
            onValueChange = { if (it.length <= 100) onIntroChange(it) }, // 100자 제한
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp), // 높이 고정
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandOrange,
                unfocusedBorderColor = LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}


/*
@Preview(showBackground = true, heightDp = 800)
@Composable
fun SignUpPreview4() {
    SignInScreen_3()
}*/