package com.kfpd_donghaeng_fe.ui.auth.signin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star // 더미 아이콘
import androidx.compose.material3.*

import androidx.compose.ui.draw.clip

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@Composable
fun BackgroundContainerScreen(content: @Composable () -> Unit) {
    // 뒷배경: 카메라 미리보기 또는 어두운 화면이 들어갈 곳
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF6E6E6E)) // 요청하신 회색 배경
    ) {
        // 배경에 표시할 상단 바 (뒤로가기 버튼)을 여기에 배치할 수 있습니다.
        // 현재는 content를 호출하여 바텀 시트(또는 다른 UI)를 띄웁니다.
        content()
    }
}



// -----------------------------------------------------------------------------
// UI 구조만 있는 바텀 시트 컨텐츠
// -----------------------------------------------------------------------------
@Composable
fun CertificateVerificationSheetUI(modifier: Modifier = Modifier) {

    // 💡 더미 데이터 (생짜 박기)
    val dummyPainter = rememberVectorPainter(Icons.Default.Star)
    val dummyName = "홍길동"
    val dummyRegNumber = "123456-1234567"
    val dummyIssueDate = "2011.06.14"

    // 중앙 카드 형태의 정보 확인 박스 (바텀 시트의 컨텐츠)
    Card(
        modifier = modifier.fillMaxWidth(),
        // 상단만 둥글게, 하단은 0
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))

            // 1. 신분증 이미지 섹션 (더미 Painter 전달)
            IdCardImageSection(imagePainter = dummyPainter)

            Spacer(modifier = Modifier.height(24.dp))

            // 2. 정보 확인 안내 텍스트
            Text(
                text = "정보를 확인해주세요",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black.copy(alpha = 0.8f),
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3. 추출된 정보 리스트 (더미 데이터 사용)
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
                    onClick = { /* 로직 없음 */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF0F0F0),
                        contentColor = Color(0xFF757575)
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("다시 촬영하기", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }

                // 확인 버튼 (주황색)
                Button(
                    onClick = { /* 로직 없음 */ },
                    modifier = Modifier.weight(1f).height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF7941E)
                    )
                ) {
                    Text("확인", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 서브 컴포저블: 신분증 이미지 영역 (Image 컴포넌트)
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
// 서브 컴포저블: 추출 정보 한 줄
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

@Preview(showBackground = true)
@Composable
fun VerificationScreenSheetUIPreview() {
    MaterialTheme {
        BackgroundContainerScreen(
            content = {
                // BackgroundContainerScreen 위에 바텀 시트 컨텐츠를
                // 화면 하단에 정렬하여 시뮬레이션합니다.
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.BottomCenter // 하단에 정렬
                ) {
                    CertificateVerificationSheetUI()
                }
            }
        )
    }
}