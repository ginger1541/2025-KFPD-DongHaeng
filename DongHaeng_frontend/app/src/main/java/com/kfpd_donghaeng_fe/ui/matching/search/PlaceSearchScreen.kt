package com.kfpd_donghaeng_fe.ui.matching.search

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
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



import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.viewmodel.matching.PlaceSearchViewModel
import com.kfpd_donghaeng_fe.R
/**
 * 재사용 가능한 장소 검색 화면
 * @param searchType "도착지" 또는 "경유지"
 * @param onPlaceSelected 장소 선택 시 콜백
 * @param onBackPressed 뒤로가기 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSearchScreen(
    searchType: String, // "출발지" or "도착지"
    onPlaceSelected: (PlaceSearchResult) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: PlaceSearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchHistories by viewModel.searchHistories.collectAsState()
    var selectedPlaceForDetail by remember { mutableStateOf<PlaceSearchResult?>(null) }
    // 💡 이미지와 동일하게 Full Screen Search UI 구성
    Column(modifier = Modifier.fillMaxSize().background(Color.White)) {

        // 1. 상단 검색바/네비게이션 영역
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp) // TopAppBar 높이
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 뒤로가기 버튼
            IconButton(onClick = onBackPressed) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기",
                    tint = AppColors.PrimaryDarkText
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                BasicTextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    modifier = Modifier
                        .fillMaxSize()
                        // 1. 배경 및 테두리 설정 (OutlinedTextField 스타일 흉내)
                        .background(
                            color = AppColors.LightGray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(20.dp)
                        )
                        .border(
                            width = 1.dp,
                            // 포커스/입력 유무에 따른 색상 처리 (필요시 isFocused 상태 추가 관리 가능)
                            color = if (searchQuery.isNotEmpty()) AppColors.AccentColor else Color(0xFFE0E0E0),
                            shape = RoundedCornerShape(20.dp)
                        ),
                    textStyle = androidx.compose.ui.text.TextStyle(
                        fontSize = 16.sp,
                        color = AppColors.PrimaryDarkText
                    ),
                    singleLine = true,
                    // 2. 내부 장식 (Placeholder, 아이콘, 텍스트 배치)
                    decorationBox = { innerTextField ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp), // 좌우 여백
                            verticalAlignment = Alignment.CenterVertically // 수직 중앙 정렬 (핵심!)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                // Placeholder
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = "장소, 버스, 지하철, 주소 검색",
                                        fontSize = 16.sp,
                                        color = Color.Gray
                                    )
                                }
                                // 실제 입력 필드
                                innerTextField()
                            }

                            // Trailing Icon (검색/삭제 아이콘)
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = viewModel::clearSearchQuery,
                                    modifier = Modifier.size(20.dp) // 아이콘 버튼 크기 조절
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "지우기",
                                        tint = AppColors.SecondaryText
                                    )
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "검색",
                                    tint = AppColors.SecondaryText,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                )
            }

            // 우측 화살표 버튼 (이미지처럼, 지금은 닫기 기능으로 대체)
            IconButton(onClick = onBackPressed) {
                Icon(
                    painterResource(id = R.drawable.ic_send), // 임시로 ic_send 사용
                    contentDescription = "닫기",
                    tint = AppColors.AccentColor
                )
            }
        }

        // 2. 홈/회사 태그 및 최근 검색
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            // 홈/회사 버튼 (PathInputBox에서 재사용)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HomeCompanyTag("집", R.drawable.ic_home)
                HomeCompanyTag("회사", R.drawable.ic_company)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 검색 결과 또는 히스토리
            if (searchQuery.isBlank()) {
                // 히스토리 표시
                if (searchHistories.isNotEmpty()) {
                    Text(
                        text = "최근 검색",
                        fontSize = 14.sp,
                        color = AppColors.SecondaryText,
                        modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp)
                    )
                    LazyColumn {
                        items(searchHistories) { place ->
                            PlaceItem(
                                place = place,
                                onClick = {
                                    // 💡 [수정] 히스토리 항목 클릭 시 상세 모달을 띄웁니다.
                                    selectedPlaceForDetail = place
                                }
                            )
                        }
                    }
                }
            } else {
                // 검색 결과
                if (isLoading) {
                    // ... 로딩 인디케이터
                } else if (searchResults.isEmpty()) {
                    // ... 결과 없음
                } else {
                    // 검색 결과 리스트
                    LazyColumn {
                        items(searchResults) { place ->
                            PlaceItem(
                                place = place,
                                onClick = {
                                    selectedPlaceForDetail = place
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // ==========================================================
    // 💡 [추가] PlaceDetailModal 렌더링 로직
    // ==========================================================
    selectedPlaceForDetail?.let { place ->
        PlaceDetailModal(
            place = place,
            onBack = { selectedPlaceForDetail = null }, // 모달 닫기
            onSelectAsStart = { routeLocation ->
                // 출발지로 선택: ViewModel에 저장 후, 검색 화면 전체 닫기
                viewModel.addToHistory(place)
                onPlaceSelected(routeLocation.toPlaceSearchResult()) // MainRouteScreen으로 선택된 장소 전달
                // 💡 [핵심 수정] 상세 모달을 닫는 대신, MainRouteScreen이 다음 플로우를 진행할 수 있도록 콜백을 호출합니다.
                // PlaceSearchScreen은 MainRouteScreen에 의해 제어되므로, onBack()을 호출하여 PlaceSearchScreen을 닫고 MainRouteScreen으로 돌아갑니다.
                onBackPressed()
            },
            onSelectAsEnd = { routeLocation ->
                // 도착지로 선택: ViewModel에 저장 후, 검색 화면 전체 닫기
                viewModel.addToHistory(place)
                onPlaceSelected(routeLocation.toPlaceSearchResult())
                onBackPressed()
            }
        )
    }
}

@Composable
fun PlaceItem(
    place: PlaceSearchResult,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            text = place.placeName,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.PrimaryDarkText
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (place.roadAddressName.isNotEmpty())
                place.roadAddressName
            else
                place.addressName,
            fontSize = 14.sp,
            color = AppColors.SecondaryText
        )
        if (place.categoryName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = place.categoryName.split(">").lastOrNull()?.trim() ?: "",
                fontSize = 12.sp,
                color = AppColors.SecondaryText.copy(alpha = 0.7f)
            )
        }
    }
    Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
}

@Composable
private fun HomeCompanyTag(label: String, iconResId: Int) {
    Row(
        modifier = Modifier
            .background(AppColors.LightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = label,
            tint = AppColors.SecondaryText,
            modifier = Modifier.size(16.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = label,
            color = AppColors.PrimaryDarkText,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}