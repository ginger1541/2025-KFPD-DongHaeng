package com.kfpd_donghaeng_fe.ui.matching

import RatingSection
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.data.PraiseTag
import com.kfpd_donghaeng_fe.data.PredefinedPraiseTags
import com.kfpd_donghaeng_fe.ui.matching.components.CompletionHeader
import com.kfpd_donghaeng_fe.ui.matching.components.PraiseTagSection
import com.kfpd_donghaeng_fe.ui.matching.components.RewardBox
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun ReviewScreen(
    totalTime: String = "18분",
    distance: String = "2.1km",
    earnedPoints: Int = 250,
    onSubmitReview: (rating: Int, selectedTags: List<PraiseTag>, message: String) -> Unit = { _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var rating by remember { mutableStateOf(0) }
    var selectedTags by remember { mutableStateOf(setOf<Int>()) }
    var message by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // 상단 완료 헤더
        CompletionHeader(
            totalTime = totalTime,
            distance = distance
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 별점 평가 섹션
        RatingSection(
            rating = rating,
            onRatingChange = { rating = it }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 칭찬 배지 섹션
        PraiseTagSection(
            selectedTags = selectedTags,
            onTagToggle = { tagId ->
                selectedTags = if (selectedTags.contains(tagId)) {
                    selectedTags - tagId
                } else {
                    selectedTags + tagId
                }
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 감사 메시지 섹션
        ThankYouMessageSection(
            message = message,
            onMessageChange = { message = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 획득 보상 박스
        RewardBox(
            points = earnedPoints,
            volunteerTime = "0h 9m"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 평가 완료하기 버튼
        Button(
            onClick = {
                val selectedPraiseTags = PredefinedPraiseTags.filter { it.id in selectedTags }
                onSubmitReview(rating, selectedPraiseTags, message)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.AccentOrange
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "평가 완료하기",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ThankYouMessageSection(
    message: String,
    onMessageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "감사 메시지",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryDarkText
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "감사 메시지를 남겨주세요(50자)",
            fontSize = 13.sp,
            color = AppColors.SecondaryText
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = message,
            onValueChange = { if (it.length <= 50) onMessageChange(it) },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            placeholder = {
                Text(
                    text = "입력",
                    color = Color(0xFFCCCCCC)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = AppColors.AccentOrange
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Preview(showBackground = true, heightDp = 1400)
@Composable
fun PreviewReviewScreen() {
    ReviewScreen(
        totalTime = "18분",
        distance = "2.1km",
        earnedPoints = 250
    )
}