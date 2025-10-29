package com.kfpd_donghaeng_fe.ui.matching.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.ui.theme.AppColors.AccentOrange
import com.kfpd_donghaeng_fe.ui.theme.AppColors.DividerColor
import com.kfpd_donghaeng_fe.ui.theme.AppColors.LightGrayButton
import com.kfpd_donghaeng_fe.ui.theme.AppColors.LocationDotColor
import com.kfpd_donghaeng_fe.ui.theme.AppColors.PrimaryDarkText
import com.kfpd_donghaeng_fe.viewmodel.matching.MatchingViewModel
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RequestConfirmContent(
    viewModel: MatchingViewModel,
    onFinalRequest: () -> Unit,
    onEdit: () -> Unit // BOOKING 단계로 돌아가는 함수
) {
    // ViewModel에서 Mock 데이터를 가져옵니다.
    val confirmedRoute = viewModel.confirmedRoute.value
    val confirmedTimes = viewModel.confirmedTimes.value // (출발 시간, 도착 시간)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text(
            text = "예약확인",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDarkText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // 날짜 (예: 8월 13일)
        Text(
            text = viewModel.selectedDateTime.value.format(DateTimeFormatter.ofPattern("M월 d일")),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryDarkText,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        // 1. 경로 목록 및 시간 표시
        Column(modifier = Modifier.fillMaxWidth()) {
            confirmedRoute.forEachIndexed { index, location ->
                RouteConfirmationItem(
                    locationName = location,
                    time = when (index) {
                        0 -> confirmedTimes.first // 출발 시간
                        confirmedRoute.lastIndex -> confirmedTimes.second // 도착 시간
                        else -> null // 경유지는 시간 없음
                    },
                    isFirst = index == 0,
                    isLast = index == confirmedRoute.lastIndex
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp)) // 버튼과의 간격

        // 2. 버튼 영역
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onEdit, // 수정 버튼 클릭 시 BOOKING 단계로 복귀
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = LightGrayButton),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("수정", color = PrimaryDarkText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onFinalRequest,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccentOrange),
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("확인", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// 경로의 각 항목 UI (Location Dot, Name, Time)
@Composable
fun RouteConfirmationItem(
    locationName: String,
    time: String?,
    isFirst: Boolean,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 40.dp),
        verticalAlignment = Alignment.Top
    ) {
        // 1. 상태/구분선
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!isFirst) {
                Divider(
                    color = DividerColor,
                    modifier = Modifier
                        .width(2.dp)
                        .height(20.dp)
                        .background(Color.White) // 배경색과 동일하게 처리
                )
            }
            // 위치 원형 아이콘
            Box(
                modifier = Modifier
                    .size(if (isFirst || isLast) 12.dp else 8.dp)
                    .background(LocationDotColor, shape = RoundedCornerShape(6.dp))
            )
            if (!isLast) {
                // 아래로 내려가는 점선
                Divider(
                    color = DividerColor,
                    modifier = Modifier
                        .width(2.dp)
                        .height(40.dp)
                        .background(Color.White)
                )
            }
        }

        // 2. 위치명 및 시간
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = locationName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryDarkText,
                modifier = Modifier.weight(1f)
            )

            if (time != null) {
                Text(
                    text = time,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryDarkText,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
