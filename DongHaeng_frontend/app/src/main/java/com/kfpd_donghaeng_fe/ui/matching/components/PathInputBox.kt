package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
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
    onSwapClick: () -> Unit,
    onLocationClick: (isStart: Boolean) -> Unit,
    onClose: () -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(bottom = 16.dp)
    ) {
        // 1. 상단 네비게이션 바 (뒤로가기 버튼만)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_chevron_left),
                    contentDescription = "뒤로가기",
                    tint = AppColors.PrimaryDarkText
                )
            }
        }

        // 2. 입력 박스 (출발/도착지)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 왼쪽: 교체 아이콘 (Swap)
                IconButton(
                    onClick = onSwapClick,
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_swap),
                        contentDescription = "출발/도착 교체",
                        modifier = Modifier.size(28.dp)
                    )
                }

                // 중앙: 출발/도착 입력칸 컬럼
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // 출발지 입력 행
                    LocationInputRowWithIcon(
                        place = startLocation,
                        placeholder = "출발",
                        iconResId = R.drawable.ic_start_dot,
                        // 💡 [수정] 현재 출발지를 선택 중인지(isSelectingStart == true) 전달
                        isActive = isSelectingStart,
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
                        // 💡 [수정] 현재 도착지를 선택 중인지(isSelectingStart == false) 전달
                        isActive = !isSelectingStart,
                        onSelect = { onLocationClick(false) }
                    )
                }
            }

            // 우측 상단: X 버튼 (Clear)
            IconButton(
                onClick = onClear,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "지우기",
                    tint = MediumGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3. 하단 태그 (집/회사)
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
    isActive: Boolean,
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
        Image(
            painter = painterResource(id = iconResId),
            contentDescription = null,
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
                color = MediumGray,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun HomeCompanyTag(label: String, iconResId: Int) {
    Surface(
        modifier = Modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        shadowElevation = 4.dp  // 그림자 추가
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconResId),
                contentDescription = label,
                tint = AppColors.AccentColor,
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
}