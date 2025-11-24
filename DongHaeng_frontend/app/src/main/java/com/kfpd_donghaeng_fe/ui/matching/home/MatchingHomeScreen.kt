package com.kfpd_donghaeng_fe.ui.matching.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.viewmodel.matching.home.MatchingHomeViewModel
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.matching.home.companion.CompanionHomeContent
import com.kfpd_donghaeng_fe.ui.matching.home.requester.RequesterHomeContent
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import com.kfpd_donghaeng_fe.domain.entity.auth.UserType
import com.kfpd_donghaeng_fe.ui.common.CommonDialog
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import androidx.compose.runtime.getValue
import com.kfpd_donghaeng_fe.data.Request

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MatchingHomeRoute(
    userType: UserType,
    viewModel: MatchingHomeViewModel = hiltViewModel(),
    onNavigateToSearch: (UserType) -> Unit,
    onNavigateToChangeLocation: () -> Unit,
    onNavigateToMyRequestDetail: (Request) -> Unit,
    onNavigateToRequestDetail: (Long) -> Unit,
) {
    LaunchedEffect(userType) {
        viewModel.setUserType(userType)
    }
    val userType by viewModel.userType.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    when (uiState) {

        // 1. Loading 상태 처리: 로딩 다이얼로그나 인디케이터 표시
        MatchingHomeUiState.Loading -> {
            // 전체 화면을 덮는 로딩 인디케이터나 다이얼로그 사용
            LoadingOverlay() // 별도 구현이 필요
        }

        // 2. Error 상태 처리: Error Dialog 표시
        is MatchingHomeUiState.Error -> {
            // 1. uiState를 Error 타입 변수(예: errorState)에 할당
            val errorState = uiState as MatchingHomeUiState.Error

            CommonDialog(
                title = "오류 발생",
                // 2. errorState 변수를 사용하여 message 속성에 접근
                message = errorState.message ?: "데이터를 불러오는 중 오류가 발생했습니다.",
                onDismiss = { viewModel.resetErrorState() }
            )
        }

        // 3. Needy 상태 처리 (기존 코드)
        is MatchingHomeUiState.NeedyState -> {
            val needyState = uiState as MatchingHomeUiState.NeedyState // as 캐스팅 추가
            RequesterHomeContent(
                recentTrips = needyState.recentTrips,
                onSearchClick = { onNavigateToSearch(userType) },
                onHistoryClick = { requestId ->
                    // ID로 리스트에서 객체 찾아서 전달
                    val request = needyState.recentTrips.find { it.id == requestId }?.toRequest()
                    if (request != null) {
                        onNavigateToMyRequestDetail(request)
                    }
                }
            )
        }

        is MatchingHomeUiState.HelperState -> {
            val helperState = uiState as MatchingHomeUiState.HelperState
            CompanionHomeContent(
                nearbyRequests = helperState.nearbyRequests,
                onSearchClick = { onNavigateToSearch(userType) },
                onChangeLocationClick = onNavigateToChangeLocation,
                onRequestClick = { requestId ->
                    // 👇 여기를 수정해서 상세 화면으로 이동시킵니다.
                    // 기존: onNavigateToRequestDetail(requestId) (이 함수가 mainNavController를 호출해야 함)
                    // MainScreen.kt에서 navigate 로직을 확인하세요.
                    onNavigateToRequestDetail(requestId)
                }
            )
        }
    }
}


@Composable
private fun MatchingHomeScreen(
    userRole: UserType,
    requesterHistory: List<RequestUiModel>,
    nearbyRequests: List<RequestUiModel>,
    onSearchClick: () -> Unit,
    onChangeLocationClick: () -> Unit,
    onRequestClick: (Long) -> Unit,
) {
    when (userRole) {
        UserType.NEEDY -> RequesterHomeContent(
            recentTrips = requesterHistory,
            onSearchClick = onSearchClick,
            onHistoryClick = onRequestClick
        )

        UserType.HELPER -> CompanionHomeContent(
            nearbyRequests = nearbyRequests,
            onSearchClick = onSearchClick,
            onChangeLocationClick = onChangeLocationClick,
            onRequestClick = onRequestClick
        )
    }
}

@Composable
fun MatchingSearchBar(
    placeholder: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.weight(1f)
            )
            Image(
                painter = painterResource(id = R.drawable.ic_search),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun LoadingOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f)), // 배경 반투명
        contentAlignment = Alignment.Center
    ) {
        // Compose Material3의 로딩 인디케이터
        CircularProgressIndicator(
            color = AppColors.AccentColor,
            strokeWidth = 4.dp
        )
    }
}
