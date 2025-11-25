package com.kfpd_donghaeng_fe.ui.matching.ongoing

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.kfpd_donghaeng_fe.GlobalApplication
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.domain.entity.auth.LoginAccountUiState
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
// 필요한 import: androidx.compose.ui.draw.shadow, androidx.compose.ui.draw.clip



// 0 또는 3 일때 !
import androidx.compose.material3.CircularProgressIndicator
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScreenUiState

// ... (기타 필요한 import) ...

// 💡 ViewModel 대신 이미 변환된 Non-null 상태를 인자로 받습니다.
@Composable
fun QRSheet(
    uiState: QRScreenUiState, // 👈 QREntity? 대신 Non-null 상태 클래스 사용
    pageuiState: OngoingEntity,
    onScanRequest: (QRScandEntity, QRTypes, Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val cornerShape = RoundedCornerShape(20.dp)
    val page = pageuiState.OngoingPage

    // Non-null Entity에서 URL을 바로 추출합니다.

    if (page == 0 || page == 2) {

        Box(
            modifier = modifier
                .padding(horizontal = 40.dp)
                .shadow(elevation = 8.dp, shape = cornerShape)
                .clip(cornerShape)
                .background(Color.White)
                .wrapContentSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ... (Text 요소들 생략) ...
                Spacer(modifier = Modifier.height(20.dp))

                Box(modifier = Modifier.size(200.dp), contentAlignment = Alignment.Center) {

                    if (uiState.isLoading) { // 💡 Non-null isLoading 플래그 사용
                        // 💡 로딩 중일 때 스피너 표시
                        CircularProgressIndicator(modifier = Modifier.size(50.dp))
                    } else {
                        // 💡 데이터가 로드 완료되었을 때 Non-null URL 사용
                        AsyncImage(
                            model = uiState.qrEntity.qrImageUrl,
                            contentDescription = "QR Code Image",
                            modifier = Modifier.size(200.dp), // 크기 명확히 지정
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }
}



