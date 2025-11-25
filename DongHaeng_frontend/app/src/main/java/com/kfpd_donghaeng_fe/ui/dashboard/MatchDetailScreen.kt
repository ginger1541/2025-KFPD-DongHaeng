package com.kfpd_donghaeng_fe.ui.dashboard

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.data.remote.dto.MatchRequestDetails
import com.kfpd_donghaeng_fe.ui.theme.*
import com.kfpd_donghaeng_fe.util.navigateToOngoingScreen
import com.kfpd_donghaeng_fe.viewmodel.dashboard.MatchDetailViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(
    matchId: Long,
    navController: NavController,
    viewModel: MatchDetailViewModel = hiltViewModel()
) {
    // TODO: 실제 로그인한 유저 ID를 가져와야 함
    val myUserId = 12345L

    // 화면 진입 시 데이터 로드
    LaunchedEffect(matchId) {
        viewModel.loadMatchDetail(matchId, myUserId)
    }

    val matchDetail by viewModel.matchDetail.collectAsState()
    val partnerInfo by viewModel.partnerInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // 다이얼로그 상태
    var showCancelDialog by remember { mutableStateOf(false) }
    var showCompleteDialog by remember { mutableStateOf(false) }

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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            if (isLoading || matchDetail == null) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = BrandOrange
                )
            } else {
                val request = matchDetail!!.request

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    // 1. 예약확인 카드
                    Text(
                        text = "예약확인",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    ReservationCard(request)

                    Spacer(modifier = Modifier.height(32.dp))

                    // 2. 요청사항
                    Text(
                        text = "요청사항",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = request.description ?: "작성된 요청사항이 없습니다.",
                            color = if (request.description != null) Color(0xFF666666) else Color(0xFFAAAAAA),
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 3. 상대방 정보 (전화번호 포함)
                    partnerInfo?.let { partner ->
                        Text(
                            text = "동행자 정보", // 상황에 따라 텍스트 변경 가능
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextBlack,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        UserInfoRow(label = "이름", value = partner.name)
                        Spacer(modifier = Modifier.height(8.dp))
                        UserInfoRow(label = "전화번호", value = partner.phone ?: "비공개")
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // 4. 환불정책
                    Text(
                        text = "환불정책",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextBlack,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    Surface(
                        color = Color(0xFFF5F5F5),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "동행 시작 1시간 전까지 취소 시 수수료가 발생하지 않습니다. 이후 취소 시에는 포인트 차감이 발생할 수 있습니다.",
                            color = Color(0xFF888888),
                            fontSize = 12.sp,
                            lineHeight = 18.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 5. 예상 금액
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "예상금액: ",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = TextBlack
                        )
                        Text(
                            text = "${request.route?.estimatedPrice ?: 0}원",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandOrange
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // 6. 하단 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showCancelDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE0E0E0)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Text("예약 취소", color = Color(0xFF888888), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { navController.navigateToOngoingScreen(matchId) },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).height(56.dp)
                        ) {
                            Text("동행 시작하기", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // 취소 다이얼로그
    if (showCancelDialog) {
        CancelReasonDialog(
            onDismiss = { showCancelDialog = false },
            onConfirm = {
                showCancelDialog = false
                showCompleteDialog = true
            }
        )
    }

    // 취소 완료 다이얼로그
    if (showCompleteDialog) {
        AlertDialog(
            onDismissRequest = {},
            containerColor = Color.White,
            title = { Text("취소 완료", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Text(
                    "동행 취소가 완료되었습니다.\n확인 버튼을 누르면 동행 화면으로 이동합니다.",
                    fontSize = 14.sp,
                    color = TextBlack,
                    textAlign = TextAlign.Center
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCompleteDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

// --- 내부 컴포넌트 ---

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ReservationCard(request: MatchRequestDetails) {
    // API 문서의 scheduledAt 활용 (ISO 8601)
    val zdt = try {
        ZonedDateTime.parse(request.scheduledAt)
    } catch (e: Exception) {
        ZonedDateTime.now()
    }
    val dateStr = zdt.format(DateTimeFormatter.ofPattern("M월 d일", Locale.KOREA))
    val timeStr = zdt.format(DateTimeFormatter.ofPattern("HH시 mm분", Locale.KOREA))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = dateStr,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextBlack
            )
            Spacer(modifier = Modifier.height(20.dp))

            // 경로 표시 (점선)
            Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(24.dp)
                ) {
                    // 출발 링
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .border(2.dp, BrandOrange, CircleShape)
                            .background(Color.White, CircleShape)
                    )
                    // 점선
                    Canvas(modifier = Modifier.weight(1f).width(1.dp).padding(vertical = 4.dp)) {
                        drawLine(
                            color = LightGray,
                            start = Offset(0f, 0f),
                            end = Offset(0f, size.height),
                            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f),
                            strokeWidth = 2.dp.toPx()
                        )
                    }
                    // 도착 링
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .border(2.dp, BrandOrange, CircleShape)
                            .background(BrandOrange, CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxHeight()
                ) {
                    // 출발지
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = request.startAddress,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextBlack,
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = "$timeStr 출발", fontSize = 12.sp, color = DarkGray)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    // 도착지
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = request.destinationAddress,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = TextBlack,
                            modifier = Modifier.weight(1f)
                        )
                        Text(text = "${request.estimatedMinutes ?: "-"}분 소요", fontSize = 12.sp, color = DarkGray)
                    }
                }
            }
        }
    }
}

@Composable
fun UserInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(4.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label : ",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = TextBlack
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = TextBlack
        )
    }
}

@Composable
fun CancelReasonDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    var reason by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color.White,
        title = { Text("취소 사유", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
        text = {
            OutlinedTextField(
                value = reason,
                onValueChange = { reason = it },
                placeholder = { Text("사유를 입력해주세요", color = Color.Gray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BrandOrange,
                    unfocusedBorderColor = LightGray
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth(),
                enabled = reason.isNotBlank()
            ) {
                Text("확인", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                Text("취소", color = Color.Gray)
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}