package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun RewardBox(
    points: Int,
    volunteerTime: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "획득 보상",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryDarkText
            )

            Text(
                text = "오늘의 동행으로 얻게 된 보상입니다.",
                fontSize = 13.sp,
                color = AppColors.SecondaryText,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Divider(color = Color(0xFFEEEEEE))

            Spacer(modifier = Modifier.height(16.dp))

            // 포인트 적립
            RewardRow(
                label = "포인트 적립",
                value = "+ ${points}p"
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 봉사 시간
            RewardRow(
                label = "봉사 시간",
                value = volunteerTime
            )
        }
    }
}

@Composable
fun RewardRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = AppColors.PrimaryDarkText,
            fontWeight = FontWeight.Medium
        )
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 점선 효과
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp)
                    .height(1.dp)
            ) {
                val dashWidth = 4.dp.toPx()
                val dashGap = 4.dp.toPx()
                val totalWidth = size.width

                var currentX = 0f
                while (currentX < totalWidth) {
                    drawLine(
                        color = androidx.compose.ui.graphics.Color(0xFFCCCCCC),
                        start = Offset(currentX, 0f),
                        end = Offset((currentX + dashWidth).coerceAtMost(totalWidth), 0f),
                        strokeWidth = 1.dp.toPx()
                    )
                    currentX += dashWidth + dashGap
                }
            }

            Text(
                text = value,
                fontSize = 15.sp,
                color = AppColors.PrimaryDarkText,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewRewardBox() {
    RewardBox(
        points = 250,
        volunteerTime = "0h 9m"
    )
}