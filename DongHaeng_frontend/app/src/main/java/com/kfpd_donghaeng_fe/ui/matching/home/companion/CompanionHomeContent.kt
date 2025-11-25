package com.kfpd_donghaeng_fe.ui.matching.home.companion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import com.kfpd_donghaeng_fe.ui.matching.home.RequestUiModel
import com.kfpd_donghaeng_fe.ui.matching.home.MatchingSearchBar
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import com.kfpd_donghaeng_fe.ui.matching.components.RequestCard
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.Color
import com.kfpd_donghaeng_fe.ui.matching.home.toRequest

@Composable
fun CompanionHomeContent(
    nearbyRequests: List<RequestUiModel>,
    onSearchClick: () -> Unit,
    onChangeLocationClick: () -> Unit,
    onRequestClick: (Long) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .background(Color.White)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        MatchingSearchBar(
            placeholder = "근처 동행 요청 찾기",
            onClick = {
            //TODO: 검색
            },
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "내 주변 요청",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "2km 이내 거리만 동행할 수 있어요",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Text(
                text = "내 위치 변경 >",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable(onClick = onChangeLocationClick)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(nearbyRequests) { req ->
                RequestCard(
                    request = req.toRequest(),
                    onClick = { onRequestClick(req.id) }
                )
            }
        }
    }
}
