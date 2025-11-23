package com.kfpd_donghaeng_fe.ui.matching.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel



import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.viewmodel.matching.PlaceSearchViewModel

/**
 * 재사용 가능한 장소 검색 화면
 * @param searchType "도착지" 또는 "경유지"
 * @param onPlaceSelected 장소 선택 시 콜백
 * @param onBackPressed 뒤로가기 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaceSearchScreen(
    searchType: String = "도착지", // "도착지" or "경유지"
    onPlaceSelected: (PlaceSearchResult) -> Unit,
    onBackPressed: () -> Unit,
    viewModel: PlaceSearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchHistories by viewModel.searchHistories.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$searchType 검색") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 검색창
            SearchTextField(
                query = searchQuery,
                onQueryChange = viewModel::updateSearchQuery,
                onClear = viewModel::clearSearchQuery,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            // 검색 결과 또는 히스토리
            if (searchQuery.isBlank()) {
                // 히스토리 표시
                if (searchHistories.isNotEmpty()) {
                    Text(
                        text = "최근 검색",
                        fontSize = 14.sp,
                        color = AppColors.SecondaryText,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    LazyColumn {
                        items(searchHistories) { place ->
                            PlaceItem(
                                place = place,
                                onClick = {
                                    onPlaceSelected(place)
                                }
                            )
                        }
                    }
                }
            } else {
                // 검색 결과
                if (isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "검색 결과가 없습니다",
                            color = AppColors.SecondaryText
                        )
                    }
                } else {
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
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier,
        placeholder = { Text("장소명 또는 주소 검색") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "검색")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Clear, contentDescription = "지우기")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.AccentColor,
            unfocusedBorderColor = Color(0xFFE0E0E0)
        )
    )
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