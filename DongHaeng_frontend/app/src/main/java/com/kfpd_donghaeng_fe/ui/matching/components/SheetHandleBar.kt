package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun SheetHandleBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp) // 인디케이터가 차지할 높이
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Divider(
            color = AppColors.SheetHandleColor,
            modifier = Modifier
                .width(95.dp)
                .clip(RoundedCornerShape(4.dp))
                .height(4.dp)
        )
    }
}