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
import com.kfpd_donghaeng_fe.R

import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import kotlinx.coroutines.CoroutineScope
import kotlin.String


@Composable
fun Background_Map() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "임시바탕...지도 넣어주세용",
            color = Color.White
        )
    }
}


// 버튼 색상 정의 <- theme 에 정리할 필요 o
val OffButtonColor = Color(0xFFE0E0E0)
val OnButtonColor = Color(0xFFF09040)
val isBtnStartDHEnabled = true // 일단 활성화 ( 아직 qr 인증 기능 x  + 다음 페이지 넘어가기 위해서~)
val isBtnEndDHEnabled =true // 목적지 도착 (종료) / qr 인증 후 종료 나눌 것인지 논의 필요
val isBtnSOSEnabled = false // 아직 구현 x
val isBtnShareLocationEnabled=false // 아직 구현 x


//버튼 초기 설정 (sos,shareplace,startDH)
@Composable
fun BtnSet(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    isEnabled: Boolean
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(50.dp),
        shape = RoundedCornerShape(8.dp),
        enabled = isEnabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isEnabled) OnButtonColor else OffButtonColor,
            contentColor = if (isEnabled) Color.White else Color.Black
        ),
    ) {
        Text(text, style = MaterialTheme.typography.titleMedium)
    }
}


//버튼 로직 설정
@Composable
fun BtnSOS_Onclick(){

}

@Composable
fun BtnShareLocation_Onclick(){

}

@Composable
fun BtnStartDH_Onclick(){

}

@Composable
fun BtnQR_Onclick(){

}


@Composable // QR 인증시 버튼 활성화 <- 일단 패스
fun onQRScanned() {
    //isButtonEnabled = true
}

@Composable
fun BtnQR(onClick: () -> Unit){
    val QRCamImg = painterResource(id = R.drawable.qr_cam_icon)
    Button(
        onClick =  onClick,
        modifier = Modifier.size(160.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        border = BorderStroke(width = 2.dp, color = Color.LightGray),
    )  {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = QRCamImg,
                contentDescription = "QR_Code_Scanner_Icon",
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)

            )
        }
    }
}

// SOS 버튼
@Composable
fun BtnSOS(onClick: () -> Unit, modifier: Modifier = Modifier) {
    BtnSet(
        text = "SOS",
        modifier = modifier,
        onClick = onClick,
        isEnabled = isBtnSOSEnabled
    )
}

//위치공유 버튼
@Composable
fun BtnShareLocation(onClick: () -> Unit, modifier: Modifier = Modifier) {
    BtnSet(
        text = "위치공유",
        modifier = modifier,
        onClick = onClick,
        isEnabled = isBtnShareLocationEnabled
    )
}

//동행 버튼
@Composable
fun BtnStartDH(onClick: () -> Unit, modifier: Modifier = Modifier) {
    BtnSet(
        text = "동행시작",
        modifier = modifier,
        onClick = onClick,
        isEnabled = isBtnStartDHEnabled
    )
}

@Composable
fun BtnEndtDH(onClick: () -> Unit, modifier: Modifier = Modifier) {
    BtnSet(
        text = "동행종료",
        modifier = modifier,
        onClick = onClick,
        isEnabled = isBtnEndDHEnabled
    )
}


// 버튼 묶음
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetButtonBatch(
    scope: CoroutineScope,
    sheetState: SheetState,
    onCloseRequest: () -> Unit,
    page:Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        if(page==0){
            BtnStartDH(
                onClick = {/*동행 시작 로직 */},
                modifier = Modifier.weight(1f)
            )
        }
        else if(page==1){
            BtnEndtDH(
                onClick = {/*동행 종료 로직1 */},
                modifier = Modifier.weight(1f)
            )
        }
        else if(page==2){
            BtnEndtDH(
                onClick = {/*동행 종료 로직2*/},
                modifier = Modifier.weight(1f)
            )
        }

    }
}


