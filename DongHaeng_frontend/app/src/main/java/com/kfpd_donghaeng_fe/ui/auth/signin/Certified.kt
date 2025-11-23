package com.kfpd_donghaeng_fe.ui.auth.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource // 실제 이미지 사용 시 필요
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.domain.entity.auth.MakeAccountUiState


// -----------------------------------------------------------------------------
// 1. 배경 컨테이너 (BackgroundContainerScreen)
// -----------------------------------------------------------------------------
@Composable
fun BackgroundContainerScreen(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6E6E6E)) // 요청하신 회색 배경
    ) {
        content()
    }
}

// -----------------------------------------------------------------------------
// 2. 바텀 시트 컨텐츠 (CertificateVerificationSheetUI)
// -----------------------------------------------------------------------------
@Composable
fun CertificateVerificationSheetUI(
    uiState: MakeAccountUiState,
    onNextClick: () -> Unit,
    previousPage: () -> Unit,
    modifier: Modifier = Modifier) {
    // 🚨 R.drawable 오류 해결: Preview 환경에서는 안전한 Vector Painter를 사용합니다.
    // 실제 앱에서는 이곳에 "painterResource(id = R.drawable.my_id_card_image)"를 사용해야 합니다.
    val dummyPainter = painterResource(id = R.drawable.ic_card_ex)

    val dummyName = "홍길동"
    val dummyRegNumber = "123456-1234567"
    val dummyIssueDate = "2011.06.14"
    MaterialTheme {
        BackgroundContainerScreen(
            content = {
                // 바텀 시트를 화면 하단에 정렬하여 시뮬레이션
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter // 하단에 정렬
                ) {
                    Card(
                        modifier = modifier.fillMaxWidth(),
                        // 💡 상단 둥글기 50.dp 반영
                        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 24.dp), // 상하 패딩 조정
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 상단 공백 조정
                            Spacer(Modifier.height(8.dp))

                            // 1. 신분증 이미지 섹션
                            IdCardImageSection(imagePainter = dummyPainter)

                            Spacer(modifier = Modifier.height(24.dp))

                            // 2. 정보 확인 안내 텍스트
                            Text(
                                text = "정보를 확인해주세요",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF7941E), // 💡 색상 조정 (스크린샷 참조)
                                modifier = Modifier.align(Alignment.Start)
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3. 추출된 정보 리스트
                            ExtractedInfoRow(label = "이름", value = dummyName)
                            ExtractedInfoRow(label = "장애인 등록번호", value = dummyRegNumber)
                            ExtractedInfoRow(label = "발급일자", value = dummyIssueDate)

                            Spacer(modifier = Modifier.height(32.dp))

                            // 4. 버튼 섹션
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 다시 촬영하기 버튼
                                Button(
                                    onClick = previousPage,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF0F0F0),
                                        contentColor = Color(0xFF757575)
                                    ),
                                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Text("다시 촬영하기", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                }

                                // 확인 버튼 (주황색)
                                Button(
                                    onClick = onNextClick,
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF7941E)
                                    ),
                                    shape = RoundedCornerShape(15.dp)
                                ) {
                                    Text("확인", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        )
    }


}

// -----------------------------------------------------------------------------
// 3. 서브 컴포저블: 신분증 이미지 영역 (IdCardImageSection)
// -----------------------------------------------------------------------------
@Composable
fun IdCardImageSection(imagePainter: Painter) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = imagePainter,
            contentDescription = "촬영된 신분증 이미지",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

// -----------------------------------------------------------------------------
// 4. 서브 컴포저블: 추출 정보 한 줄 (ExtractedInfoRow)
// -----------------------------------------------------------------------------
@Composable
fun ExtractedInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            color = Color(0xFF757575),
            modifier = Modifier.width(110.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = value,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
    }
}

/*

@Composable
fun VerificationScreenSheetUIScreen() {
    MaterialTheme {
        BackgroundContainerScreen(
            content = {
                // 바텀 시트를 화면 하단에 정렬하여 시뮬레이션
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter // 하단에 정렬
                ) {
                    CertificateVerificationSheetUI()
                }
            }
        )
    }
}*/