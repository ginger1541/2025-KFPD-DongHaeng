package com.kfpd_donghaeng_fe.ui.auth.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.auth.UserType
import com.kfpd_donghaeng_fe.ui.theme.*

/** 페이지 3: 사용자 유형 Composable */
@Composable
fun UserTypePage(
    // ViewModel로부터 받을 상태
    selectedType: UserType?, // 현재 선택된 유형 (NEEDY, HELPER, or null)
    // ViewModel로 전달할 이벤트
    onUserTypeSelect: (UserType) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "사용자 유형",
            style = MaterialTheme.typography.headlineSmall,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "주로 어떤 용도로 서비스를 이용하실 예정인가요?",
            style = MaterialTheme.typography.bodyMedium,
            color = DarkGray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // --- "도움이 필요해요" 카드 ---
        SelectableCard(
            iconResId = R.drawable.ic_needy,
            text = "도움이 필요해요",
            subtext = "이동이나 일상생활에서 도움이 필요해요.",
            isSelected = (selectedType == UserType.NEEDY),
            onClick = { onUserTypeSelect(UserType.NEEDY) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // --- "도움을 드릴게요" 카드 ---
        SelectableCard(
            iconResId = R.drawable.ic_helper,
            text = "도움을 드릴게요",
            subtext = "다른 분들의 이동과 활동을 돕고 싶어요.",
            isSelected = (selectedType == UserType.HELPER),
            onClick = { onUserTypeSelect(UserType.HELPER) }
        )

        // --- '다음' 버튼 ---
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onNextClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
            shape = RoundedCornerShape(8.dp),
            // 사용자 유형이 선택되지 않으면 버튼 비활성화
            enabled = (selectedType != null)
        ) {
            Text("다음", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}


/** 사용자 유형 선택 카드 Composable (재사용) */
@Composable
private fun SelectableCard(
    iconResId: Int,
    text: String,
    subtext: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 선택 상태에 따라 배경색과 테두리색 변경
    val backgroundColor = if (isSelected) LightOrange else MediumGray
    val borderColor = if (isSelected) BrandOrange else Color.Transparent

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() } // 클릭 이벤트
            .padding(horizontal = 24.dp, vertical = 20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = text,
                modifier = Modifier.size(50.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleLarge,
                    color = TextBlack
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.bodyMedium,
                    color = DarkGray
                )
            }
        }
    }
}