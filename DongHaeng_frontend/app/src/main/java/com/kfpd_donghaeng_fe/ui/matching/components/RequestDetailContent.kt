// main/java/com/kfpd_donghaeng_fe/ui/matching/components/RequestDetailContent.kt
package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.ui.theme.AppColors

// RequestDetailContent - Mockup image_b2695f.jpg의 하단 시트
@Composable
fun RequestDetailContent(
    onNext: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var requestMessage by remember { mutableStateOf("") }
    val maxChar = 500

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 24.dp)
    ) {
        Text(
            text = "요청 사항",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryDarkText
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 요청 사항 입력 필드
        OutlinedTextField(
            value = requestMessage,
            onValueChange = {
                if (it.length <= maxChar) requestMessage = it
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            placeholder = {
                Text(
                    "최대 ${maxChar}자까지 입력 가능합니다.",
                    color = AppColors.SecondaryText.copy(alpha = 0.7f)
                )
            },
            maxLines = 10,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.AccentOrange,
                unfocusedBorderColor = Color(0xFFE0E0E0)
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 버튼 Row (취소/확인)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.LightGrayButton),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("취소", color = AppColors.PrimaryDarkText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onNext,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("확인", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}