package com.kfpd_donghaeng_fe.ui.auth.signup

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.ui.theme.*

/** 페이지 4: 이용 약관 Composable */
@Composable
fun TermsPage(
    // ViewModel로 부터 받을 상태
    allAgreed: Boolean,
    serviceAgreed: Boolean,
    privacyAgreed: Boolean,
    locationAgreed: Boolean,

    // ViewModel로 전달할 이벤트
    onAllAgreeChange: (Boolean) -> Unit,
    onServiceAgreeChange: (Boolean) -> Unit,
    onPrivacyAgreeChange: (Boolean) -> Unit,
    onLocationAgreeChange: (Boolean) -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 3개 필수 약관이 모두 동의 됐는지 확인
    val allRequiredAgreed = serviceAgreed && privacyAgreed && locationAgreed

    Column(modifier = modifier.fillMaxSize()) {

        // 1. 상단 로고
        Image(
            painter = painterResource(id = R.drawable.ic_logo_orange), // 로고 아이콘
            contentDescription = "앱 로고",
            modifier = Modifier
                .padding(top = 20.dp, bottom = 12.dp)
                .size(width = 100.dp, height = 100.dp) // (크기 조절 필요)
                .align(Alignment.CenterHorizontally)
        )

        Text(
            text = "서비스 이용을 위한 약관에 동의해주세요.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextBlack,
            modifier = Modifier
                .padding(bottom = 32.dp)
                .align(Alignment.CenterHorizontally)
        )

        // 2. 전체 동의
        TermRow(
            text = "약관 전체 동의",
            checked = allAgreed, // '모두 동의' 상태
            onCheckedChange = onAllAgreeChange,
            isTitle = true // 굵은 글씨
        )

        Divider(
            modifier = Modifier.padding(vertical = 16.dp),
            color = MediumGray
        )

        // 3. 개별 약관
        TermRow(
            text = "[필수] 서비스 이용약관",
            checked = serviceAgreed,
            onCheckedChange = onServiceAgreeChange,
            onViewClick = { /* TODO: '보기' 클릭 시 웹뷰/다이얼로그 표시 */ }
        )
        Spacer(modifier = Modifier.height(16.dp))

        TermRow(
            text = "[필수] 개인정보 처리방침",
            checked = privacyAgreed,
            onCheckedChange = onPrivacyAgreeChange,
            onViewClick = { /* TODO: '보기' 클릭 시 웹뷰/다이얼로그 표시 */ }
        )
        Spacer(modifier = Modifier.height(16.dp))

        TermRow(
            text = "[필수] 위치정보 이용약관",
            checked = locationAgreed,
            onCheckedChange = onLocationAgreeChange,
            onViewClick = { /* TODO: '보기' 클릭 시 웹뷰/다이얼로그 표시 */ }
        )

        // --- '가입 완료하기' 버튼 (하단 고정) ---
        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onCompleteClick, // '가입 완료' 이벤트 호출
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BrandOrange),
            shape = RoundedCornerShape(8.dp),
            // (중요) 3개 필수 약관이 모두 동의 됐을 때만 활성화
            enabled = allRequiredAgreed
        ) {
            Text("가입 완료하기", color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp)) // 하단 여백
    }
}


/** 약관 한 줄 Composable (재사용) */
@Composable
private fun TermRow(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    isTitle: Boolean = false,
    onViewClick: (() -> Unit)? = null // '보기' 버튼 (선택 사항)
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = BrandOrange, // 체크됐을 때 색상
                uncheckedColor = DarkGray,
                checkmarkColor = Color.White
            )
        )

        Text(
            text = text,
            style = if (isTitle) MaterialTheme.typography.titleMedium
            else MaterialTheme.typography.bodyLarge,
            fontWeight = if (isTitle) FontWeight.Bold else FontWeight.Normal,
            color = TextBlack
        )

        Spacer(modifier = Modifier.weight(1f)) // '보기' 버튼을 오른쪽 끝으로 밀기

        // onViewClick이 null이 아닐 때만 '보기' 버튼 표시
        if (onViewClick != null) {
            TextButton(onClick = onViewClick) {
                Text(
                    text = "보기",
                    color = DarkGray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}