//시트 위 멘트 (두개)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetTop( page:Int, ){
    Spacer(modifier = Modifier.height(8.dp))
    var FirstMent:String=""
    var SecondMent:String=""
    if(page==0){
        FirstMent="상대방과 만나셨나요?"
        SecondMent="QR 코드를 스캔하여 동행을 시작하세요."
    }
    else if(page ==1){
        FirstMent="동행이 시작되었습니다."
        SecondMent="목적지 도착 후 동행 종료 QR 코드를 스캔하세요."
    }
    else if(page==2){
        FirstMent="목적지에 도착하셨나요?"
        SecondMent="마지막으로 QR코드를 스캔하여 동행을 종료하세요"
    }
    Text(
        text = "$FirstMent",
        style = MaterialTheme.typography.headlineSmall,
        color = Color.Black.copy(alpha = 0.9f),
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        text = "$SecondMent",
        style = MaterialTheme.typography.titleSmall,
        color = Color.Gray
    )
}



@Composable
fun SheetMiddle(page:Int){
    Spacer(modifier = Modifier.height(32.dp))

    // page 0 또는 2일 때: QR 버튼 표시
    if (page == 0 || page == 2) {
        // QR 버튼
        BtnQR(
            onClick = {/* QR 인증 로직 <- 카메라 띄우기 */}
        )
    }
    // page 1일 때: 동행 정보 표시
    else if (page == 1) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp), // 상하 여백 추가
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. 시작 시간 섹션
            Column(
                modifier = Modifier.weight(1f), // 전체 너비의 절반 차지
                horizontalAlignment = Alignment.CenterHorizontally // 수평 중앙 정렬
            ) {
                Text(
                    text = "시작 시간",
                    style = MaterialTheme.typography.titleMedium, // 비교적 작은 글꼴
                    color = Color.Gray.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "18:20", // 변수로 변환 필요 (주석 무시)
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), // 큰 글꼴, 볼드
                    color = Color.Black
                )
            } // Column 닫는 중괄호 (원래 없었음)

            // 중앙 수직 구분선
            Divider(
                color = Color.Black.copy(alpha = 0.5f), // 이미지의 중앙 수직선 색상
                modifier = Modifier
                    .height(80.dp) // 세로 길이는 텍스트 높이에 맞게 조정
                    .width(1.dp)
            )

            // 2. 이동 거리 섹션
            Column(
                modifier = Modifier.weight(1f), // 전체 너비의 나머지 절반 차지
                horizontalAlignment = Alignment.CenterHorizontally // 수평 중앙 정렬
            ) {
                Text(
                    text = "이동 거리",
                    style = MaterialTheme.typography.titleMedium, // 비교적 작은 글꼴
                    color = Color.Gray.copy(alpha = 0.8f)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "0.0Km", // 변수로 변환 필요 (주석 무시)
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold), // 큰 글꼴, 볼드
                    color = Color.Black
                )
            } // Column 닫는 중괄호 (원래 없었음)
        } // Row 닫는 중괄호 (원래 없었음)
    } // else if (page == 1) 닫는 중괄호 (원래 없었음)
    // else if 문이 끝나고 else 문이 시작해야 합니다.
    else {
        Text("잘못된 페이지!")
    }

} // SheetMiddle 함수 닫는 중괄호


//시트 전체 레이아웃
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SheetInside(
    scope: CoroutineScope,
    sheetState: SheetState,
    onCloseRequest: () -> Unit,
    page:Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        SheetTop(page)
        SheetMiddle(page)
        Spacer(modifier = Modifier.height(40.dp),)
        // 버튼 묶음
        SheetButtonBatch(scope, sheetState, onCloseRequest,page)
    }
}


//시트 틀
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(page:Int) {
    val scope = rememberCoroutineScope()

    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded,
        skipHiddenState = true // 완전히 닫히지 않게
    )

    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = bottomSheetState
    )

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 80.dp,
        sheetShape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp), //모서리둥글게
        sheetContainerColor = Color.White, //배경지정
        sheetDragHandle = {//핸들 지정
            BottomSheetDefaults.DragHandle(
                color = Color.Gray,
                width = 80.dp
            )
        },
        sheetContent = {
            SheetInside(
                scope = scope,
                sheetState = bottomSheetState,
                onCloseRequest = { /* 닫히지 않게 */ },
                page=page
            )
        },
        content = {
            Background_Map()
        }
    )

}


@Preview(showBackground = true)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DefaultAppPreview() {
    KFPD_DongHaeng_FETheme(dynamicColor = false) {
        BottomSheet(0)
    }
}

