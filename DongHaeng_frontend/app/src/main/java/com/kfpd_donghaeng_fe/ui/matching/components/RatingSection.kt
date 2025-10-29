import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.ui.theme.AppColors

// ReviewScreen.kt

@Composable
fun RatingSection(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = "오늘의 동행은 어떠셨나요?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.PrimaryDarkText
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "동행의 만족도를 평가해주세요",
            fontSize = 13.sp,
            color = AppColors.SecondaryText
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 별점
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(5) { index ->
                Icon(
                    imageVector = if (index < rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = null,
                    tint = if (index < rating) AppColors.AccentColor else Color(0xFFCCCCCC),
                    modifier = Modifier
                        .size(48.dp)
                        .clickable { onRatingChange(index + 1) }
                )
            }
        }
    }
}