package com.kfpd_donghaeng_fe.ui.matching.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import com.kfpd_donghaeng_fe.ui.theme.AppColors
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RequestTimePicker(
    currentDateTime: LocalDateTime,
    onConfirm: (LocalDateTime) -> Unit,
    onCancel: () -> Unit
) {
    val baseNow = remember { LocalDateTime.now() }
    val today = remember { baseNow.toLocalDate() }
    val tomorrow = remember { today.plusDays(1) }

    // 🔥 방법 1: 늦은 시간엔 내일부터 시작
    val minimumHoursRequired = 3 // 최소 3시간 선택폭 필요
    val currentHour = baseNow.hour
    val startDate = if (currentHour >= (24 - minimumHoursRequired)) {
        tomorrow // 21시 이후면 내일부터
    } else {
        today
    }

    val lastDate = remember { startDate.plusDays(7) }
    val dates = remember(startDate) { (0..7).map { startDate.plusDays(it.toLong()) } }

    fun roundUpToNext10(min: Int): Pair<Int, Int> {
        val m = ((min + 9) / 10) * 10
        return if (m >= 60) 1 to 0 else 0 to m
    }

    // 🔥 수정: 시작 날짜 기준으로 초기값 설정
    val initialHourMinute: Pair<Int, Int> = remember(startDate) {
        if (startDate == today) {
            val (carry, mm) = roundUpToNext10(baseNow.minute)
            val hh = (baseNow.hour + carry).coerceAtMost(23)
            hh to mm
        } else {
            9 to 0 // 내일부터는 기본 9:00
        }
    }

    var selectedDate by remember(startDate) {
        mutableStateOf(startDate)
    }
    var selectedHour by remember(startDate) {
        mutableStateOf(initialHourMinute.first)
    }
    var selectedMinute by remember(startDate) {
        mutableStateOf(initialHourMinute.second)
    }

    fun availableHours(date: LocalDate): List<Int> {
        return if (date == today && date == startDate) {
            // 오늘이면서 시작 날짜면 현재 시간 이후만
            val (carry, _) = roundUpToNext10(baseNow.minute)
            val startHour = (baseNow.hour + carry).coerceAtMost(23)
            (startHour..23).toList()
        } else {
            // 내일 이후는 전체 시간
            (0..23).toList()
        }
    }

    fun availableMinutes(date: LocalDate, hour: Int): List<Int> {
        val base = listOf(0, 10, 20, 30, 40, 50)
        if (date != today || date != startDate) return base

        val (carry, next10) = roundUpToNext10(baseNow.minute)
        val firstHour = (baseNow.hour + carry).coerceAtMost(23)

        return when {
            hour < firstHour -> emptyList()
            hour > firstHour -> base
            else -> base.filter { it >= next10 }
        }
    }

    var hours by remember(selectedDate) { mutableStateOf(availableHours(selectedDate)) }
    var minutes by remember(selectedDate, selectedHour) {
        mutableStateOf(availableMinutes(selectedDate, selectedHour))
    }

    LaunchedEffect(selectedDate) {
        val hh = availableHours(selectedDate)
        hours = hh
        if (selectedHour !in hours && hours.isNotEmpty()) {
            selectedHour = hours.first()
        }

        val mm = availableMinutes(selectedDate, selectedHour)
        minutes = if (mm.isEmpty() && hours.isNotEmpty()) {
            selectedHour = hours.first()
            availableMinutes(selectedDate, hours.first())
        } else mm

        if (minutes.isNotEmpty() && selectedMinute !in minutes) {
            selectedMinute = minutes.first()
        }
    }

    LaunchedEffect(selectedHour, selectedDate) {
        val mm = availableMinutes(selectedDate, selectedHour)
        minutes = mm
        if (mm.isNotEmpty() && selectedMinute !in mm) {
            selectedMinute = mm.first()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(AppColors.BackgroundColor)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "예약시간",
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.DarkText,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )

        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(AppColors.LightGray.copy(alpha = 0.6f))
        )

        Spacer(Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
        ) {
            val dateFormatter = remember { DateTimeFormatter.ofPattern("MM.dd E") }
            val datesUi = remember(dates, today, tomorrow) {
                dates.map {
                    when (it) {
                        today -> "오늘"
                        tomorrow -> "내일"
                        else -> it.format(dateFormatter)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimePickerColumn(
                    items = datesUi,
                    initialIndex = dates.indexOf(selectedDate).coerceAtLeast(0),
                    onItemSelected = { idx -> selectedDate = dates[idx] },
                    modifier = Modifier.weight(1f),
                    listKey = "date_${startDate}_${selectedDate}"
                )

                TimePickerColumn(
                    items = hours.map { String.format("%02d", it) },
                    initialIndex = hours.indexOf(selectedHour).coerceAtLeast(0),
                    onItemSelected = { idx ->
                        if (hours.isNotEmpty()) selectedHour = hours[idx]
                    },
                    modifier = Modifier.weight(1f),
                    listKey = "hour_${selectedDate}_${hours.size}"
                )

                Text(
                    ":",
                    fontSize = 24.sp,
                    color = AppColors.AccentOrange,
                    fontWeight = FontWeight.Bold
                )

                TimePickerColumn(
                    items = minutes.map { String.format("%02d", it) },
                    initialIndex = minutes.indexOf(selectedMinute).coerceAtLeast(0),
                    onItemSelected = { idx ->
                        if (minutes.isNotEmpty()) selectedMinute = minutes[idx]
                    },
                    modifier = Modifier.weight(1f),
                    listKey = "min_${selectedDate}_${selectedHour}_${minutes.size}"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AppColors.AccentOrange.copy(alpha = 0.2f))
                    .align(Alignment.Center)
            )

            Text(
                text = "출발 시간",
                fontSize = 12.sp,
                color = AppColors.DarkText,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = (-30).dp)
            )
        }

        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.LightGray),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("취소", color = AppColors.DarkText, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = {
                    val picked = LocalDateTime.of(
                        selectedDate,
                        LocalTime.of(selectedHour, selectedMinute)
                    )
                    onConfirm(picked.truncatedTo(ChronoUnit.MINUTES))
                },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentOrange),
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("확인", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimePickerColumn(
    items: List<String>,
    initialIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    listKey: Any = items.size to (items.firstOrNull() ?: "")
) {
    if (items.isEmpty()) return

    val total = items.size
    val loopCount = 500
    val middle = (total * loopCount) / 2
    val startIndex = middle + initialIndex.coerceIn(0, total - 1)

    val itemHeight = 40.dp
    val visibleItems = 5
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = startIndex)

    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)

    var isInitializing by remember(listKey) { mutableStateOf(true) }

    LaunchedEffect(listKey) {
        isInitializing = true
        listState.scrollToItem(startIndex)
        delay(50)
        isInitializing = false
    }

    LaunchedEffect(listState) {
        snapshotFlow {
            listState.isScrollInProgress to listState.firstVisibleItemIndex
        }
            .distinctUntilChanged()
            .collect { (isScrolling, _) ->
                if (!isScrolling && !isInitializing) {
                    val info = listState.layoutInfo
                    if (info.visibleItemsInfo.isNotEmpty()) {
                        val center = (info.viewportStartOffset + info.viewportEndOffset) / 2
                        val centerItem = info.visibleItemsInfo.minByOrNull {
                            kotlin.math.abs((it.offset + it.size / 2) - center)
                        }
                        centerItem?.let {
                            val actualIndex = ((it.index % total) + total) % total
                            onItemSelected(actualIndex)
                        }
                    }
                }
            }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItems)
            .fillMaxWidth()
    ) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(itemHeight * 2)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            AppColors.BackgroundColor,
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(itemHeight * 2)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            AppColors.BackgroundColor
                        )
                    )
                )
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight * 2)
        ) {
            items(count = total * loopCount, key = { it }) { idx ->
                val actualIndex = ((idx % total) + total) % total
                val itemInfo = listState.layoutInfo.visibleItemsInfo
                    .find { it.index == idx }

                val center = (listState.layoutInfo.viewportStartOffset +
                        listState.layoutInfo.viewportEndOffset) / 2
                val itemCenter = itemInfo?.let { it.offset + it.size / 2 } ?: 0
                val distance = kotlin.math.abs(itemCenter - center).toFloat()
                val maxDistance = itemHeight.value * 2
                val alpha = (1f - (distance / maxDistance)).coerceIn(0.3f, 1f)

                // 🔥 추가: 중앙 아이템 판별
                val isCenterItem = distance < (itemHeight.value * 0.5f) // 중앙에 위치하면

                Text(
                    text = items[actualIndex],
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    color = if (isCenterItem) {
                        AppColors.AccentOrange  // 👈 중앙: 오렌지
                    } else {
                        AppColors.DarkText.copy(alpha = alpha)  // 👈 나머지: 회색 + 투명도
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .wrapContentHeight(Alignment.CenterVertically)
                )
            }
        }
    }
}