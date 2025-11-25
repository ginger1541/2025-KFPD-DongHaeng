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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.kfpd_donghaeng_fe.data.PraiseTag
import com.kfpd_donghaeng_fe.data.PredefinedPraiseTags
import com.kfpd_donghaeng_fe.ui.matching.components.CompletionHeader
import com.kfpd_donghaeng_fe.ui.matching.components.PraiseTagSection
import com.kfpd_donghaeng_fe.ui.matching.components.RewardBox
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.viewmodel.matching.ReviewViewModel

@Composable
fun ReviewScreen(
    totalTime: String = "18분",
    distance: String = "2.1km",
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
            shape = RoundedCornerShape(10.dp)
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

        TextField(
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
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF5F5F5),  // 포커스 시 배경
                unfocusedContainerColor = Color(0xFFF5F5F5),  // 기본 배경
                disabledContainerColor = Color(0xFFF5F5F5),
                focusedIndicatorColor = Color.Transparent,  // 밑줄 제거
                unfocusedIndicatorColor = Color.Transparent,  // 밑줄 제거
                disabledIndicatorColor = Color.Transparent
            ),
        )
    }
}
@Composable
fun ReviewRoute(
    matchId: Long,
    partnerId: Long,
    navController: NavHostController,
    displayTime: String,
    displayDist: String,
    viewModel: ReviewViewModel = hiltViewModel()
) {
    ReviewScreen(
        totalTime = displayTime,
        distance = displayDist,
        onSubmitReview = { rating, selectedTags, message ->
            // 1. 태그 객체를 문자열 리스트로 변환 (서버가 String 리스트를 원함)
            val badgeStrings = selectedTags.map { it.text }

            // 2. API 호출
            viewModel.submitReview(
                matchId = matchId,
                revieweeId = partnerId,
                rating = rating,
                comment = message,
                badges = badgeStrings,
                onSuccess = {
                    // 3. 성공 시 홈으로 이동 (모든 매칭 관련 스택 제거)
                    navController.navigate("home/NEEDY") { // 또는 HELPER
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    )
}

//@Preview(showBackground = true, heightDp = 1000) // 높이를 넉넉하게 설정
//@Composable
//fun PreviewReviewScreen() {
//    // 테마 적용 (선택 사항, 폰트나 색상을 정확히 보려면 권장)
//    com.kfpd_donghaeng_fe.ui.theme.KFPD_DongHaeng_FETheme {
//        ReviewScreen(
//            totalTime = "18분",
//            distance = "2.1km",
//            earnedPoints = 250,
//            // 클릭 이벤트는 비워둡니다.
//            onSubmitReview = { _, _, _ -> }
//        )
//    }
//}