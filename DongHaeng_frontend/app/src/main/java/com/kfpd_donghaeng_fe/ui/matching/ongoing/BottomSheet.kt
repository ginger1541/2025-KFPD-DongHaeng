package com.kfpd_donghaeng_fe.ui.matching.ongoing

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import kotlinx.coroutines.CoroutineScope
import androidx.compose.ui.zIndex
import com.kfpd_donghaeng_fe.GlobalApplication
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.viewmodel.matching.OngoingViewModel

// TODO :  카메라 인식, 상단 padding 없애기





// 임시 배경


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
        shape = RoundedCornerShape(16.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) OnButtonColor else OffButtonColor,
            contentColor = if (isEnabled) Color.White else Color.Black
        )
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}



@Composable
fun BtnEndDH(nextPage: () -> Unit ,modifier: Modifier = Modifier) {
    BtnSet(text = "동행종료", modifier = modifier, onClick =  nextPage, isEnabled = isBtnEndDHEnabled)
}

// QR 버튼
@Composable
fun BtnQR(requestScan: (matchId: Long, qrType: QRTypes) -> Unit,onClick: () -> Unit) {
    val QRCamImg = painterResource(id = R.drawable.qr_cam_icon)
    Button(
        onClick = {

            val matchId = 1L // 실제 Match ID
            //val qrType = if (page == 0) QRTypes.START else QRTypes.END
            val qrType=QRTypes.START
            val scanData = QRScandEntity.Empty
             requestScan(matchId, QRTypes.START)

                  },
        modifier = Modifier
            .width(200.dp)
            .height(160.dp), // 직사각형
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(width = 3.dp, color = Color.LightGray)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().padding(1.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = QRCamImg,
                contentDescription = "QR_Code_Scanner_Icon",
                modifier = Modifier.fillMaxWidth().aspectRatio(1.3f)
            )
        }
    }
}

// 버튼 묶음
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetButtonBatch(scope: CoroutineScope, sheetState: SheetState, onCloseRequest: () -> Unit, page: Int, nextPage:()->Unit, onEndDH: () -> Unit) {
    Row(
        // fillMaxWidth()를 유지하고, horizontalArrangement = Arrangement.Center 로 버튼을 중앙에 배치
        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
        horizontalArrangement = Arrangement.Center, // 👈 중앙 정렬
        verticalAlignment = Alignment.CenterVertically
    ) {
        when(page) {
            1 -> {
                BtnEndDH(
                    nextPage,
                    // 버튼의 너비를 160.dp로 고정하여 길이를 줄입니다.
                    modifier = Modifier.width(160.dp)
                        .height(50.dp)
                )
            }
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
fun SheetMiddle( requestScan: (matchId: Long, qrType: QRTypes) -> Unit, nextPage: () -> Unit, page: Int) {
    Spacer(modifier = Modifier.height(16.dp)) // 패딩 줄임
    when(page) {
        0,2 -> BtnQR(requestScan, onClick = nextPage)
        1 -> {
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("시작 시간", style = MaterialTheme.typography.titleMedium, color = Color.Gray.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("18:20", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                }
                Divider(color = Color.Black.copy(alpha = 0.5f), modifier = Modifier.height(90.dp).width(1.dp))
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("이동 거리", style = MaterialTheme.typography.titleMedium, color = Color.Gray.copy(alpha = 0.8f))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("0.0Km", style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), color = Color.Black)
                }


            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

// 시트 내부 전체
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetInside(requestScan: (matchId: Long, qrType: QRTypes) -> Unit,scope: CoroutineScope, sheetState: SheetState, onCloseRequest: () -> Unit, nextPage:()->Unit,page: Int,onEndDH: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SheetTop(page)
        SheetMiddle(requestScan,nextPage,page)
        Spacer(modifier = Modifier.height(20.dp)) // 간격 줄임
        SheetButtonBatch(
            scope, sheetState, onCloseRequest, page,
            nextPage,
            onEndDH = onEndDH,
        )
    }
}

// BottomSheet Scaffold
// BottomSheet Scaffold (수정: 시트 초기 크기 고정)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(uiState: OngoingEntity,
                resultUiState: QRScanResultEntity,
                locateUiState:QRScandEntity,
                requestScan: (matchId: Long, qrType: QRTypes) -> Unit,
                nextPage:()->Unit,
                NavigateToReview: () -> Unit
) {
    val page = uiState.OngoingPage
    val nextPage =nextPage
    val onEndDH = { NavigateToReview() }
    val scope = rememberCoroutineScope()


    // SheetValue.Expanded 대신 PartiallyExpanded를 사용하여 콘텐츠 길이에 맞게 초기화합니다.
    val bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Expanded, skipHiddenState = false) // skipHiddenState를 false로 변경할 수 있습니다.
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 350.dp, // 접혔을 때 높이
        sheetShape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp), // 둥근 정도 줄임
        sheetContainerColor = Color.White,
        sheetDragHandle = {
            BottomSheetDefaults.DragHandle(color = Color.Gray, width = 40.dp) // 얇게
        },
        sheetContent = {
            SheetInside( requestScan ,scope, bottomSheetState, onCloseRequest = {}, nextPage= nextPage,page = page,onEndDH = onEndDH,)
        },
        content = {
            //Background_Map()
        }
    )
}
