package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.kfpd_donghaeng_fe.data.DummyRequests

@Composable
fun CompanionMatchingContent(
    navController: NavHostController, // ✅ NavController 수신
    modifier: Modifier = Modifier,
    // ViewModel 등 필요한 인자
) {
    RequestListContent(
        requests = DummyRequests, // 실제로 ViewModel Live Data/State
        modifier = Modifier.fillMaxSize(),
        navController = navController
    )
}