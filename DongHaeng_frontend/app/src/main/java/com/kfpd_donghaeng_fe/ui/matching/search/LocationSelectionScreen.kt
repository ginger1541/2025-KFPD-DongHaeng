package com.kfpd_donghaeng_fe.ui.matching.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
import com.kfpd_donghaeng_fe.ui.theme.MainOrange

@Composable
fun LocationSelectionScreen(
    placeName: String,
    address: String,
    lat: Double,
    lng: Double,
    onConfirm: () -> Unit, // 설정하기 버튼 클릭 시 콜백
    onBackClick: () -> Unit
) {
    // 2. 지도에 표시할 마커 객체 생성
    val selectedLocationMarker = remember(lat, lng) {
        listOf(
            RouteLocation(
                id = "target",
                type = com.kfpd_donghaeng_fe.domain.entity.LocationType.TARGET, // 임의 타입 사용 (마커 아이콘 커스텀을 위해)
                placeName = placeName,
                address = address,
                latitude = lat,
                longitude = lng,
            )
        )
    }

    // 3. ZStack 레이아웃 구현
    Box(modifier = Modifier.fillMaxSize()) {
        // [배경] 카카오맵
        KakaoMapView(
            modifier = Modifier.fillMaxSize(),
            locationX = lng,
            locationY = lat,
            markers = selectedLocationMarker,
            enabled = true
        )

        // [전경-하단] 정보 카드
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(elevation = 10.dp, shape = RoundedCornerShape(16.dp)), // 그림자 효과
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 검색어 (장소 이름)
                Text(
                    text = placeName,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(8.dp))

                // 상세 위치 (주소)
                Text(
                    text = address,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(24.dp))

                // 설정하기 버튼
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MainOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "이 위치로 설정하기",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}