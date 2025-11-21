package com.kfpd_donghaeng_fe.ui.matching.search

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
            // 검색 입력 필드 (디자인과 달리 TextField로 구현)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = viewModel::updateSearchQuery,
                    placeholder = { Text("장소, 버스, 지하철, 주소 검색", fontSize = 16.sp) },
                    modifier = Modifier.fillMaxSize(),
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = viewModel::clearSearchQuery) {
                                Icon(Icons.Default.Clear, contentDescription = "지우기")
                            }
                        } else {
                            // 💡 검색 아이콘 (이미지에는 없음)
                            Icon(Icons.Default.Search, contentDescription = "검색")
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AppColors.AccentColor,
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedContainerColor = AppColors.LightGray.copy(alpha = 0.5f),
                        unfocusedContainerColor = AppColors.LightGray.copy(alpha = 0.5f),
                    )
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
                HomeCompanyTag("회사", R.drawable.ic_home)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 검색 결과 또는 히스토리
            if (searchQuery.isBlank()) {
                // 히스토리 표시 (이미지 image_b268c1.png)
                if (searchHistories.isNotEmpty()) {
                    Text(
                        text = "최근 검색",
                        fontSize = 14.sp,
                        color = AppColors.SecondaryText,
                        modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp)
                    )
                    // TODO: 히스토리 리스트 구현 (현재는 PlaceItem 재사용)
                    LazyColumn {
                        items(searchHistories) { place ->
                            PlaceItem(
                                place = place,
                                onClick = {
                                    // 히스토리 항목 클릭 시, 해당 장소를 선택하고 검색창을 지웁니다.
                                    onPlaceSelected(place)
                                    viewModel.clearSearchQuery()
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
                    // 검색 결과 리스트 (이미지 image_b268c1.png의 하단 리스트)
                    LazyColumn {
                        items(searchResults) { place ->
                            PlaceItem(
                                place = place,
                                onClick = {
                                    viewModel.addToHistory(place)
                                    onPlaceSelected(place)
                                }
                            )
                        }
                    }
                }
            }
        }
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