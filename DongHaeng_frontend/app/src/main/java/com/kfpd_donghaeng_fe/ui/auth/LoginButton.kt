package com.kfpd_donghaeng_fe.ui.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.ui.theme.MainOrange


@Composable
fun LoginPageButton(Text: String, Onclick: () -> Unit) {
    Button(
        onClick = Onclick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            // 🎨 수정: 버튼 배경색 변경
            containerColor = MainOrange,
            contentColor = Color.White
        )
    ) {
        Text(
            text = Text,
            fontSize = 15.sp, // 텍스트 크기 약간 키움 (12sp -> 15sp 추천)
            color = Color.White,
            fontWeight = FontWeight.Bold // 버튼 텍스트는 굵게 하는 게 예쁩니다
        )
    }
}