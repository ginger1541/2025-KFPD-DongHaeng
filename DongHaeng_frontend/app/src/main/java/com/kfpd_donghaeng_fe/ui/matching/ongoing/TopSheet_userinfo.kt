package com.kfpd_donghaeng_fe.ui.matching.ongoing
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface // Material3 사용 시
import com.kfpd_donghaeng_fe.ui.theme.MainOrange
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color // Color 사용을 위해 import
import androidx.compose.ui.unit.dp
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image // 1. Image 컴포저블 자체를 사용하기 위해 필요
import com.kfpd_donghaeng_fe.R // 3. 프로젝트의 리소스(R) 클래스 사용을 위해 필요

// --- 기존 import 및 함수 (UserProfile, Contact, RequestPlace)는 동일하다고 가정 ---


import androidx.compose.material3.*
import androidx.compose.ui.text.font.FontWeight


// 메인 색상 정의 (Preview를 위해 임의로 지정)
val MainOrange = Color(0xFFEA7A34)

@Composable
fun UserProfile(name: String, DH_score: Int) { //유저 프로필사진,닉네임,동행지수
    val ProfileImg = painterResource(id = R.drawable.def_prof_pic)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        // 💡 간격 확대: 프로필 이미지와 텍스트 사이 간격 증가
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(
            painter = ProfileImg,
            contentDescription = "ProfileImg",
            // 💡 이미지 크기 확대
            modifier = Modifier.size(56.dp)
        )
        Column {
            Text(
                text = "$name",
                color = Color.White,
                // 💡 titleLarge 유지 (원래 컸음)
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "동행지수 $DH_score 점",
                color = Color.White,
                // 💡 크기 확대: bodySmall -> bodyMedium
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
fun Contact() {
    val PhoneImg = painterResource(id = R.drawable.phone_icon)
    val MessageImg = painterResource(id = R.drawable.message_icon)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        // 💡 간격 확대: 아이콘 간 간격 증가
        horizontalArrangement = Arrangement.spacedBy(40.dp)
    ) {
        // 아이콘 크기 통일 및 확대
        Image(
            painter = MessageImg,
            contentDescription = "MessageImg",
            // 💡 아이콘 크기 확대
            modifier = Modifier.size(30.dp)
        )
        Image(
            painter = PhoneImg,
            contentDescription = "PhoneImg",
            // 💡 아이콘 크기 확대
            modifier = Modifier.size(45.dp)
        )
    }
}

@Composable
fun RequestPlace(department: String, arrival: String) { //요청 출발 장소 - 도착장소
    // 💡 이미지 리소스는 프로젝트에 맞게 확인이 필요합니다.
    val rectImg = painterResource(id = R.drawable.rect_icon)
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Image(painter = rectImg, contentDescription = "rectImg")
        Text(
            text = "$department  >  $arrival", // 💡 텍스트를 하나로 합쳐 간결하게 표시
            color = Color.White
        )
    }
}


@Composable
fun ProgressStepBar(
    totalSteps: Int,
    currentStep: Int,
    modifier: Modifier = Modifier
) {

    val progress = (currentStep.toFloat() / (totalSteps - 1)).coerceIn(0f, 1f)
    val stepLabels = listOf("요청 접수", "동행", "완료")

    Column(modifier = modifier.fillMaxWidth()) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
            color = Color.White, // 💡 진행된 색상 (이미지에서는 Thumb 위치로만 표현)
            trackColor = Color.White.copy(alpha = 0.4f)
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stepLabels.forEachIndexed { index, label ->
                val isHighlighted = index == currentStep // 💡 현재 스텝만 강조하도록 변경
                Text(
                    text = label,
                    color = if (isHighlighted) Color.White else Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
fun Batch(
    requestPlaceContent: @Composable () -> Unit,
    userProfileContent: @Composable () -> Unit,
    contactContent: @Composable () -> Unit,
    distanceText: String,
    progressStep: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 24.dp),
        // 💡 전체적인 세로 간격을 조금 줄여서 요소들을 가깝게 배치
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 1. 출발/도착지 (상단)
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            requestPlaceContent()
        }

        // 💡 [수정] 프로필, 연락처, 거리 텍스트를 하나의 Column으로 묶어 관리
        // 💡 [수정] 프로필, 연락처, 거리 텍스트를 하나의 Column으로 묶어 관리

        Column(modifier = Modifier.fillMaxWidth()) {
            // 2. 프로필 + 연락처 아이콘
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 왼쪽 영역 (프로필)
                Row(
                    modifier = Modifier.fillMaxWidth(), // 가로 전체 채우기
                    verticalAlignment = Alignment.CenterVertically // 중앙 수직 정렬
                ) {
                    // 1. 왼쪽 영역 (프로필 정보)
                    Box(
                        modifier = Modifier
                            .weight(1f) // 50% 공간 차지
                            .padding(end = 30.dp), // 💡 구분선 전까지의 여백
                        contentAlignment = Alignment.CenterEnd // 내용을 오른쪽 끝(구분선 방향)으로 정렬
                    ) {
                        userProfileContent()
                    }

                    // 2. 중앙 수직 구분선
                    Divider(
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .height(48.dp)
                            .width(1.dp)
                    )

                    // 3. 오른쪽 영역 (연락처 아이콘)
                    Box(
                        modifier = Modifier
                            .weight(1f) // 50% 공간 차지
                            .padding(start = 50.dp), // 구분선 다음부터의 여백
                        contentAlignment = Alignment.CenterStart // 💡 내용을 왼쪽 끝(구분선 방향)으로 정렬
                    ) {
                        contactContent()
                    }
                }
            }

            // 거리 텍스트
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(
                    text = distanceText,
                    color = Color.White.copy(alpha = 0.8f),
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // 3. 단계별 로딩 바
        ProgressStepBar(totalSteps = 3, currentStep = progressStep)
    }
}


@Preview(showBackground = true)
@Composable
fun UserProfilePreview() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MainOrange //배경색
    ) {
        Batch(
            requestPlaceContent = {
                RequestPlace(department = "신림 현대아파트", arrival = "장군봉 근린공원")
            },
            userProfileContent = {
                UserProfile(name = "춤추는 무지", DH_score = 87)
            },
            contactContent = {
                Contact()
            },
            distanceText = "약속 장소까지 0.8km",
            progressStep = 2 // "요청 접수" 단계
        )
    }
}



/* <- 로딩 바 (보류)
@Preview(showBackground = true)
@Composable
fun LinearDeterminateIndicator() {
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Create a coroutine scope

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = {
            loading = true
            scope.launch {
                loadProgress { progress ->
                    currentProgress = progress
                }
                loading = false // Reset loading when the coroutine finishes
            }
        }, enabled = !loading) {
            Text("Start loading")
        }

        if (loading) {
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Iterate the progress value */
suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(100)
    }
}*/