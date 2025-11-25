package com.kfpd_donghaeng_fe.ui.auth.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.ui.auth.LoginPageButton
import com.kfpd_donghaeng_fe.ui.theme.MainOrange



//TODO: 109 줄 oncick 이벤트 해결
// 로그인 버튼 <- 로그인로직 구현
// closed eye 아이콘 추가 + 비번 보이게/안보이게 로직 추가


@Composable
fun LoginPage(
    uiState: LoginAccountUiState,
    onNextClick: () -> Unit,
    MovetoMain: () -> Unit,
) {
    // 로그인 입력 상태 관리
    var idText by remember { mutableStateOf("") }
    var pwText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 25.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(200.dp))

        // 1. 제목: "로그인"
        Text(
            text = "로그인",
            color = Color.Black,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 2. 아이디 입력 필드
        LoginTextField(
            value = idText,
            onValueChange = { idText = it },
            label = "아이디(이메일)"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 비밀번호 입력 필드
        LoginTextField(
            value = pwText,
            onValueChange = { pwText = it },
            label = "비밀번호",
            isPassword = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 4. 로그인 버튼
        LoginPageButton("로그인", MovetoMain)

        Spacer(modifier = Modifier.height(16.dp))

        // 5. 회원가입 링크
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "아직 계정이 없으신가요?  ",
                color = Color.Gray,
                style = MaterialTheme.typography.titleSmall,
            )
            Text(
                modifier = Modifier.clickable {
                    onNextClick()
                },
                text = "회원가입하기",
                color = MainOrange,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun LoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String, // "아이디" 또는 "비밀번호" 텍스트
    isPassword: Boolean = false // 비밀번호 모드 여부
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(text = label) },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),

        // 🔒 비밀번호 시각적 변환
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,

        // ⌨️ 키보드 옵션
        keyboardOptions = KeyboardOptions(
            keyboardType = if (isPassword) KeyboardType.Password else KeyboardType.Email,
            imeAction = if (isPassword) ImeAction.Done else ImeAction.Next
        ),

        // 🎨 색상 커스텀 (MainOrange 테마 적용)
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MainOrange,
            focusedLabelColor = MainOrange,
            cursorColor = MainOrange,
            unfocusedContainerColor = Color.White,
            focusedContainerColor = Color.White
        )
    )
}

/*
@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    LoginScreen()
}*/