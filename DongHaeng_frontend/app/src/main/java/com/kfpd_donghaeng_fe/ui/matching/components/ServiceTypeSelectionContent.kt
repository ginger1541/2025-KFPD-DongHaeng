// main/java/com/kfpd_donghaeng_fe/ui/matching/components/ServiceTypeSelectionContent.kt
package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.kfpd_donghaeng_fe.ui.theme.AppColors

enum class ServiceType {
    SIMPLE_MOVE, // 단순 이동
    ACTIVITY_SUPPORT // 활동 지원
}

// ServiceTypeSelectionContent - Mockup image_b268c6.jpg의 하단 시트
@Composable
fun ServiceTypeSelectionContent(
    routeReady: Boolean,
    onSelect: (ServiceType) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedType by remember { mutableStateOf<ServiceType?>(null) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "서비스 유형",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryDarkText
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 1. 단순 이동 카드
        ServiceTypeCard(
            iconResId = R.drawable.ic_needy, // 임시 아이콘 (적절한 아이콘이 없어 임시 사용)
            title = "단순 이동",
            description = "출발지부터 도착지까지 동행합니다.",
            isSelected = selectedType == ServiceType.SIMPLE_MOVE,
            onClick = { selectedType = ServiceType.SIMPLE_MOVE }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. 활동 지원 카드
        ServiceTypeCard(
            iconResId = R.drawable.ic_helper, // 임시 아이콘
            title = "활동 지원",
            description = "도착지 이후의 활동까지 동행합니다.",
            isSelected = selectedType == ServiceType.ACTIVITY_SUPPORT,
            onClick = { selectedType = ServiceType.ACTIVITY_SUPPORT }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 3. 다음 버튼
        Button(
            onClick = { selectedType?.let(onSelect) },
            enabled = routeReady && selectedType != null, // 경로가 준비되고 유형이 선택되어야 활성화
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.AccentOrange,
                disabledContainerColor = Color(0xFFE0E0E0)
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                "예약 시간 선택",
                color = if (routeReady && selectedType != null) Color.White else Color(0xFF9E9E9E),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ServiceTypeCard(
    iconResId: Int,
    title: String,
    description: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) AppColors.AccentOrange else Color(0xFFE0E0E0)
    val backgroundColor = if (isSelected) AppColors.ReviewBackground else Color(0xFFF9F9F9)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = BorderStroke(2.dp, borderColor),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = title,
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryDarkText
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = AppColors.SecondaryText
                )
            }
        }
    }
}