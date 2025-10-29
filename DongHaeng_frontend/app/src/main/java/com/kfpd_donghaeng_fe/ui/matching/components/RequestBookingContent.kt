package com.kfpd_donghaeng_fe.ui.matching.componentes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.ui.matching.search.PlaceSearchScreen
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.ui.theme.AppColors.AccentColor
import com.kfpd_donghaeng_fe.ui.theme.AppColors.PrimaryDarkText
import com.kfpd_donghaeng_fe.ui.theme.AppColors.SecondaryText
import com.kfpd_donghaeng_fe.viewmodel.matching.BookingViewModel

// UI State Models
data class LocationInput(
    val id: String,
    val type: LocationType,
    val address: String,
    val placeInfo: PlaceSearchResult? = null,
    val isEditable: Boolean = true
)

enum class LocationType {
    START,
    WAYPOINT,
    END
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RequestBookingContent(
    onTimePickerRequested: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    viewModel: BookingViewModel = hiltViewModel()
) {
    // ViewModel 상태 관찰
    val routeInputs by viewModel.routeInputs.collectAsState()
    val hasWaypoint = routeInputs.any { it.type == LocationType.WAYPOINT }
    val isEndLocationSet = routeInputs.find { it.type == LocationType.END }
        ?.placeInfo != null

    // 검색 화면 네비게이션 상태
    var showSearch by remember { mutableStateOf<String?>(null) }

    // 목업데이터
    LaunchedEffect(Unit) {
        viewModel.setMockEndLocationForTest()
    }

    if (showSearch != null) {
        // Full Screen 검색 화면
        PlaceSearchScreen(
            searchType = showSearch!!,
            onPlaceSelected = { placeInfo ->
                when (showSearch) {
                    "도착지" -> viewModel.updateEndLocation(placeInfo)
                    "경유지" -> viewModel.updateWaypointLocation(placeInfo)
                }
                showSearch = null
            },
            onBackPressed = {
                showSearch = null
            }
        )
    } else {
        // 예약 화면
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "예약하기",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryDarkText
                )
                Text(
                    text = "재설정",
                    fontSize = 12.sp,
                    color = SecondaryText,
                    modifier = Modifier.clickable {
                        viewModel.resetRoute()
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))

            // 경로 입력 박스
            PathInputBox(
                routeInputs = routeInputs,
                hasWaypoint = hasWaypoint,
                onAddWaypoint = {
                    if (!hasWaypoint) {
                        viewModel.addWaypoint()
                    }
                },
                onRemoveWaypoint = viewModel::removeWaypoint,
                onLocationClick = { location ->
                    when (location.type) {
                        LocationType.END -> showSearch = "도착지"
                        LocationType.WAYPOINT -> showSearch = "경유지"
                        else -> {}
                    }
                },
                onCancel = onCancel  // 이 부분 추가!
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 요청사항 버튼
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = PrimaryDarkText
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "요청사항",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = PrimaryDarkText,
                    modifier = Modifier.clickable {
                        // 요청사항 모달 띄우기 - TODO
                        println("요청사항 clicked")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 동행 예약하기 버튼
            Button(
                onClick = {
                    onTimePickerRequested?.invoke() ?: run {
                        println("동행 예약하기")
                    }
                },
                enabled = isEndLocationSet, // 도착지 설정되어야 활성화
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AccentColor,
                    disabledContainerColor = Color(0xFFE0E0E0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(
                    "동행 예약하기",
                    color = if (isEndLocationSet) Color.White else Color(0xFF9E9E9E),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PathInputBox(
    routeInputs: List<LocationInput>,
    hasWaypoint: Boolean,
    onAddWaypoint: () -> Unit,
    onRemoveWaypoint: () -> Unit,
    onLocationClick: (LocationInput) -> Unit,
    onCancel: (() -> Unit)? = null
) {
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 메인 박스 (스트로크 포함)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                routeInputs.forEachIndexed { index, input ->
                    LocationInputRow(
                        input = input,
                        onLocationClick = { onLocationClick(input) }
                    )

                    // 출발지 다음에 디바이더 + 버튼
                    if (input.type == LocationType.START) {
                        Spacer(modifier = Modifier.height(8.dp))
                        DividerWithButton(
                            hasWaypoint = hasWaypoint,
                            canAddWaypoint = !hasWaypoint, // 경유지 1개 제한
                            onAddWaypoint = onAddWaypoint,
                            onRemoveWaypoint = onRemoveWaypoint
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 경유지 다음에 디바이더 (버튼 없음)
                    if (input.type == LocationType.WAYPOINT && index < routeInputs.size - 1) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFFE0E0E0))
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // 우측 상단 X 버튼 (닫기)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-8).dp, y = (-8).dp)
                .size(32.dp)
                .clickable {
                    onCancel?.invoke()
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_close_gray),
                contentDescription = "닫기",
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun DividerWithButton(
    hasWaypoint: Boolean,
    canAddWaypoint: Boolean,
    onAddWaypoint: () -> Unit,
    onRemoveWaypoint: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )

        Spacer(modifier = Modifier.width(8.dp))

        Image(
            painter = painterResource(
                id = if (hasWaypoint)
                    R.drawable.ic_minus_circle
                else
                    R.drawable.ic_plus_circle
            ),
            contentDescription = if (hasWaypoint) "경유지 제거" else "경유지 추가",
            modifier = Modifier
                .size(32.dp)
                .clickable {
                    if (hasWaypoint) {
                        onRemoveWaypoint()
                    } else if (canAddWaypoint) {
                        onAddWaypoint()
                    }
                }
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(Color(0xFFE0E0E0))
        )
    }
}

@Composable
fun LocationInputRow(
    input: LocationInput,
    onLocationClick: () -> Unit
) {
    val color = when (input.type) {
        LocationType.START -> Color(0xFFFF9800)
        LocationType.WAYPOINT -> Color(0xFF9E9E9E)
        LocationType.END -> Color(0xFFFF9800)
    }

    val displayText = input.placeInfo?.placeName ?: input.address

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (input.type != LocationType.START) {
                    Modifier.clickable(onClick = onLocationClick)
                } else Modifier
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(color, shape = CircleShape)
        )

        Spacer(Modifier.width(12.dp))

        Text(
            text = displayText,
            fontSize = 16.sp,
            color = if (input.placeInfo == null && input.isEditable)
                SecondaryText
            else
                PrimaryDarkText,
            modifier = Modifier.weight(1f)
        )
    }
}