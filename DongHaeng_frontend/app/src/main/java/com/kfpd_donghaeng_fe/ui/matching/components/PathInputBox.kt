package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.ui.theme.MediumGray

@Composable
fun PathInputBox(
    startLocation: RouteLocation?,
    endLocation: RouteLocation?,
    isSelectingStart: Boolean,
    onLocationClick: (isStart: Boolean) -> Unit,
    onClose: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            // 상단 상태바 영역만큼 패딩이 필요할 수 있음 (WindowInsets 활용 권장)
            .statusBarsPadding()
            .padding(bottom = 16.dp)
    ) {
        // 1. 상단바 (뒤로가기 + 입력 박스)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 뒤로가기 버튼
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = AppColors.PrimaryDarkText
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // 메인 입력 박스 (통합된 스트로크)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 왼쪽: 출발/도착 입력칸 컬럼
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        // 출발지 입력 행
                        LocationInputRowWithIcon(
                            place = startLocation,
                            placeholder = "출발",
                            iconResId = R.drawable.ic_start_dot,
                            iconTint = Color(0xFF4A90E2), // 파란색
                            onSelect = { onLocationClick(true) }
                        )

                        // 구분선
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )

                        // 도착지 입력 행
                        LocationInputRowWithIcon(
                            place = endLocation,
                            placeholder = "도착",
                            iconResId = R.drawable.ic_end_dot,
                            iconTint = Color(0xFFE25B4A), // 빨간색
                            onSelect = { onLocationClick(false) }
                        )
                    }

                    // 오른쪽: 교체 아이콘 (Swap)
                    IconButton(
                        onClick = { /* TODO: 출발/도착지 교체 로직 구현 필요 */ },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_swap),
                            contentDescription = "출발/도착 교체",
                            tint = MediumGray,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            }
        }

        // 2. 하단 태그 (집/회사) - 디자인 유지
        Row(
            modifier = Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeCompanyTag("집", R.drawable.ic_home)
            HomeCompanyTag("회사", R.drawable.ic_home)
        }
    }
}

@Composable
fun LocationInputRowWithIcon(
    place: RouteLocation?,
    placeholder: String,
    iconResId: Int,
    iconTint: Color,
    onSelect: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 아이콘
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(12.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 텍스트
        if (place != null) {
            Text(
                text = place.placeName,
                color = AppColors.PrimaryDarkText,
                fontSize = 16.sp,
                maxLines = 1
            )
        } else {
            Text(
                text = placeholder,
                color = MediumGray, // 플레이스홀더 색상
                fontSize = 16.sp
            )
        }
    }
}

// HomeCompanyTag는 기존과 동일하게 유지
@Composable
fun HomeCompanyTag(label: String, iconResId: Int) {
    Row(
        modifier = Modifier
            .background(Color(0xFFF5F5F5), RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            tint = MediumGray,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            color = AppColors.PrimaryDarkText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}