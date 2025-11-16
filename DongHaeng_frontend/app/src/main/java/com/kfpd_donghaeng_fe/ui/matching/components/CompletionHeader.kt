package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.R

// ReviewScreen.kt
@Composable
fun CompletionHeader(
    totalTime: String,
    distance: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                AppColors.ReviewBackground,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(vertical = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 체크 아이콘
            Image(
                painter = painterResource(id = R.drawable.ic_check_circle),
                contentDescription = null,
                modifier = Modifier.size(40.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "동행이 완료되었습니다",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryDarkText
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "실제 소요시간: $totalTime / 이동거리: $distance",
                fontSize = 13.sp,
                color = AppColors.SecondaryText
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCompletionHeader() {
    CompletionHeader(
        totalTime = "18분",
        distance = "2.1km"
    )
}