package com.kfpd_donghaeng_fe.ui.matching

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.matching.components.QrVerificationDialog
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun RequestDetailScreen(
    request: Request,
    onBackClick: () -> Unit = {},
    onAcceptClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var showQrDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
        ) {
            // 상단 오렌지 프로필 헤더
            ProfileHeader()

            Spacer(modifier = Modifier.height(16.dp))

            // 동행 정보 섹션
            TripInfoSection(request = request)

            Spacer(modifier = Modifier.height(16.dp))

            // 하단 버튼 - 스크롤해야 보임
            Button(
                onClick = { showQrDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.AccentColor
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "동행시작",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // QR 다이얼로그
        if (showQrDialog) {
            QrVerificationDialog(
                onDismiss = { showQrDialog = false },
                onConfirm = {
                    showQrDialog = false
                    onAcceptClick()
                }
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                AppColors.AccentColor,
                shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
            )
            .padding(top = 48.dp, bottom = 32.dp)
    ) {
        // 알림 아이콘 (우측 상단)
        IconButton(
            onClick = { /* 알림 */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 8.dp)
        ) {
            Icon(
                Icons.Default.Notifications,
                contentDescription = "알림",
                tint = Color.White
            )
        }

        // 프로필 정보 (중앙)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 프로필 이미지
            Image(
                painter = painterResource(id = R.drawable.ic_user_profile),
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 이름
            Text(
                text = "동행하는 우인이",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 아이디
            Text(
                text = "@donghang1234",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.8f)
            )
        }
    }
}

@Composable
private fun TripInfoSection(
    request: Request,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // 동행 정보 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "동행 정보",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryDarkText
            )
            TextButton(onClick = { /* 인증하기 */ }) {
                Text(
                    text = "인증하기",
                    color = AppColors.AccentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AppColors.AccentOrange,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 지도 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                KakaoMapView(
                    locationX = 126.97796919,
                    locationY = 37.56661209,
                    enabled = true
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 지도 하단 정보
        Text(
            text = "총 1시간 10분 / 4.2km / 30days",
            fontSize = 12.sp,
            color = AppColors.SecondaryText,
            modifier = Modifier.align(Alignment.End)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 동행 내역 헤더
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "동행 내역",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryDarkText
            )
            TextButton(onClick = { /* 더보기 */ }) {
                Text(
                    text = "더보기",
                    color = AppColors.AccentOrange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = AppColors.AccentOrange,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 동행 내역 카드
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                // 요청 사항
                Text(
                    text = "요청 사항",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryDarkText
                )
                Text(
                    text = "동행자가 입력한 요청 사항 입니다.",
                    fontSize = 13.sp,
                    color = AppColors.SecondaryText,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color(0xFFEEEEEE))

                Spacer(modifier = Modifier.height(16.dp))

                // 획득 포인트
                InfoRow(
                    label = "획득 포인트",
                    value = "+ ${request.pricePoints} p"
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 이동 시간
                InfoRow(
                    label = "이동 시간",
                    value = request.departureTime.replace("출발", "").trim()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 동행 시간
                InfoRow(
                    label = "동행 시간",
                    value = request.travelTime.replace("약", "").replace("소요", "").trim()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider(color = Color(0xFFEEEEEE))

                Spacer(modifier = Modifier.height(16.dp))

                // 총 봉사 시간
                InfoRow(
                    label = "총 봉사 시간",
                    value = "2h 00m"
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
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
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Canvas로 점선 그리기
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
/*
@Preview(showBackground = true, heightDp = 1200)
@Composable
fun PreviewRequestDetailScreen() {
    val dummyRequest = Request(
        id = 1,
        departure = "연세대학교 세브란스 병원",
        arrival = "동국대학교 서울캠퍼스",
        departureTime = "10분 후 출발",
        travelTime = "약 18분 소요",
        distance = "0.5km",
        pricePoints = 3100
    )

    RequestDetailScreen(request = dummyRequest)
}*/