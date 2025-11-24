package com.kfpd_donghaeng_fe.ui.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kfpd_donghaeng_fe.R
import com.kfpd_donghaeng_fe.data.ChatRoomUiModel
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import com.kfpd_donghaeng_fe.ui.theme.BrandOrange
import com.kfpd_donghaeng_fe.viewmodel.chat.ChatViewModel

@Composable
fun ChatListScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    // ViewModel 데이터 관찰
    val chatRooms by viewModel.chatRooms.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 검색창
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            placeholder = { Text("검색", color = Color.Gray) },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = BrandOrange,
                unfocusedBorderColor = Color(0xFFEEEEEE),
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 채팅방 리스트
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp) // 하단 탭바 가림 방지
        ) {
            items(chatRooms) { room ->
                ChatRoomItem(room = room) {
                    // 클릭 시 상세 화면으로 이동
                    navController.navigate("chat_detail/${room.id}")
                }
            }
        }
    }
}

@Composable
fun ChatRoomItem(
    room: ChatRoomUiModel,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 프로필 이미지
        Image(
            painter = painterResource(id = room.partnerProfileRes ?: R.drawable.ic_user),
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0F0F0))
        )

        Spacer(modifier = Modifier.width(16.dp))

        // 이름 및 마지막 메시지
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = room.partnerName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.PrimaryDarkText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = room.lastMessage,
                fontSize = 14.sp,
                color = AppColors.SecondaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 뱃지 및 시간
        Column(horizontalAlignment = Alignment.End) {
            // 시간 표시 (필요 시 추가)
            if (room.timestamp.isNotEmpty()) {
                Text(
                    text = room.timestamp,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // 뱃지
            if (room.unreadCount > 0) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .background(BrandOrange, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = room.unreadCount.toString(),
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}