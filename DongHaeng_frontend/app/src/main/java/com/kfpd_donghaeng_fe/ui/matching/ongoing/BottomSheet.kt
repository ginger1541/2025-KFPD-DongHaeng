package com.kfpd_donghaeng_fe.ui.matching.ongoing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingViewModel
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.zIndex
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView

// TODO : bottomsheet 초기 크기 고정, 카메라 인식, 상단 padding 없애기






// 임시 배경
@Composable
fun Background_Map() {
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        KakaoMapView(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(0f),
            locationX = 126.97796919,
            locationY = 37.56661209,
            enabled = true
        )
    }
}

// 버튼 색상 및 활성화
val OffButtonColor = Color(0xFFE0E0E0)
val OnButtonColor = Color(0xFFF09040)
val isBtnStartDHEnabled = true
val isBtnEndDHEnabled = true
val isBtnSOSEnabled = false
val isBtnShareLocationEnabled = false

// 공통 버튼
@Composable
fun BtnSet(text: String, modifier: Modifier = Modifier, onClick: () -> Unit, isEnabled: Boolean) {
    Button(
        onClick = onClick,
        modifier = modifier.height(40.dp), // 약간 더 납작하게
        shape = RoundedCornerShape(8.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) OnButtonColor else OffButtonColor,
            contentColor = if (isEnabled) Color.White else Color.Black
        )
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}

// 동행 버튼
@Composable
fun BtnStartDH(viewModel: OngoingViewModel, modifier: Modifier = Modifier) {
    BtnSet(text = "동행시작", modifier = modifier, onClick = { viewModel.nextPage() }, isEnabled = isBtnStartDHEnabled)
}

@Composable
fun BtnEndDH(viewModel: OngoingViewModel, modifier: Modifier = Modifier) {
    BtnSet(text = "동행종료", modifier = modifier, onClick = { viewModel.nextPage() }, isEnabled = isBtnEndDHEnabled)
}

// SOS 버튼
@Composable
fun BtnSOS(onClick: () -> Unit, modifier: Modifier = Modifier) {
    BtnSet(text = "SOS", modifier = modifier, onClick = onClick, isEnabled = isBtnSOSEnabled)
}

// 위치공유 버튼
@Composable
fun BtnShareLocation(onClick: () -> Unit, modifier: Modifier = Modifier) {
    BtnSet(text = "위치공유", modifier = modifier, onClick = onClick, isEnabled = isBtnShareLocationEnabled)
}

// QR 버튼
@Composable
fun BtnQR(onClick: () -> Unit) {
    val QRCamImg = painterResource(id = R.drawable.qr_cam_icon)
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(120.dp)
            .height(100.dp), // 직사각형
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(width = 2.dp, color = Color.LightGray)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(1.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = QRCamImg,
                contentDescription = "QR_Code_Scanner_Icon",
                modifier = Modifier.fillMaxWidth().aspectRatio(1f)
            )
        }
    }
}

// 버튼 묶음
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetButtonBatch(scope: CoroutineScope, sheetState: SheetState, onCloseRequest: () -> Unit, page: Int, viewModel: OngoingViewModel, onEndDH: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp), // 높이 줄임
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BtnSOS(onClick = {}, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        BtnShareLocation(onClick = {}, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(8.dp))
        when(page) {
            0 -> BtnStartDH(viewModel, Modifier.weight(1f))
            1,2 -> BtnEndDH(viewModel, Modifier.weight(1f))
        }
    }
}

// 상단 멘트
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetTop(page: Int) {
    Spacer(modifier = Modifier.height(4.dp)) // padding 최소화
    val (firstMent, secondMent) = when(page) {
        0 -> "상대방과 만나셨나요?" to "QR 코드를 스캔하여 동행을 시작하세요."
        1 -> "동행이 시작되었습니다." to "목적지 도착 후 동행 종료 QR 코드를 스캔하세요."
        2 -> "목적지에 도착하셨나요?" to "마지막으로 QR코드를 스캔하여 동행을 종료하세요"
        else -> "잘못된 페이지!" to ""
    }
    Text(text = firstMent, style = MaterialTheme.typography.headlineSmall, color = Color.Black.copy(alpha = 0.9f), fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(4.dp))
    Text(text = secondMent, style = MaterialTheme.typography.titleSmall, color = Color.Gray)
}

// 중간 컨텐츠
@Composable
fun SheetMiddle(page: Int) {
    Spacer(modifier = Modifier.height(16.dp)) // 패딩 줄임
    when(page) {
        0,2 -> BtnQR(onClick = {})
        1 -> {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("시작 시간", style = MaterialTheme.typography.titleMedium, color = Color.Gray.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("18:20", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                }
                Divider(color = Color.Black.copy(alpha = 0.5f), modifier = Modifier.height(60.dp).width(1.dp))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("이동 거리", style = MaterialTheme.typography.titleMedium, color = Color.Gray.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("0.0Km", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                }
            }
        }
    }
}

// 시트 내부 전체
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetInside(scope: CoroutineScope, sheetState: SheetState, onCloseRequest: () -> Unit, page: Int, viewModel: OngoingViewModel, onEndDH: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SheetTop(page)
        SheetMiddle(page)
        Spacer(modifier = Modifier.height(20.dp)) // 간격 줄임
        SheetButtonBatch(
            scope, sheetState, onCloseRequest, page, viewModel,
            onEndDH = onEndDH,
        )
    }
}

// BottomSheet Scaffold
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(viewModel: OngoingViewModel = viewModel(), onNavigateToReview: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val page = uiState.OngoingPage
    val onEndDH = { onNavigateToReview() }
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded, skipHiddenState = true)
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 40.dp, // 낮게
        sheetShape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp), // 둥근 정도 줄임
        sheetContainerColor = Color.White,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.Gray, width = 40.dp) // 얇게
        },
        sheetContent = {
            SheetInside(scope, bottomSheetState, onCloseRequest = {}, page = page, viewModel = viewModel, onEndDH = onEndDH,)
        },
        content = {
            Background_Map()
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultAppPreview() {
    KFPD_DongHaeng_FETheme(dynamicColor = false) {
        BottomSheet(
            viewModel = TODO(),
            onNavigateToReview = TODO()
        )
    }
}
