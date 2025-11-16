package com.kfpd_donghaeng_fe.ui.matching.componentes

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kfpd_donghaeng_fe.ui.auth.UserType
import com.kfpd_donghaeng_fe.ui.matching.MatchingPhase
import com.kfpd_donghaeng_fe.ui.matching.components.CompanionMatchingContent
import com.kfpd_donghaeng_fe.ui.matching.components.RequestConfirmContent
import com.kfpd_donghaeng_fe.ui.matching.components.RequestTimePicker
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.viewmodel.matching.MatchingViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun BottomMatchingSheetContent(
    modifier: Modifier = Modifier,
    role: UserType = UserType.NEEDY,
    navController: NavHostController,
    viewModel: MatchingViewModel = hiltViewModel(),
    onNavigateToOngoing: () -> Unit,
) {
    // 2. 💡 ViewModel의 currentPhase 상태를 관찰합니다.
    val currentPhase = viewModel.currentPhase.collectAsState().value

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        when (role) {
            UserType.NEEDY -> {
                when (currentPhase) {
                    MatchingPhase.OVERVIEW -> RequesterOverviewContent(
                        onBookClicked = viewModel::navigateToBooking
                    )
                    MatchingPhase.BOOKING -> RequestBookingContent(
                        onTimePickerRequested = viewModel::navigateToTimeSelection,
                        onCancel = viewModel::navigateToOverview
                    )
                    MatchingPhase.TIME_SELECTION -> RequestTimePicker(
                        currentDateTime = viewModel.selectedDateTime.value,
                        onConfirm = { newDateTime ->
                            viewModel.updateSelectedTime(newDateTime)
                            // TODO: 실제 경로 확인 로직 호출 (findRouteAndCalculatePrice)
                            viewModel.navigateToConfirm()
                        },
                        onCancel = viewModel::navigateToBooking
                    )
                    MatchingPhase.CONFIRM -> RequestConfirmContent(
                        viewModel = viewModel,
                        onFinalRequest = {
                            // 1. 최종 매칭 API 호출 (TODO: 실제 API 로직)
                            println("최종 매칭 API 호출")
                            // 2. OngoingScreen으로 이동
                            onNavigateToOngoing() },
                        onEdit = viewModel::navigateToBooking
                    )
                }
            }
            UserType.HELPER -> CompanionMatchingContent(
                navController = navController
            )
        }
    }
}

@Composable
fun RequesterOverviewContent(onBookClicked: () -> Unit) {
    Column(
        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Text(
            text = "현재 2명의 도우미가 활동 중이에요",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryDarkText
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "예상 매칭 시간: 3-8분",
            fontSize = 14.sp,
            color = AppColors.SecondaryText,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 동행 요청하기
            Button(
                onClick = onBookClicked,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                modifier = Modifier
                    .weight(1f)
                    .height(72.dp)
            ) {
                Text(
                    text = "동행 요청하기",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(60.dp))
    }
}