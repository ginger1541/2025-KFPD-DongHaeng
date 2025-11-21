package com.kfpd_donghaeng_fe.ui.auth.signin

import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.* // remember, mutableStateOf, getValue, setValue 사용을 위해 필요
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.auth.UserType
import com.kfpd_donghaeng_fe.ui.theme.* // 테마 및 색상 import (BrandOrange, TextBlack 등)
import com.kfpd_donghaeng_fe.viewmodel.auth.MakeAccountUiState

/** 페이지 3: 사용자 유형 Composable */
@Composable
fun UserTypePage(
    uiState: MakeAccountUiState,
    selectedType: UserType?,
    onUserTypeSelect: (UserType) -> Unit,
    onNextClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 32.dp ,top = 40.dp), // 상단 여백
        contentAlignment = Alignment.Center
    ) {
        StepCircle(1)
    }
    // 💡 전체 화면을 Box로 감싸서 콘텐츠와 하단 버튼을 분리합니다.
    Box(modifier = modifier.fillMaxSize()) {
        // 1️⃣ [콘텐츠 영역]: 제목, 설명, 카드들 (스크롤 가능한 영역)
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 카드 내부와 동일한 좌우 여백을 주어 정렬을 맞춥니다.
                .padding(horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally// 하단 버튼 공간 확보
        ) {
            Text(
                modifier = Modifier
                    .padding(top=50.dp),
                text = "사용자 유형",
                style = MaterialTheme.typography.headlineLarge,
                color = MainOrange,
                fontWeight = FontWeight.ExtraBold

            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "주로 어떤 용도로 서비스를 이용하실 예정인가요?",
                style = MaterialTheme.typography.bodySmall,
                color = DarkGray,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(80.dp))
            // --- "도움이 필요해요" 카드 ---
            SelectableCard(
                iconResId = R.drawable.ic_needy,
                text = "도움이 필요해요",
                subtext = "이동이나 일상생활에서 도움이 필요해요.",
                isSelected = (selectedType == UserType.NEEDY),
                onClick = { onUserTypeSelect(UserType.NEEDY) }
            )

            Spacer(modifier = Modifier.height(0.dp))

            // --- "도움을 드릴게요" 카드 ---
            SelectableCard(
                iconResId = R.drawable.ic_helper,
                text = "도움을 드릴게요",
                subtext = "다른 분들의 이동과 활동을 돕고 싶어요.",
                isSelected = (selectedType == UserType.HELPER),
                onClick = { onUserTypeSelect(UserType.HELPER) }
            )
        }

        // 2️⃣ [하단 고정 버튼 영역]
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter) // Box 하단 중앙에 고정
                .fillMaxWidth()
                .padding(horizontal = 25.dp) // 콘텐츠와 동일한 좌우 패딩
                .padding(bottom = 24.dp) // 하단 여백
        ) {
            // LoginPageButton 컴포넌트를 사용합니다. (이름이 "다음" 대신 "완료"로 가정)

            // 💡 버튼은 enabled 속성으로 선택 상태에 따라 활성화/비활성화됩니다.
            Button(
                onClick = onNextClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
                shape = RoundedCornerShape(8.dp),
                // 사용자 유형이 선택되지 않으면 버튼 비활성화
                enabled = (selectedType != null)
            ) {
                Text("완료", color = Color.White)
            }
        }
    }
}


/** 사용자 유형 선택 카드 Composable (재사용) */

/** 사용자 유형 선택 카드 Composable (재사용) */
@Composable
private fun SelectableCard(
    iconResId: Int,
    text: String,
    subtext: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isSelected) LightOrange else LightOrange
    val borderColor = if (isSelected) BrandOrange else Color.Transparent

    // 💡 1. 전체를 하나의 Box로 감싸서 카드와 아이콘을 겹치게 합니다.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp)// ⬅️ 카드 전체 영역이 가로 폭 전체를 사용
            .height(220.dp), // ⬅️ 겹치는 아이콘을 수용할 만큼 높이를 늘립니다.
        contentAlignment = Alignment.TopCenter // 내부 아이콘 정렬을 위해 TopCenter 설정
    ) {
        // 2. [아래에 깔리는 요소]: 카드 배경 영역
        Column(
            modifier = Modifier
                .offset(y = 50.dp) // ⬅️ 아이콘 공간을 위해 아래로 50dp 이동
                .fillMaxWidth()
                .height(150.dp) // 카드 자체의 높이
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .border(
                    width = 2.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable { onClick() }
                .padding(horizontal = 24.dp), // ⬅️ Box에 horizontal padding을 줌
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 빈 공간 (아이콘이 덮을 공간)
            Spacer(modifier = Modifier.height(40.dp))

            // 텍스트 내용
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MainOrange
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }

        // 3. [그 위에 겹쳐지는 요소]: 아이콘
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = text,
            modifier = Modifier.size(100.dp) // ⬅️ Box의 TopCenter에 위치 (offset 필요 없음)
                .zIndex(1f)
                .offset(y=-10.dp), // ⬅️ 카드 위에 명확하게 표시되도록 zIndex 설정 (선택 사항)
            tint = Color.Unspecified
        )
    }
}

// ------------------------------------------------------------------
/*
/** 🚀 프리뷰 컴포넌트 */
@Preview(showBackground = true, name = "User Type Selection")
@Composable
fun UserTypePagePreview_Interactive() {
    // 💡 상태를 관리하여 클릭 시 선택 상태가 변하는지 테스트
    var selectedTypeState by remember { mutableStateOf<UserType?>(null) } // 초기 상태: null

    // 💡 실제 앱의 테마로 감싸야 정확한 스타일이 적용됩니다.
    // YourAppTheme { // ⬅️ 실제 테마 컴포저블로 대체하세요.
    UserTypePage(
        selectedType = selectedTypeState,
        onUserTypeSelect = { newType ->
            // 클릭할 때마다 상태 업데이트
            selectedTypeState = if (selectedTypeState == newType) null else newType
            // 이미 선택된 것을 다시 클릭하면 해제되도록 토글 로직을 적용했습니다.
        },
        onNextClick = {
            println("완료 버튼 클릭. 선택된 타입: $selectedTypeState")
        },
        // 프리뷰에서 잘 보이도록 좌우 패딩을 적용합니다.
        modifier = Modifier.padding(top = 20.dp)
    )
    // }
}*/