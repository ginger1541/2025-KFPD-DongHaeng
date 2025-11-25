package com.kfpd_donghaeng_fe.ui.matching.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.LocationType
import com.kfpd_donghaeng_fe.domain.entity.toRouteLocation
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import kotlin.random.Random
import kotlin.math.*


// TODO: 현재 위도, 경도 변환
private const val EARTH_RADIUS_KM = 6371.0
private const val MOCK_USER_LAT = 37.5665 // 서울 시청 근처 위도
private const val MOCK_USER_LON = 126.9780 // 서울 시청 근처 경도

/**
 * 하버사인 공식을 사용하여 두 지점 간의 거리를 KM 단위로 계산합니다.
 */
private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return EARTH_RADIUS_KM * c // Distance in KM
}

/**
 * PlaceSearchResult에서 Mock 사용자 위치까지의 거리를 계산합니다.
 */
private fun PlaceSearchResult.distanceToUserKm(): Double {
    val placeLat = this.y.toDoubleOrNull() ?: return 0.0
    val placeLon = this.x.toDoubleOrNull() ?: return 0.0

    return haversineDistance(MOCK_USER_LAT, MOCK_USER_LON, placeLat, placeLon)
}

fun RouteLocation.toPlaceSearchResult(): PlaceSearchResult {
    return PlaceSearchResult(
        placeName = this.placeName,
        addressName = this.address,
        roadAddressName = this.address,
        categoryName = "선택된 장소",
        phone = "",
        x = this.longitude?.toString() ?: "",
        y = this.latitude?.toString() ?: ""
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceDetailSheetContent(
    place: PlaceSearchResult,
    onClose: () -> Unit,
    onSelect: (isStart: Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val randomReviewCount = remember { Random.nextInt(5, 50) }
    val distanceLabel = "264km" // Mock data for now

    Column(
        modifier = modifier.fillMaxWidth().verticalScroll(rememberScrollState())
    ) {
        // 2. Header (Place Name, Close Button)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = place.placeName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryDarkText
            )
            IconButton(onClick = onClose) {
                Icon(Icons.Default.Close, contentDescription = "닫기")
            }
        }
        Divider(color = Color(0xFFE0E0E0), modifier = Modifier)
        Spacer(modifier = Modifier.height(16.dp))

        // 3. Details and Buttons
        Column(modifier = Modifier) {
            // 지하철 출구 번호 / 리뷰 수 / 거리
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "지하철 출구번호 | 리뷰 ${randomReviewCount}", fontSize = 14.sp, color = AppColors.SecondaryText)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = distanceLabel, fontSize = 14.sp, color = AppColors.SecondaryText)
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 출발/도착 버튼
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSelect(true) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("출발", color = Color.White) }
                Button(
                    onClick = { onSelect(false) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("도착", color = Color.White) }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // 4. 거리뷰 / 이미지 (Mockup)
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).background(AppColors.MapPlaceholder, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) { Text("이미지를 제공하지 않습니다", color = AppColors.SecondaryText) }
        }
    }
}