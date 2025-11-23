package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.data.Request
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun RequestCard(
    request: Request,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.CardBackground),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 거리
            Text(
                text = "내 위치에서 ${request.distance}",
                fontSize = 12.sp,
                color = AppColors.SecondaryText,
                modifier = Modifier.align(Alignment.End)
            )
            Spacer(modifier = Modifier.height(4.dp))

            // 출발/도착
            LocationRow(
                label = "출발",
                location = request.departure,
                isStart = true,
                pricePoints = request.pricePoints
            )
            Spacer(modifier = Modifier.height(8.dp))
            LocationRow(
                label = "도착",
                location = request.arrival,
                isStart = false
            )
            Spacer(modifier = Modifier.height(12.dp))

            // 소요시간
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = request.departureTime,
                    fontSize = 14.sp,
                    color = AppColors.SecondaryText
                )
                Text(
                    text = request.travelTime,
                    fontSize = 14.sp,
                    color = AppColors.SecondaryText
                )
            }
        }
    }
}

@Composable
fun LocationRow(
    label: String,
    location: String,
    isStart: Boolean,
    pricePoints: Int? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Label Box
        LabelBox(
            text = label,
            backgroundColor = AppColors.LabelBackground,
            textColor = Color.White
        )
        Spacer(Modifier.width(8.dp))

        Text(
            text = location,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.PrimaryDarkText,
            modifier = Modifier.weight(1f)
        )

        // Price/Points (출발 Row에만 표시)
        if (isStart && pricePoints != null) {
            Text(
                text = "${pricePoints}점",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.AccentOrange
            )
        }
    }
}


@Composable
fun LabelBox(text: String, backgroundColor: Color, textColor: Color) {
    Box(
        modifier = Modifier
            .background(backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}
