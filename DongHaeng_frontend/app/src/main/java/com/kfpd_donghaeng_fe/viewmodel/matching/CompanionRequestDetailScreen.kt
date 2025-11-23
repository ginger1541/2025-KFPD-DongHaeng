package com.kfpd_donghaeng_fe.ui.matching

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.data.remote.dto.CompanionRequestDetailDto
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.viewmodel.matching.CompanionRequestDetailViewModel
import com.kfpd_donghaeng_fe.viewmodel.matching.RequestDetailUiState
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute
import com.kfpd_donghaeng_fe.domain.entity.RoutePoint
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CompanionRequestDetailScreen(
    requestId: Long,
    onBackClick: () -> Unit,
    onMatchSuccess: (Long) -> Unit,
    viewModel: CompanionRequestDetailViewModel = hiltViewModel()
) {
    LaunchedEffect(requestId) {
        viewModel.loadDetail(requestId)
    }

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_logo_orange),
                        contentDescription = "Logo",
                        tint = Color.Unspecified,
                        modifier = Modifier.height(24.dp)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is RequestDetailUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AppColors.AccentOrange)
                }
                is RequestDetailUiState.Error -> {
                    Text(text = state.message, modifier = Modifier.align(Alignment.Center))
                }
                is RequestDetailUiState.Success -> {
                    RequestDetailContent(
                        data = state.data,
                        onAccept = {
                            viewModel.acceptRequest(requestId, onMatchSuccess)
                        }
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RequestDetailContent(
    data: CompanionRequestDetailDto,
    onAccept: () -> Unit
) {
    val scrollState = rememberScrollState()

    val walkingRoute = remember(data.route) {
        data.route?.points?.let { points ->
            WalkingRoute(
                points = points.map { RoutePoint(it.lng, it.lat) },
                totalDistance = data.route.totalDistanceMeters ?: 0,
                totalTime = 0
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // 1. 지도 (패딩과 라디우스 추가)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(240.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            KakaoMapView(
                modifier = Modifier.fillMaxSize(),
                locationX = data.startLongitude,
                locationY = data.startLatitude,
                route = walkingRoute,
                enabled = true
            )
        }

        Column(modifier = Modifier.padding(24.dp)) {
            // 2. 예약 확인
            Text("예약확인", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            RouteInfoCard(data)

            Spacer(modifier = Modifier.height(32.dp))

            // 3. 요청사항
            Text("요청사항", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = data.description ?: "요청사항이 없습니다.",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 14.sp,
                    color = Color(0xFF555555),
                    lineHeight = 22.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 4. 예약자 정보
            Text("예약자 정보", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("예약자 정보는 요청 수락 후 확인할 수 있습니다.", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            BlindInfoRow("이름")
            Spacer(modifier = Modifier.height(8.dp))
            BlindInfoRow("나이")
            Spacer(modifier = Modifier.height(8.dp))
            BlindInfoRow("이메일")

            Spacer(modifier = Modifier.height(32.dp))

            // 5. 환불 정책
            Text("환불정책", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Surface(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "8월 11일 오후 3:00 이전에 취소하면 수수료 없이 취소할 수 있습니다...",
                    modifier = Modifier.padding(16.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF888888),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 6. 하단 수락 버튼
            AcceptBottomBar(
                price = data.route?.estimatedPrice ?: 0,
                onAccept = onAccept
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RouteInfoCard(data: CompanionRequestDetailDto) {
    val zdt = runCatching { ZonedDateTime.parse(data.scheduledAt) }.getOrDefault(ZonedDateTime.now())
    val dateStr = zdt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
    val timeStr = zdt.format(DateTimeFormatter.ofPattern("HH시 mm분", Locale.KOREA))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE0E0E0)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(dateStr, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(20.dp))

            // 출발
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).border(2.dp, AppColors.AccentOrange, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Text(data.startAddress, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text("$timeStr 출발", fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 도착
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(12.dp).border(2.dp, AppColors.AccentOrange, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Text(data.destinationAddress, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                Text("${data.estimatedMinutes}분 소요 예상", fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Composable
fun BlindInfoRow(label: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "$label : ",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(60.dp)
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .height(24.dp)
                .background(Color(0xFFF0F0F0), RoundedCornerShape(4.dp))
        )
    }
}

@Composable
fun AcceptBottomBar(price: Int, onAccept: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("예상금액: ", fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("${price}원", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.AccentOrange)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onAccept,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange)
        ) {
            Text("요청 수락하기", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, heightDp = 1200)
@Composable
fun PreviewCompanionRequestDetailScreen() {
    // 1. Mock Requester 정보
    val mockRequester = com.kfpd_donghaeng_fe.data.remote.dto.RequesterProfileDto(
        id = 12345,
        name = "홍길동",
        profileImageUrl = null,
        companionScore = 4.5
    )

    // 2. Mock Route 정보 (경로 좌표 포함)
    val mockRoute = com.kfpd_donghaeng_fe.data.remote.dto.RouteInfoDto(
        estimatedPrice = 6000,
        totalDistanceMeters = 1500,
        points = listOf(
            com.kfpd_donghaeng_fe.data.remote.dto.PointDto(37.5665, 126.9780),
            com.kfpd_donghaeng_fe.data.remote.dto.PointDto(37.5670, 126.9785),
            com.kfpd_donghaeng_fe.data.remote.dto.PointDto(37.5675, 126.9790)
        )
    )

    // 3. Mock 상세 데이터 (DTO)
    val mockDetailData = com.kfpd_donghaeng_fe.data.remote.dto.CompanionRequestDetailDto(
        id = 1001,
        title = "광주 금남로까지 동행 부탁드립니다",
        description = "제가 휠체어 사이즈가 커서 무거울 수도 있어요. 계단 이동 시 도움이 필요합니다.",
        startAddress = "서강대학교 인문대학 1호관",
        destinationAddress = "루프 홍대점",
        startLatitude = 37.5665,
        startLongitude = 126.9780,
        destinationLatitude = 37.5675,
        destinationLongitude = 126.9790,
        estimatedMinutes = 20,
        scheduledAt = java.time.ZonedDateTime.now().plusHours(1).toString(),
        route = mockRoute,
        requester = mockRequester
    )

    // 4. UI 미리보기 (bottomBar 제거, onAccept 파라미터 추가)
    MaterialTheme {
        RequestDetailContent(
            data = mockDetailData,
            onAccept = {}
        )
    }
}