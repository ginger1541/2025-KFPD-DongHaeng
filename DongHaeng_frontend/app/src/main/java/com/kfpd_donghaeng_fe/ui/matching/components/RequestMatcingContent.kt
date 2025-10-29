package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun RequesterMatchingContent() {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text(
            text = "현재 2명의 도우미가 활동 중이에요",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryDarkText
        )

        Text(
            text = "예상 매칭 시간: 3-8분",
            fontSize = 14.sp,
            color = AppColors.SecondaryText,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { /* 동행 요청 로직 */ },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp)
            ) {
                Text(
                    text = "동행 요청하기",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}