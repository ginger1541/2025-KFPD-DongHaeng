package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.ui.matching.ongoing.Background_Map
import com.kfpd_donghaeng_fe.ui.matching.ongoing.Batch
import com.kfpd_donghaeng_fe.ui.matching.ongoing.BtnEndtDH
import com.kfpd_donghaeng_fe.ui.matching.ongoing.BtnSOS
import com.kfpd_donghaeng_fe.ui.matching.ongoing.BtnShareLocation
import com.kfpd_donghaeng_fe.ui.matching.ongoing.BtnStartDH
import com.kfpd_donghaeng_fe.ui.matching.ongoing.Contact
import com.kfpd_donghaeng_fe.ui.matching.ongoing.RequestPlace
import com.kfpd_donghaeng_fe.ui.matching.ongoing.SheetMiddle
import com.kfpd_donghaeng_fe.ui.matching.ongoing.SheetTop
import com.kfpd_donghaeng_fe.ui.matching.ongoing.UserProfile
import com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme
import com.kfpd_donghaeng_fe.viewmodel.auth.OngoingViewModel
import kotlinx.coroutines.CoroutineScope

// Hilt를 사용하려면 Activity에 @AndroidEntryPoint 어노테이션이 필요합니다.
// Hilt를 사용하려면 Application 클래스에 @HiltAndroidApp 어노테이션이 필요합니다.

/**
 * 동행 진행 중 화면의 메인 Composable
 * ViewModel로부터 상태를 받아 TopSheet와 BottomSheet를 조합합니다.
 */
@Composable
fun OngoingScreen(
    // Hilt를 통해 ViewModel 주입
    viewModel: OngoingViewModel = hiltViewModel()
) {
    // ViewModel의 uiState를 관찰(collect)하고, 변경될 때마다 Composable을 다시 그립니다.
    val uiState by viewModel.uiState.collectAsState()
    val currentPage = uiState.currentPage

    OngoingScreenContent(
        currentPage = currentPage,
        onStartClick = { viewModel.goToNextStep() },    // page 0 -> 1
        onEndClick = { viewModel.goToNextStep() },      // page 1 -> 2
        onFinalEndClick = { viewModel.completeAccompany() } // page 2 -> 완료
    )
}

/**
 * UI 상태를 받아 실제 화면을 그리는 Composable (Preview를 위해 분리)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OngoingScreenContent(
    currentPage: Int,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onFinalEndClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberStandardBottomSheetState(
        initialValue = SheetValue.Expanded,
        skipHiddenState = true // BottomSheet이 완전히 숨겨지지 않도록 설정
    )
    val scaffoldState = rememberBottomSheetScaffoldState(bottomSheetState = bottomSheetState)

    BottomSheetScaffold(
        scaffoldState = scaffoldState,
        sheetPeekHeight = 80.dp,
        sheetShape = RoundedCornerShape(topStart = 50.dp, topEnd = 50.dp),
        sheetContainerColor = Color.White,
        sheetDragHandle = { BottomSheetDefaults.DragHandle(color = Color.Gray, width = 80.dp) },
        // BottomSheet의 컨텐츠
        sheetContent = {
            // ViewModel의 이벤트를 전달하기 위해 SheetInside를 재정의
            CustomSheetInside(
                scope = scope,
                sheetState = bottomSheetState,
                page = currentPage,
                onStartClick = onStartClick,
                onEndClick = onEndClick,
                onFinalEndClick = onFinalEndClick
            )
        }
    ) { paddingValues ->
        // BottomSheetScaffold의 content 영역
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 배경 지도 (BottomSheet.kt에서 import)
            Background_Map()

            // TopSheet UI (TopSheet_userinfo.kt에서 import)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                color = Color(0xFFEA7A34), // MainOrange
                shape = RoundedCornerShape(bottomStart = 30.dp, bottomEnd = 30.dp)
            ) {
                // TopSheet의 컨텐츠 (TopSheet_userinfo.kt에서 import)
                // ViewModel의 currentPage를 progressStep으로 전달
                Batch(
                    requestPlaceContent = { RequestPlace(department = "신림 현대아파트", arrival = "장군봉 근린공원") },
                    userProfileContent = { UserProfile(name = "춤추는 무지", DH_score = 87) },
                    contactContent = { Contact() },
                    distanceText = "약속 장소까지 0.8km",
                    progressStep = currentPage // ViewModel의 상태를 전달
                )
            }
        }
    }
}

/**
 * ViewModel의 이벤트를 (`onStartClick` 등) `SheetButtonBatch`에 전달하기 위해
 * `SheetInside`를 커스텀 버전으로 새로 정의합니다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CustomSheetInside(
    scope: CoroutineScope,
    sheetState: SheetState,
    page: Int,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onFinalEndClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SheetTop, SheetMiddle은 BottomSheet.kt에서 import하여 재사용
        SheetTop(page)
        SheetMiddle(page)
        Spacer(modifier = Modifier.height(40.dp))

        // 버튼 이벤트를 ViewModel과 연결하기 위해 CustomSheetButtonBatch 사용
        CustomSheetButtonBatch(
            page = page,
            onStartClick = onStartClick,
            onEndClick = onEndClick,
            onFinalEndClick = onFinalEndClick
        )
    }
}

/**
 * `SheetButtonBatch` 또한 ViewModel의 이벤트를 받도록 커스텀 버전으로 새로 정의합니다.
 */
@Composable
private fun CustomSheetButtonBatch(
    page: Int,
    onStartClick: () -> Unit,
    onEndClick: () -> Unit,
    onFinalEndClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // BtnSOS, BtnShareLocation은 BottomSheet.kt에서 import하여 재사용
        BtnSOS(
            onClick = { /* SOS 로직 */ },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        BtnShareLocation(
            onClick = { /* 위치공유 로직 */ },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))

        // page 값에 따라 ViewModel의 적절한 함수를 호출
        when (page) {
            0 -> BtnStartDH(
                onClick = onStartClick, // viewModel.goToNextStep() 호출
                modifier = Modifier.weight(1f)
            )
            1 -> BtnEndtDH(
                onClick = onEndClick,   // viewModel.goToNextStep() 호출
                modifier = Modifier.weight(1f)
            )
            2 -> BtnEndtDH(
                onClick = onFinalEndClick, // viewModel.completeAccompany() 호출
                modifier = Modifier.weight(1f)
            )
        }
    }
}


//==================================================================================
//region Preview
//==================================================================================
@Preview(showBackground = true, name = "Page 0: Before Start")
@Composable
fun OngoingScreenPage0Preview() {
    KFPD_DongHaeng_FETheme(dynamicColor = false) {
        OngoingScreenContent(currentPage = 0, {}, {}, {})
    }
}

@Preview(showBackground = true, name = "Page 1: On Going")
@Composable
fun OngoingScreenPage1Preview() {
    KFPD_DongHaeng_FETheme(dynamicColor = false) {
        OngoingScreenContent(currentPage = 1, {}, {}, {})
    }
}

@Preview(showBackground = true, name = "Page 2: Before End")
@Composable
fun OngoingScreenPage2Preview() {
    KFPD_DongHaeng_FETheme(dynamicColor = false) {
        OngoingScreenContent(currentPage = 2, {}, {}, {})
    }
}
//endregion Preview
