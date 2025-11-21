package com.kfpd_donghaeng_fe.ui.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.auth.UserType
import com.kfpd_donghaeng_fe.ui.theme.*
import com.kfpd_donghaeng_fe.util.navigateToNewSearchFlow

@Composable
fun HomeScreen(userType: UserType, navController: NavController) {
    // 화면 전체를 스크롤 가능하게
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF9F9F9)) // 전체 배경색
    ) {
            // 주황색 헤더 (배경 역할)
            HomeHeader()

            // 동행 지수 카드 (헤더 위에 겹쳐짐)
            ScoreCard(
                modifier = Modifier
                    .padding(horizontal = 24.dp) // 좌우 여백
                    .padding(top = 16.dp) // 헤더와 카드 사이의 여백
            )
        // 3. 카드 아래 영역 (사용자 유형별 컨텐츠)
        Spacer(modifier = Modifier.height(24.dp)) // 카드와 내용 사이 여백

        Column(
            modifier = Modifier.padding(horizontal = 24.dp) // 좌우 공통 여백
        ) {
            when (userType) {
                UserType.NEEDY -> NeedyContent(
                    navController = navController,
                    userType = userType
                )
                UserType.HELPER -> HelperContent(
                    navController = navController,
                    userType = userType
                )
            }

        }

        Spacer(modifier = Modifier.height(24.dp)) // 스크롤 하단 여백
    }
}


@Composable
private fun ScoreCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // 그림자
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "동행지수",
                style = MaterialTheme.typography.bodyMedium,
                color = DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TODO: 동행 지수 받아 와야 함
            Text(
                text = "70",
                fontSize = 60.sp,
                fontWeight = FontWeight.Bold,
                color = BrandOrange
            )

            Text(
                text = "신뢰받는 동행자",
                style = MaterialTheme.typography.bodyLarge,
                color = TextBlack
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 뱃지
            Row(
                horizontalArrangement = Arrangement.spacedBy(24.dp) // 뱃지 사이 간격
            ) {
                Icon(painterResource(id = R.drawable.ic_mission), "badge 1", tint = BrandOrange)
                Icon(painterResource(id = R.drawable.ic_mission), "badge 2", tint = BrandOrange)
                Icon(painterResource(id = R.drawable.ic_mission), "badge 3", tint = BrandOrange)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "이번 달 동행 4회 달성",
                style = MaterialTheme.typography.bodySmall,
                color = DarkGray
            )
        }
    }
}


/**
 * 홈 화면의 상단 주황색 헤더 Composable
 */
@Composable
private fun HomeHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp) // 헤더의 높이 (조절 가능)
            .background(BrandOrange) // 테마의 주황색
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // 1. 로고
            Image(
                painter = painterResource(id = R.drawable.ic_logo_white), // 흰색 로고 필요
                contentDescription = "로고",
                modifier = Modifier.size(width = 80.dp, height = 80.dp) // (크기 조절)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 환영 메시지
            Text(
                text = "동행하는 우인님,", // (TODO: 실제 사용자 이름)
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "어서오세요!",
                color = Color.White,
                fontSize = 18.sp
            )
        }

        // 3. 알림 아이콘 (우측 상단)
        IconButton(
            onClick = { /* TODO: 알림 화면으로 이동 */ },
            modifier = Modifier.align(Alignment.TopEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications, // 머티리얼 기본 아이콘
                contentDescription = "알림",
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

/**
 * Needy (도움이 필요해요) 사용자 홈 컨텐츠 (버튼 버전)
 */
@Composable
private fun NeedyContent(
    navController: NavController, // ✅ NavController 인자 추가
    userType: UserType,           // ✅ UserType 인자 추가
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "주변 동행자에게 요청하세요",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "동행 요청하기" 버튼
        Button(
            onClick = { navController.navigateToNewSearchFlow(userType) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightOrange),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "동행 예약하기",
                    color = BrandOrange,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_reserve),
                    contentDescription = "요청하기",
                    tint = BrandOrange
                )
            }
        }
    }
}

/**
 * Helper (도움을 드릴게요) 사용자 홈 컨텐츠 (버튼 버전)
 */
@Composable
private fun HelperContent(
    navController: NavController, // ✅ NavController 인자 추가
    userType: UserType,           // ✅ UserType 인자 추가
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "현재 3개의 요청이 있어요",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextBlack
        )

        Spacer(modifier = Modifier.height(16.dp))

        // "주변 요청 확인하기" 버튼
        // TODO: 없어지긴 했는데 어쨌든 수정
        Button(
            onClick = { navController.navigateToNewSearchFlow(userType) },
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = LightOrange)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "주변 요청 확인하기",
                    color = BrandOrange,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_search),
                    contentDescription = "요청 확인",
                    tint = BrandOrange
                )
            }
        }
    }
}