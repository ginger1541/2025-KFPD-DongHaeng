package com.kfpd_donghaeng_fe.ui.matching.ongoing



import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import kotlinx.coroutines.CoroutineScope
import android.util.Log // Log.d 사용을 위해 import

// ... (OngoingScreen 함수 생략)

/**
 * UI 상태를 받아 실제 화면을 그리는 Composable (Preview를 위해 분리)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OngoingScreenContent(
    currentPage: Int,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onFinalEndClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded,
        skipHiddenState = true
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    // **1. QR 스캐너 표시 상태 (버튼 클릭 시 제어)**
    var isQrScannerVisible by remember { mutableStateOf(false) }

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 80.dp,
        sheetShape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
        sheetContainerColor = Color.White,
        sheetDragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray, width = 80.dp) },
        sheetContent = {
            SheetInsideWithEvents(
                scope = scope,
                sheetState = bottomSheetState,
                page = currentPage,
                isQrScannerVisible = isQrScannerVisible, // 상태 전달
                onShowQrScanner = { isQrScannerVisible = true }, // 스캐너 표시 요청
                onHideQrScanner = { isQrScannerVisible = false }, // 스캐너 숨김 요청
                onStartClick = onStartClick,
                onEndClick = onEndClick,
                onFinalEndClick = onFinalEndClick
            )
        }
    ) { paddingValues ->
        // BottomSheetScaffold의 content 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 배경 지도 (BottomSheet.kt에서 import)
            Background_Map()

            // TopSheet UI
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = Color(0xFFEA7A34), // MainOrange
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            ) {
                // Batch (TopSheet_userinfo.kt에서 import)
                Batch(
                    requestPlaceContent = { RequestPlace(department = "신림 현대아파트", arrival = "장군봉 근린공원") },
                    userProfileContent = { UserProfile(name = "춤추는 무지", DH_score = 87) },
                    contactContent = { Contact() },
                    distanceText = "약속 장소까지 0.8km",
                    progressStep = currentPage // ViewModel의 상태를 전달
                )
            }
        }
    }
}

/**
 * ViewModel 이벤트 연결을 위해 SheetInsideWithEvents를 다시 정의합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SheetInsideWithEvents(
    scope: CoroutineScope,
    sheetState: SheetState,
    page: Int,
    isQrScannerVisible: Boolean, // 추가: QR 스캐너 표시 여부
    onShowQrScanner: () -> Unit,  // 추가: 스캐너 표시 이벤트
    onHideQrScanner: () -> Unit,  // 추가: 스캐너 숨김 이벤트
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onFinalEndClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        SheetTop(page)

        // **2. SheetMiddle에 스캐너 상태 및 이벤트 전달**
        SheetMiddle(
            page = page,
            isQrScannerVisible = isQrScannerVisible,
            onShowQrScanner = onShowQrScanner,
            onHideQrScanner = onHideQrScanner,
            onQrScanSuccess = { qrCode ->
                Log.d("SheetMiddle", "QR Code Detected: $qrCode")

                // QR 코드 인식 후 스캐너 숨김
                onHideQrScanner()

                // page 0 (시작 전) -> 동행 시작 API/로직 처리 후 다음 단계(1)로 이동
                if (page == 0) {
                    // TODO: 서버에 QR 코드(qrCode)를 보내 동행 시작 인증
                    onStartClick()
                }
                // page 2 (종료 전) -> 동행 종료 API/로직 처리 후 최종 완료
                else if (page == 2) {
                    // TODO: 서버에 QR 코드(qrCode)를 보내 동행 종료 인증
                    onFinalEndClick()
                }
            }
        )

        Spacer(modifier = Modifier.height(40.dp))

        SheetButtonBatchWithEvents(
            page = page,
            onStartClick = onStartClick,
            onEndClick = onEndClick,
            onFinalEndClick = onFinalEndClick
        )
    }
}


/**
 * 기존 BottomSheet.kt의 SheetMiddle 함수를 QrScannerView를 사용하도록 수정합니다.
 */
@Composable
fun SheetMiddle(
    page: Int,
    isQrScannerVisible: Boolean, // 추가: QR 스캐너 표시 여부
    onShowQrScanner: () -> Unit, // 추가: 스캐너 표시 이벤트
    onHideQrScanner: () -> Unit, // 추가: 스캐너 숨김 이벤트
    onQrScanSuccess: (String) -> Unit // QR 인식 성공 시 콜백 추가
) {
    Spacer(modifier = Modifier.height(32.dp))
    when (page) {
        0, 2 -> {
            // **3. isQrScannerVisible 상태에 따라 UI 분기**
            if (isQrScannerVisible) {
                // QR 스캐너가 보일 때
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(280.dp)
                ) {
                    QrScannerView(
                        onQrCodeDetected = onQrScanSuccess
                    )
                }
                // 스캐너가 보일 때 닫기 버튼을 추가할 수도 있습니다.
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(onClick = onHideQrScanner) {
                        Text("QR 스캐너 닫기")
                    }
                }
            } else {
                // QR 스캐너가 보이지 않을 때 (버튼 표시)
                // BtnQR이 기존 BottomSheet.kt에 정의되어 있다고 가정합니다.
                // 만약 없다면 일반 Button을 사용해야 합니다.
                Button(
                    onClick = onShowQrScanner,
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(80.dp)
                ) {
                    Text(if (page == 0) "동행 시작 QR 스캔" else "동행 종료 QR 스캔")
                }
            }
        }
        1 -> {
            // 동행 중일 때 표시되는 UI (기존 코드 유지)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // ... (시작 시간, 이동 거리 표시 로직)
                Text("동행 중입니다. 멋진 파트너와 함께 하세요!")
            }
        }
        else -> Text("잘못된 페이지!")
    }
    Spacer(modifier = Modifier.height(32.dp))
}


/**
 * 기존 BottomSheet.kt의 SheetButtonBatch를 ViewModel 이벤트와 연결하기 위해 새로 정의
 */
@Composable
private fun SheetButtonBatchWithEvents(
    page: Int,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onFinalEndClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // BtnSOS, BtnShareLocation (BottomSheet.kt에서 import)
        BtnSOS(
            onClick = { /* SOS 로직 */ },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        BtnShareLocation(
            onClick = { /* 위치공유 로직 */ },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))

        // page 값에 따라 ViewModel의 적절한 함수를 호출
        when (page) {
            0 -> BtnStartDH(
                onClick = onStartClick, // ViewModel의 goToNextStep() 호출 (0 -> 1)
                modifier = Modifier.weight(1f)
            )
            1 -> BtnEndtDH(
                onClick = onEndClick,   // ViewModel의 goToNextStep() 호출 (1 -> 2)
                modifier = Modifier.weight(1f)
            )
            2 -> BtnEndtDH(
                onClick = onFinalEndClick, // ViewModel의 completeAccompany() 호출
                modifier = Modifier.weight(1f)
            )
        }
    }
}
//... (Preview 코드 생략)
