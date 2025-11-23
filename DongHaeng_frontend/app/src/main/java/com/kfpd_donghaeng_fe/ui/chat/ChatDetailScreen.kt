package com.kfpd_donghaeng_fe.ui.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kfpd_donghaeng_fe.data.MatchingInfoUiModel
import com.kfpd_donghaeng_fe.data.MessageType
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.ui.theme.BrandOrange
import com.kfpd_donghaeng_fe.viewmodel.chat.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatDetailScreen(
    chatRoomId: Long,
    onBackClick: () -> Unit,
    viewModel: ChatViewModel = hiltViewModel()
) {
    // 1. 현재 방의 메시지 리스트 가져오기
    val allMessages by viewModel.currentMessages.collectAsState()

    // 2. 채팅방 이름 가져오기
    val chatRooms by viewModel.chatRooms.collectAsState()
    val roomInfo = chatRooms.find { it.id == chatRoomId }
    val partnerName = roomInfo?.partnerName ?: "알 수 없음"

    // 3. 방 입장 처리 (API 호출 및 소켓 연결)
    LaunchedEffect(key1 = chatRoomId) {
        viewModel.enterChatRoom(chatRoomId)
    }

    // 4. 스크롤 상태 (메시지 추가 시 자동 스크롤)
    val listState = rememberLazyListState()
    LaunchedEffect(allMessages.size) {
        if (allMessages.isNotEmpty()) {
            listState.animateScrollToItem(allMessages.size - 1)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(partnerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("동행 진행 중", fontSize = 12.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            ChatInputArea(
                onSendMessage = { text ->
                    viewModel.sendMessage(chatRoomId, text)
                }
            )
        }
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(allMessages) { message ->
                Spacer(modifier = Modifier.height(16.dp))
                when (message.type) {
                    MessageType.MATCHING_CARD -> message.matchingInfo?.let { MatchingCard(it) }
                    MessageType.SYSTEM -> message.text?.let { SystemMessage(it) }
                    MessageType.TALK_OTHER -> message.text?.let { OtherMessageBubble(it) }
                    MessageType.TALK_ME -> message.text?.let { MyMessageBubble(it) }
                }
            }
        }
    }
}

// --- 하위 컴포넌트들 ---

@Composable
fun MatchingCard(info: MatchingInfoUiModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
        elevation = CardDefaults.cardElevation(0.dp),
        border = BorderStroke(1.dp, Color(0xFFE0E0E0))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(info.date, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(12.dp))

            // 출발지
            Row(verticalAlignment = Alignment.Top) {
                TimelineDot(isStart = true)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(info.startPlace, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(info.startTime, fontSize = 12.sp, color = Color.Gray)
                }
            }

            // 연결선
            Box(
                modifier = Modifier
                    .padding(start = 5.dp)
                    .height(24.dp)
                    .width(2.dp)
                    .background(Color(0xFFE0E0E0))
            )

            // 도착지
            Row(verticalAlignment = Alignment.Top) {
                TimelineDot(isStart = false)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(info.endPlace, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text(info.endTime, fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun TimelineDot(isStart: Boolean) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(Color.White, CircleShape)
            .padding(2.dp)
            .background(BrandOrange, CircleShape)
    ) {
        if (isStart) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(4.dp)
                    .background(Color.White, CircleShape)
            )
        }
    }
}

@Composable
fun SystemMessage(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Text(text, fontSize = 14.sp, color = AppColors.PrimaryDarkText, lineHeight = 20.sp)
    }
}

@Composable
fun OtherMessageBubble(text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Column(
            modifier = Modifier
                .background(Color(0xFFF0F0F0), RoundedCornerShape(0.dp, 16.dp, 16.dp, 16.dp))
                .padding(12.dp)
                .widthIn(max = 260.dp)
        ) {
            Text(text, fontSize = 15.sp, color = Color.Black)
        }
    }
}

@Composable
fun MyMessageBubble(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        Column(
            modifier = Modifier
                .background(BrandOrange, RoundedCornerShape(16.dp, 0.dp, 16.dp, 16.dp))
                .padding(12.dp)
                .widthIn(max = 260.dp)
        ) {
            Text(text, fontSize = 15.sp, color = Color.White)
        }
    }
}

@Composable
fun ChatInputArea(onSendMessage: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .imePadding(), // 키보드 올라올 때 패딩 처리
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {}) {
            Icon(Icons.Default.Add, contentDescription = "더보기", tint = BrandOrange)
        }

        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .height(50.dp),
            placeholder = { Text("메세지를 입력하세요", fontSize = 14.sp) },
            shape = RoundedCornerShape(25.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color(0xFFF0F0F0),
                unfocusedContainerColor = Color(0xFFF0F0F0)
            ),
            trailingIcon = {
                if (text.isNotEmpty()) {
                    IconButton(onClick = {
                        onSendMessage(text)
                        text = ""
                    }) {
                        Icon(Icons.Default.Send, contentDescription = "전송", tint = BrandOrange)
                    }
                }
            }
        )
    }
}