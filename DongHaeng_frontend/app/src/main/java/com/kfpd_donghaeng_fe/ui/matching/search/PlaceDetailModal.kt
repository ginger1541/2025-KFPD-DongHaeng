package com.kfpd_donghaeng_fe.ui.matching.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.LocationType
import com.kfpd_donghaeng_fe.domain.entity.toRouteLocation
import com.kfpd_donghaeng_fe.ui.common.KakaoMapView
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
fun PlaceDetailModal(
    place: PlaceSearchResult,
    onBack: () -> Unit,
    onSelectAsStart: (RouteLocation) -> Unit,
    onSelectAsEnd: (RouteLocation) -> Unit,
) {
    // 💡 Mock 리뷰 생성
    val randomReviewCount = remember { Random.nextInt(5, 50) }
    val mockReviews = remember {
        listOf(
            "출구 찾기가 편해요.", "직원들이 친절했어요.", "주변에 식당이 많아요.",
            "접근성이 좋아요.", "새로 생겨서 깨끗해요.", "동행하기 좋은 장소예요."
        ).shuffled().take(Random.nextInt(1, 4))
    }

    val distanceKm by remember(place) {
        mutableStateOf(place.distanceToUserKm())
    }

    val distanceLabel = remember(distanceKm) {
        if (distanceKm < 1.0) {
            // 1km 미만일 경우 미터(m)로 표시
            "${(distanceKm * 1000).coerceAtLeast(0.0).toInt()}m"
        } else {
            // 1km 이상일 경우 소수점 첫째 자리까지 km로 표시
            "${"%.1f".format(distanceKm)}km"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = place.placeName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.PrimaryDarkText
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                actions = {
                    IconButton(onClick = onBack) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_close_gray),
                            contentDescription = "닫기",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 1. 지도 영역 (Mockup)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                // TODO: KakaoMapView에 마커 표시 로직 추가 필요
                KakaoMapView(
                    modifier = Modifier.fillMaxSize(),
                    locationX = place.x.toDoubleOrNull() ?: 126.9780,
                    locationY = place.y.toDoubleOrNull() ?: 37.5665,
                    enabled = true
                )
            }

            // 2. 상세 정보 및 버튼
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = place.placeName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryDarkText
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 지하철 출구 번호 / 리뷰 수
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "지하철 출구번호 | 리뷰 ${randomReviewCount}",
                        fontSize = 14.sp,
                        color = AppColors.SecondaryText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    // 💡 [수정] 계산된 거리 표시
                    Text(
                        text = distanceLabel,
                        fontSize = 14.sp,
                        color = AppColors.SecondaryText
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = place.addressName.split(" ").lastOrNull() ?: "",
                        fontSize = 14.sp,
                        color = AppColors.SecondaryText
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 출발/도착 버튼
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            onSelectAsStart(place.toRouteLocation(LocationType.START))
                            onBack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("출발", color = Color.White)
                    }
                    Button(
                        onClick = {
                            onSelectAsEnd(place.toRouteLocation(LocationType.END))
                            onBack()
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("도착", color = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 3. 거리뷰 / 이미지 (Mockup)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(AppColors.MapPlaceholder, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    // TODO: 구글 맵 API 연동 시 이 부분에 이미지 로직 추가
                    Text("거리뷰 이미지 Mockup", color = AppColors.SecondaryText)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 4. 리뷰 섹션 (Mockup)
                Text(
                    text = "동행 후기",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryDarkText
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    mockReviews.forEach { review ->
                        Text(
                            text = "• $review",
                            fontSize = 14.sp,
                            color = AppColors.PrimaryDarkText
                        )
                    }
                }
            }
        }
    }
}