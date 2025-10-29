package com.kfpd_donghaeng_fe.ui.matching.ongoing



import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController // 💡 키보드 제어
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatKeyboard(
    onMessageSent: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textState by remember { mutableStateOf("") }

    // 1. 키보드 컨트롤러와 포커스 요청자 준비
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }


    DisposableEffect(Unit) {
        // 포커스를 요청 + 키보드
        focusRequester.requestFocus()
        keyboardController?.show()

        onDispose {
            // 이 컴포저블이 화면에서 사라질 때 키보드를 숨깁니다.
            keyboardController?.hide()
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(androidx.compose.ui.graphics.Color.White)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        // 텍스트 입력 필드
        OutlinedTextField(
            value = textState,
            onValueChange = { textState = it },
            label = { Text("메시지를 입력하세요...") },
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            // 3. FocusRequester를 TextField에 연결합니다.
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
        )

        // 전송 버튼
        IconButton(
            onClick = {
                if (textState.isNotBlank()) {
                    onMessageSent(textState)
                    textState = ""
                }
            },
            enabled = textState.isNotBlank(),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Send,
                contentDescription = "SendIcon"
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun ChattingScreen() {
    Box(modifier = Modifier.fillMaxSize()) {

        // 2️⃣ 상단 시트 (유저 프로필)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .zIndex(1f) // 상단 시트를 위로
                .padding(0.dp)

        ) {
            TopSheet(0)

        }
        ChatKeyboard(
            onMessageSent = { message ->
                println("메시지 전송: $message")
                // TODO: 메시지 전송 로직 (ViewModel 호출) 구현
            },
            modifier = Modifier
                .align(Alignment.BottomCenter) // 하단 중앙에 정렬
                .zIndex(1f) // TopSheet와 동일하게 위에 배치 (필요하다면)
        )

    }
}