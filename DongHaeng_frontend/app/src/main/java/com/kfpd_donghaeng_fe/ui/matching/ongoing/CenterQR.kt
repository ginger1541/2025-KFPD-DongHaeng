package com.kfpd_donghaeng_fe.ui.matching.ongoing

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.kfpd_donghaeng_fe.GlobalApplication
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
// 필요한 import: androidx.compose.ui.draw.shadow, androidx.compose.ui.draw.clip



@Composable
fun QRSheet(page: Int, modifier: Modifier = Modifier) {
    // 실제 QR 코드 이미지 리소스 ID를 사용해야 합니다.
    val qrCodeImage = painterResource(id = R.drawable.ic_qrcode)
    val cornerShape = RoundedCornerShape(20.dp)

    // Box를 사용하여 배경과 그림자, 둥근 모서리를 처리합니다.
    Box(
        modifier = modifier
            .padding(horizontal = 40.dp)
            .shadow(
                elevation = 8.dp,
                shape = cornerShape
            )
            .clip(cornerShape)
            // 💡 수정된 부분: 흰색에 알파 값(0.9f)을 적용하여 반투명하게 만듭니다.
            .background(Color.White)
            .wrapContentSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 상단 안내 멘트
            Text(
                text = "QR 인증으로 안전한 동행이 보장됩니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 메인 안내 멘트
            Text(
                text = "동행 전 후 QR 인증을 진행해주세요.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
            )
            Spacer(modifier = Modifier.height(20.dp))

            // QR 코드 이미지 (크게)
            Image(
                painter = qrCodeImage,
                contentDescription = "QR Code for companion",
                modifier = Modifier
                    .size(200.dp)
            )
        }
    }
}
// Preview는 생략합니다.

@Preview(showBackground = true)
@Composable
fun QRSheetPreview() {
    KFPD_DongHaeng_FETheme {
        QRSheet(page = 0) // page=0 상태로 미리보기
    }
}