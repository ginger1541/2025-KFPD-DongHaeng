package com.kfpd_donghaeng_fe.ui.matching.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.data.PraiseTag
import com.kfpd_donghaeng_fe.data.PredefinedPraiseTags
import com.kfpd_donghaeng_fe.ui.theme.AppColors

@Composable
fun PraiseTagSection(
    selectedTags: Set<Int>,
    onTagToggle: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "칭찬 배지를 선택해주세요",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryDarkText
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "상대방에게 배지가 전달됩니다.",
            fontSize = 13.sp,
            color = AppColors.SecondaryText
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 태그들을 FlowRow처럼 배치
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 첫 번째 줄
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PraiseTagChip(
                    tag = PredefinedPraiseTags[0],
                    isSelected = selectedTags.contains(PredefinedPraiseTags[0].id),
                    onClick = { onTagToggle(PredefinedPraiseTags[0].id) }
                )
                PraiseTagChip(
                    tag = PredefinedPraiseTags[1],
                    isSelected = selectedTags.contains(PredefinedPraiseTags[1].id),
                    onClick = { onTagToggle(PredefinedPraiseTags[1].id) }
                )
            }

            // 두 번째 줄
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PraiseTagChip(
                    tag = PredefinedPraiseTags[2],
                    isSelected = selectedTags.contains(PredefinedPraiseTags[2].id),
                    onClick = { onTagToggle(PredefinedPraiseTags[2].id) }
                )
                PraiseTagChip(
                    tag = PredefinedPraiseTags[3],
                    isSelected = selectedTags.contains(PredefinedPraiseTags[3].id),
                    onClick = { onTagToggle(PredefinedPraiseTags[3].id) }
                )
            }

            // 세 번째 줄
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                PraiseTagChip(
                    tag = PredefinedPraiseTags[4],
                    isSelected = selectedTags.contains(PredefinedPraiseTags[4].id),
                    onClick = { onTagToggle(PredefinedPraiseTags[4].id) }
                )
                PraiseTagChip(
                    tag = PredefinedPraiseTags[5],
                    isSelected = selectedTags.contains(PredefinedPraiseTags[5].id),
                    onClick = { onTagToggle(PredefinedPraiseTags[5].id) }
                )
            }
        }
    }
}

@Composable
fun PraiseTagChip(
    tag: PraiseTag,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) AppColors.AccentOrange else AppColors.ReviewBackground
    ) {
        Text(
            text = tag.text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (isSelected) Color.White else AppColors.PrimaryDarkText,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPraiseTagSection() {
    var selectedTags by remember { mutableStateOf(setOf(1, 3)) }
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
}