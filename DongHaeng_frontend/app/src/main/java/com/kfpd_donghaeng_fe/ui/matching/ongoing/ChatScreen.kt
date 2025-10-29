package com.kfpd_donghaeng_fe.ui.matching.ongoing

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex


@Composable
fun Chat(){

}


@Preview(showBackground = true)
@Composable
fun ChattingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        // 2️⃣ 상단 시트 (유저 프로필)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .zIndex(1f) // 상단 시트를 위로
                .padding(0.dp)

        ) {
            TopSheet(0)

        }

    }
}