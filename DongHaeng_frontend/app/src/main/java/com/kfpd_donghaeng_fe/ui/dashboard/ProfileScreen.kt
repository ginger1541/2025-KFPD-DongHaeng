package com.kfpd_donghaeng_fe.ui.dashboard

// 💡 필요한 import들을 모두 추가했습니다.
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.theme.MainOrange // 💡 MainOrange가 정의되어 있다고 가정합니다.




/**
 * 💡 상단 프로필 섹션: 이미지와 일치하도록 수정
 */
@Composable
fun TopUserBox(name: String, username: String) {

    val userProfileImg = painterResource(id = R.drawable.ic_user_pro_edit) // '사람 + 연필' 아이콘

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
            .background(MainOrange)
            .clip(RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)),
    ) {
        //* TODO: 알람 버튼 추가
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Image(
                painter = userProfileImg,
                contentDescription = "사용자 프로필 수정",
                modifier = Modifier.size(100.dp)
            )
            Text(
                text = name,
                color = Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = username,
                color = Color.White,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 💡 '받은 동행 평가' 아이템: Row 레이아웃으로 수정
 */
@Composable
fun Reveiw(num: Int, msg: String) {

    val reviewerIcon = painterResource(id = R.drawable.ic_user)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Image(
            painter = reviewerIcon,
            contentDescription = "평가 아이콘",
            modifier = Modifier.size(36.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "$num",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.width(20.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))


        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.LightGray.copy(alpha = 0.3f),
            modifier = Modifier
        ) {
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}


@Composable
fun Reveiw2(name: String, department: String, arrival: String, date: Int, msg: String) {
    val reviewerProfileImg = painterResource(id = R.drawable.ic_reveiw_prof)

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Image(
            painter = reviewerProfileImg,
            contentDescription = "후기 작성자 프로필",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            //이름
            Text(
                text = name,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            // 장소
            Text(
                text = "$department > $arrival / $date" + "일 전",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            //  후기 메시지
            Text(
                text = msg,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}


@Composable
fun SectionHeader(title: String, onMoreClicked: () -> Unit = {}) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(
            onClick = {
                // TODO: "더보기" 클릭 시 실행할 동작
            },

            contentPadding = PaddingValues(4.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = MainOrange // 💡 텍스트 색상 지정
            )
        ) {
            Text(
                text = "더보기 >",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}


@Composable
fun ReveiwScreen() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp) // 섹션 간 간격
    ) {
        //동행 평가들
        SectionHeader("받은 동행 평가")
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Reveiw(11, "친절해요")
            Reveiw(9, "응답이 빨라요")
            Reveiw(4, "시간약속을 잘 지켜요")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 동행 후기들
        SectionHeader("받은 동행 후기 36")
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Reveiw2("장*민", "서울 관악구", "세브란스 병원", 3, "친절하게 대해주셔서 감사합니다.")
            Reveiw2("손*연", "서강대학교", "푸르메 병원", 10, "덕분에 늦지 않고 도착할 수 있었어요.")
            Reveiw2("장*세", "센트럴시티 터미널", "방동역 4번 출구", 1, "감사합니다~~")
            Reveiw2("조*찬", "서울남산초등학교", "중앙 재래시장", 1, "감사합니다. 다음 번에도 또 뵙고 싶어요^^")
        }
    }
}


/**
 * 💡 프리뷰: 스크롤 가능하도록 수정
 */
@Preview(showBackground = true)
@Composable
fun UserReveiwScreen() {
    Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            TopUserBox("동행하는 우인이", "@donghang1234")
            ReveiwScreen()
        }
    }
}

