// main/java/com/kfpd_donghaeng_fe/ui/matching/components/RequestCard.kt
package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun RequestCard(
    request: Request,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        // 우측 상단 distance (박스 밖)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = request.distance,
                fontSize = 11.sp,
                color = AppColors.SecondaryText
            )
        }

        // 스트로크로 감싸진 내용
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = AppColors.SecondaryText.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(12.dp),
            color = AppColors.CardBackground
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 날짜
                Text(
                    text = request.date,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryDarkText
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 출발지
                LocationTimeRow(
                    location = request.departure,
                    time = request.departureTime,
                    isStart = true
                )

                // 중간 연결선
                VerticalSolidLine(
                    modifier = Modifier
                        .padding(start = 9.dp)
                        .height(24.dp)
                )

                // 도착지
                LocationTimeRow(
                    location = request.arrival,
                    time = request.arrivalTime,
                    isStart = false
                )
            }
        }
    }
}

@Composable
fun LocationTimeRow(
    location: String,
    time: String,
    isStart: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 이미지 아이콘
        Image(
            painter = painterResource(
                id = if (isStart) R.drawable.ic_start_dot else R.drawable.ic_end_dot
            ),
            contentDescription = if (isStart) "출발지" else "도착지",
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 위치명
        Text(
            text = location,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.PrimaryDarkText
        )

        // 점선으로 연결
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            HorizontalDashedLine()
        }

        // 시간
        Text(
            text = time,
            fontSize = 14.sp,
            color = AppColors.SecondaryText
        )
    }
}

@Composable
fun VerticalSolidLine(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(2.dp)
    ) {
        Divider(
            color = Color.LightGray,
            thickness = 2.dp,
            modifier = Modifier.fillMaxHeight()
        )
    }
}

@Composable
fun HorizontalDashedLine() {
    androidx.compose.foundation.Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color.LightGray,
            start = androidx.compose.ui.geometry.Offset(0f, size.height / 2),
            end = androidx.compose.ui.geometry.Offset(size.width, size.height / 2),
            strokeWidth = 1.dp.toPx(),
            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                floatArrayOf(8f, 4f),
                0f
            )
        )
    }
}