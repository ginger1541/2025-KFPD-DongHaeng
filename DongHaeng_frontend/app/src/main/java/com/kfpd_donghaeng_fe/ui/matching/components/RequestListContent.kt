package com.kfpd_donghaeng_fe.ui.matching.components
// (feature/accompany/ui/matching/componentes/RequestListContent.kt 에 위치)

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.util.navigateToRequestDetail

@Composable
fun RequestListContent(
    requests: List<Request>,
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "실시간 요청",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.PrimaryDarkText
                )
                Text(
                    text = "조건에 맞는 최상단 ${requests.size}개만 보여요.",
                    fontSize = 12.sp,
                    color = AppColors.SecondaryText
                )
            }
            Text(
                text = "모든 요청 확인 >",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.PrimaryDarkText,
                modifier = Modifier.clickable { /* 모든 요청 확인 동작 */ }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(requests, key = { it.id }) { request ->
                RequestCard(
                    request = request,
                    onClick = {
                        // 항목 클릭 시 전체 화면으로 이동
                        val requestIdLong = request.id.toLong()
                        Log.d("NAV_DEBUG", "CLICKED: Request ID = $requestIdLong")
                        navController.navigateToRequestDetail(request.id.toLong())
                    }
                )
            }
        }
    }
}
