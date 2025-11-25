package com.kfpd_donghaeng_fe.viewmodel.matching

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.data.remote.dto.ReviewRequestDto
import com.kfpd_donghaeng_fe.data.repository.MatchRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val matchRepository: MatchRepositoryImpl
) : ViewModel() {

    fun submitReview(
        matchId: Long,
        revieweeId: Long,
        rating: Int,
        comment: String,
        badges: List<String>, // 예: ["친절해요", "시간 잘 지켰어요"]
        onSuccess: () -> Unit
    ) {
        val request = ReviewRequestDto(
            matchId = matchId,
            revieweeId = revieweeId,
            rating = rating,
            comment = comment,
            selectedBadges = badges
        )

        viewModelScope.launch {
            matchRepository.writeReview(request)
                .onSuccess {
                    Log.d("ReviewViewModel", "후기 작성 성공")
                    onSuccess() // 성공 시 홈으로 이동하는 콜백 호출
                }
                .onFailure { e ->
                    Log.e("ReviewViewModel", "후기 작성 실패: ${e.message}")
                    // 에러 처리 (Toast 등)
                }
        }
    }
}