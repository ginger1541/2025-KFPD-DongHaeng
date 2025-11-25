package com.kfpd_donghaeng_fe.ui.matching.ongoing
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
import androidx.compose.foundation.clickable
import com.kfpd_donghaeng_fe.R // 3. 프로젝트의 리소스(R) 클래스 사용을 위해 필요-
// --- 기존 import 및 함수 (UserProfile, Contact, RequestPlace)는 동일하다고 가정 ---


import androidx.compose.material3.*
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.font.FontWeight
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.OngoingRequestEntity




@Composable
fun MsgImg_Onclick(){
    // TODO: 클릭시 화면 넘어가기 로직 추가
}


@Composable // 메세지이미지 클릭을 위한 함수
fun MessageIconButton(
    MessageImg: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 💡 IconButton 대신 Box와 clickable Modifier를 사용하여 원형 배경/효과를 제거합니다.
    Box(
        modifier = modifier
            .size(45.dp) // 💡 Contact 함수에서 사용되는 PhoneImg와 크기를 통일 (45dp)
            .clickable(onClick = onClick) // ✅ 클릭 가능하도록 만듭니다.
            .padding(8.dp) // 💡 클릭 영역 확보를 위해 내부 패딩을 줍니다. (선택 사항)
    ) {
        Image(
            painter = MessageImg,
            contentDescription = "MessageImg",
            // 💡 Box의 크기(45dp) 내에서 이미지를 중앙에 배치합니다.
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable //메세지 클릭시
fun MessageImg_Onclick(){

}
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
        horizontalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // 아이콘 크기 통일 및 확대
        MessageIconButton(
            MessageImg = MessageImg,
            onClick = {

            },
            modifier = Modifier.padding(8.dp)
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
        modifier = Modifier.offset(x = (-12).dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(painter = rectImg, contentDescription = "rectImg")
        Text(
            text = "$department  >  $arrival", // 💡 텍스트를 하나로 합쳐 간결하게 표시
            color = Color.White,
            fontWeight = FontWeight.Bold
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
            color = Color.White, // 진행된 색상 (이미지에서는 Thumb 위치로만 표현)
            trackColor = Color.White.copy(alpha = 0.4f)
        )

        Spacer(Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            stepLabels.forEachIndexed { index, label ->
                val isHighlighted = index == currentStep //  현재 스텝만 강조하도록 변경
                Text(
                    text = label,
                    color = if (isHighlighted) Color.White else Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold
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
                            .height(58.dp)
                            .width(2.dp)
                    )

                    // 3. 오른쪽 영역 (연락처 아이콘)
                    Box(
                        modifier = Modifier
                            .weight(1f) // 50% 공간 차지
                            .padding(start = 30.dp), // 구분선 다음부터의 여백
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



// 1. 엔티티를 인자로 받도록 TopSheet 함수를 수정합니다.
@Composable
fun TopSheet(ongoingEntity: OngoingEntity, ongoingRequestEntity: OngoingRequestEntity) {

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MainOrange // 배경색
    ) {
        Batch(
            requestPlaceContent = {
                RequestPlace(
                    department = ongoingRequestEntity.startAddress,
                    arrival = ongoingRequestEntity.destinationAddress
                )
            },
            userProfileContent = {
                UserProfile(
                    name = ongoingRequestEntity.Name,
                    DH_score = ongoingRequestEntity.DHScore
                )
            },
            contactContent = {
                Contact()
            },
            distanceText = "", // (엔티티에 필드가 없다면 임시로 유지)
            progressStep = ongoingEntity.OngoingPage
        )
    }
}


