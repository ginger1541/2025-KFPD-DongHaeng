// main/java/com/kfpd_donghaeng_fe/ui/matching/components/PaymentContent.kt
package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.common.CommonDialog
import com.kfpd_donghaeng_fe.ui.theme.AppColors

// PaymentContent - Mockup image_b268c2.jpg의 하단 시트 + 예약 완료 팝업
@Composable
fun PaymentContent(
    onConfirm: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier,
    estimatedPrice: String = "6,000원", // TODO: ViewModel에서 계산된 값 사용
    points: Int = 0
) {
    var showCompletionDialog by remember { mutableStateOf(false) } // State for popup

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
            .verticalScroll(rememberScrollState()) // 스크롤 가능하게
    ) {
        Text(
            text = "결제수단",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryDarkText
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. 결제 카드 이미지 (Mockup)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            //
            Box(
                modifier = Modifier
                    .width(240.dp)
                    .height(150.dp)
                    .background(Color.Black, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                // TODO: 실제 카드 이미지 리소스로 교체
                Text("Credit Card Mockup", color = Color.White, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // 2. 포인트/쿠폰 (Mockup)
        PaymentOptionRow(
            iconRes = R.drawable.ic_plus_circle, // 임시 아이콘
            label = "포인트",
            value = "${points}p",
            onToggle = { /* 포인트 사용/미사용 */ }
        )
        PaymentOptionRow(
            iconRes = R.drawable.ic_minus_circle, // 임시 아이콘
            label = "쿠폰",
            value = "쿠폰 없음",
            onToggle = { /* 쿠폰 선택 */ }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. 예상 금액
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "예상 금액: ",
                fontSize = 16.sp,
                color = AppColors.PrimaryDarkText
            )
            Text(
                text = estimatedPrice,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.AccentOrange
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. 버튼 영역 (수정/결제하기)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEdit,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.LightGrayButton),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("수정", color = AppColors.PrimaryDarkText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { showCompletionDialog = true }, // 💡 팝업 띄우기
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("결제하기", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    // 5. 예약 완료 팝업 (CommonDialog 사용)
    if (showCompletionDialog) {
        CommonDialog(
            title = "예약 완료",
            message = "동행 예약이 완료되었습니다.\n근처 동행자에게 알림 메시지를 보냅니다.\n\n" +
                    "// TODO: 요청 생성 및 채팅방 생성 API 호출 (현재 주석 처리)\n" + // 💡 서버 API TODO 추가
                    "[확인] 버튼을 누르면 홈 화면으로 이동합니다.", // 💡 텍스트 수정: 홈 화면으로 이동
            onDismiss = {
                showCompletionDialog = false
                onConfirm() // 팝업 닫고 최종 완료 액션 실행 (Home으로 이동 요청)
            },
            cancelText = "확인",
            onConfirm = null
        )
    }
}

@Composable
fun PaymentOptionRow(
    iconRes: Int,
    label: String,
    value: String,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = AppColors.SecondaryText,
                modifier = Modifier.size(24.dp)
            )
            Spacer(Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 16.sp,
                color = AppColors.PrimaryDarkText
            )
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 16.sp,
                color = AppColors.PrimaryDarkText
            )
            Spacer(Modifier.width(12.dp))
            // 토글 버튼 (Mockup)
            Switch(
                checked = false, // Mock
                onCheckedChange = { onToggle() },
                colors = SwitchDefaults.colors(
                    checkedTrackColor = AppColors.AccentOrange,
                    uncheckedThumbColor = Color(0xFFEEEEEE)
                )
            )
        }
    }
}