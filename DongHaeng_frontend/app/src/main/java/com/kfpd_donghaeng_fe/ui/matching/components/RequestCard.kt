// main/java/com/kfpd_donghaeng_fe/ui/matching/components/RequestCard.kt
package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun RequestCard(
    request: Request,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // 상단: 날짜와 거리
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = request.date, // ✅ "오늘" 또는 "11월 25일" (ViewModel에서 처리됨)
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryDarkText
                )
                Text(
                    text = request.distance, // ✅ "내 위치에서 1.2km" (ViewModel에서 처리됨)
                    fontSize = 12.sp,
                    color = AppColors.SecondaryText
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 출발지
            LocationTimeRow(
                location = request.departure,
                time = request.departureTime, // ✅ "오후 2시 출발" (ViewModel에서 처리됨)
                isStart = true
            )

            // 중간 연결선
            VerticalDottedLine(
                modifier = Modifier
                    .padding(start = 11.dp) // 원의 중심(12dp)에 맞춤 (원 크기 24dp일 때)
                    .height(24.dp)
            )

            // 도착지
            LocationTimeRow(
                location = request.arrival,
                time = request.arrivalTime, // ✅ "오후 3시 도착" (ViewModel에서 처리됨)
                isStart = false
            )
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
        // 동그라미 아이콘 (출발/도착 구분 가능)
        Box(
            modifier = Modifier
                .size(24.dp) // 전체 크기
                .background(
                    color = if (isStart) AppColors.AccentOrange.copy(alpha = 0.1f) else Color.Transparent,
                    shape = CircleShape
                )
                .then(if (!isStart) Modifier.background(Color(0xFFF5F5F5), CircleShape) else Modifier), // 도착은 회색 배경 등 처리 가능
            contentAlignment = Alignment.Center
        ) {
            // 내부 작은 점
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = if (isStart) AppColors.AccentOrange else Color.Gray,
                        shape = CircleShape
                    )
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 위치명
        Text(
            text = location,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.PrimaryDarkText,
            modifier = Modifier.weight(1f)
        )

        // 시간 (점선 없이 바로 표시하거나, 필요 시 점선 추가)
        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = time,
            fontSize = 14.sp,
            color = AppColors.SecondaryText
        )
    }
}

@Composable
fun VerticalDottedLine(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.width(2.dp)) { // 터치 영역 확보 위해 width 약간 줌
        val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        drawLine(
            color = Color.LightGray,
            start = Offset(center.x, 0f),
            end = Offset(center.x, size.height),
            strokeWidth = 2.dp.toPx(),
            pathEffect = pathEffect
        )
    }
}

//// 미리보기
//@Preview(showBackground = true)
//@Composable
//fun RequestCardPreview() {
//    MaterialTheme {
//        RequestCard(
//            request = Request(
//                date = "8월 13일",
//                distance = "0.5km",
//                departure = "서강대학교 인문대학 1호관",
//                departureTime = "17시 10분",
//                arrival = "루프 홍대점",
//                arrivalTime = "17시 30분",
//                id = 1,
//                pricePoints = 1
//
//            ),
//            onClick = {}
//        )
//    }
//}
