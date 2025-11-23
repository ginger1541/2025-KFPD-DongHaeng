package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

// 💡 홈/회사 버튼은 Mockup 이미지를 기반으로 임시로 추가합니다.
@Composable
fun PathInputBox(
    startLocation: RouteLocation?,
    endLocation: RouteLocation?,
    isSelectingStart: Boolean, // 현재 어떤 필드가 활성화(검색 대기) 상태인지
    onLocationClick: (isStart: Boolean) -> Unit,
    onClose: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // 1. 출발지/도착지 입력 Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            // (1) 출발/도착 아이콘
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 출발지 마커 (오렌지)
                Icon(
                    painter = painterResource(id = R.drawable.ic_start_dot),
                    contentDescription = "출발지",
                    tint = Color.Unspecified, // XML의 색상 사용
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // 구분선 (회색 선)
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(24.dp)
                        .background(MediumGray)
                )
                Spacer(modifier = Modifier.height(4.dp))
                // 도착지 마커 (회색)
                Icon(
                    painter = painterResource(id = R.drawable.ic_end_dot),
                    contentDescription = "도착지",
                    tint = Color.Unspecified, // XML의 색상 사용
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // (2) 입력 필드
            Column(modifier = Modifier.weight(1f)) {
                // 출발지 입력 필드
                LocationInputRow(
                    place = startLocation,
                    placeholder = "출발지 입력",
                    onSelect = { onLocationClick(true) },
                    isActive = isSelectingStart
                )
                Spacer(modifier = Modifier.height(16.dp))
                // 도착지 입력 필드
                LocationInputRow(
                    place = endLocation,
                    placeholder = "도착지 입력",
                    onSelect = { onLocationClick(false) },
                    isActive = !isSelectingStart
                )
            }

            // (3) X 버튼 (닫기)
            IconButton(onClick = onClose) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close_gray),
                    contentDescription = "닫기",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Divider(
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            color = AppColors.LightGray
        )

        // 2. 홈/회사 버튼 (Mockup)
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HomeCompanyTag("집", R.drawable.ic_home)
            HomeCompanyTag("회사", R.drawable.ic_home)
        }
    }
}

// 경로 입력 Row 컴포넌트
@Composable
fun LocationInputRow(
    place: RouteLocation?,
    placeholder: String,
    onSelect: () -> Unit,
    isActive: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(if (isActive) AppColors.LightGray else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onSelect)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        if (place != null) {
            Text(
                text = place.placeName,
                color = AppColors.PrimaryDarkText,
                fontSize = 16.sp
            )
        } else {
            Text(
                text = placeholder,
                color = AppColors.SecondaryText,
                fontSize = 16.sp
            )
        }
    }
}

// 홈/회사 태그 컴포넌트
@Composable
fun HomeCompanyTag(label: String, iconResId: Int) {
    Row(
        modifier = Modifier
            .background(AppColors.LightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            tint = AppColors.SecondaryText,
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