package com.kfpd_donghaeng_fe.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.data.remote.dto.MatchItemResponse
import com.kfpd_donghaeng_fe.ui.theme.*
import com.kfpd_donghaeng_fe.viewmodel.dashboard.ScheduleViewModel
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScheduleScreen(
    navController: NavHostController,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val activeMatches by viewModel.activeMatches.collectAsState()
    val pastMatches by viewModel.pastMatches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val tabTitles = listOf("예약중", "동행 완료")
    val pagerState = rememberPagerState(pageCount = { tabTitles.size })
    val coroutineScope = rememberCoroutineScope()

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
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1. 탭 바
            TabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.White,
                contentColor = DarkGray,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                        color = BrandOrange,
                        height = 3.dp
                    )
                },
                divider = { HorizontalDivider(color = LightGray, thickness = 1.dp) }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 16.sp,
                                color = if (pagerState.currentPage == index) BrandOrange else MediumGray
                            )
                        }
                    )
                }
            }

            // 2. 리스트 영역 (weight(1f)로 남은 공간 채움 -> UI 깨짐 해결)
            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = BrandOrange)
                }
            } else {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.weight(1f)
                ) { page ->
                    val matches = if (page == 0) activeMatches else pastMatches

                    if (matches.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = if(page==0) "예약된 동행이 없습니다." else "완료된 동행 내역이 없습니다.",
                                color = MediumGray
                            )
                        }
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(matches) { match ->
                                MatchItemCard(
                                    match = match,
                                    onClick = {
                                        // 버튼 클릭 시 상세 페이지로 이동
                                        navController.navigate("match_detail_screen/${match.matchId}")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchItemCard(match: MatchItemResponse, onClick: () -> Unit) {
    // ✅ 날짜 파싱: API의 scheduledAt 사용
    val zdt = try {
        ZonedDateTime.parse(match.request.scheduledAt)
    } catch (e: Exception) {
        ZonedDateTime.now()
    }

    val dateStr = zdt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
    val timeStr = zdt.format(DateTimeFormatter.ofPattern("HH시 mm분", Locale.KOREA))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color(0xFFF0F0F0)),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // ✅ 카드 전체가 버튼 역할
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                // 1. 날짜 헤더
                Text(
                    text = dateStr,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = DarkGray
                )
                Spacer(modifier = Modifier.height(20.dp))

                // 2. 출발지 -> 도착지 경로 뷰 (점선 포함)
                // ✅ startAddress, destinationAddress 사용
                RouteInfoView(
                    startLoc = match.request.startAddress,
                    endLoc = match.request.destinationAddress,
                    startTime = timeStr,
                    estTime = "${match.request.estimatedMinutes}분 소요 예상"
                )
            }

            // 3. 도장 (완료/취소 상태일 때만 우측 하단 표시)
            if (match.status == "completed" || match.status == "cancelled") {
                StampBadge(
                    status = match.status,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 20.dp, bottom = 20.dp)
                )
            }
        }
    }
}

@Composable
fun RouteInfoView(startLoc: String, endLoc: String, startTime: String, estTime: String) {
    Row(modifier = Modifier.height(IntrinsicSize.Min)) {
        // 왼쪽 그래픽 (출발/도착 링 + 점선)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(24.dp)
        ) {
            // 출발점
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .border(2.dp, BrandOrange, CircleShape)
                    .background(Color.White, CircleShape)
            )
            // 점선
            Canvas(
                modifier = Modifier
                    .weight(1f)
                    .width(1.dp)
                    .padding(vertical = 4.dp)
            ) {
                drawLine(
                    color = LightGray,
                    start = Offset(0f, 0f),
                    end = Offset(0f, size.height),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                    strokeWidth = 2.dp.toPx()
                )
            }
            // 도착점
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .border(2.dp, BrandOrange, CircleShape)
                    .background(Color.White, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // 오른쪽 텍스트 정보
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxHeight()
        ) {
            // 출발
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = startLoc,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextBlack,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "$startTime 출발", fontSize = 12.sp, color = DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 도착
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = endLoc,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = TextBlack,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "$estTime 도착", fontSize = 12.sp, color = DarkGray)
            }
        }
    }
}

@Composable
fun StampBadge(status: String, modifier: Modifier = Modifier) {
    val (text, color) = if (status == "completed") {
        "동행\n완료" to BrandOrange
    } else {
        "예약\n취소" to Color(0xFFFFB74D)
    }

    Box(
        modifier = modifier
            .size(72.dp)
            .rotate(-15f)
            .border(3.dp, color, CircleShape)
            .background(Color.White.copy(alpha = 0.9f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = color,
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            lineHeight = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}