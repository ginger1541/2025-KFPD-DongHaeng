package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.domain.entity.BadgeType
import com.kfpd_donghaeng_fe.domain.entity.ReviewResponseEntity
import org.w3c.dom.Text


//후기 작성
//POST /api/reviews
//1. 후기 작성 요청

// 1. 후기 작성 요청
data class ReviewRequestDto(
    @SerializedName(value = "matchId", alternate = ["match_id", "MatchId", "MATCH_ID"])
    val matchId: Long,

    @SerializedName(value = "revieweeId", alternate = ["reviewee_id", "RevieweeId", "REVIEWEE_ID"])
    val revieweeId: Long,

    @SerializedName("rating")
    val rating: Int, // 1~5

    @SerializedName("comment")
    val comment: String,

    @SerializedName(value = "selectedBadges", alternate = ["selected_badges", "SelectedBadges", "SELECTED_BADGES"])
    val selectedBadges: List<String>
)

// 2. 후기 작성 응답 (최상위)
data class ReviewResponseDto(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("data") val data: ReviewDataDto?
)

// 3. 응답 데이터 (data 필드 내부)
data class ReviewDataDto(
    @SerializedName(value = "reviewId", alternate = ["review_id", "ReviewId", "REVIEW_ID"])
    val reviewId: Long,

    @SerializedName(value = "matchId", alternate = ["match_id", "MatchId", "MATCH_ID"])
    val matchId: Long,

    @SerializedName(value = "reviewerId", alternate = ["reviewer_id", "ReviewerId", "REVIEWER_ID"])
    val reviewerId: Long,

    @SerializedName(value = "revieweeId", alternate = ["reviewee_id", "RevieweeId", "REVIEWEE_ID"])
    val revieweeId: Long,

    @SerializedName("rating")
    val rating: Int,

    @SerializedName("comment")
    val comment: String,

    @SerializedName(value = "selectedBadges", alternate = ["selected_badges", "SelectedBadges", "SELECTED_BADGES"])
    val selectedBadges: List<String>,

    @SerializedName(value = "createdAt", alternate = ["created_at", "CreatedAt", "CREATED_AT"])
    val createdAt: String
)

fun ReviewDataDto.toDomain() = ReviewResponseEntity(
    reviewId = reviewId,  // 추가됨
    matchId = matchId,
    reviewerId = reviewerId,  // 추가됨
    revieweeId = revieweeId,
    rating = rating,
    comment = comment,
    selectedBadges = selectedBadges,
    createdAt = createdAt
)


//후기 조회 (특정 사용자)
//GET /api/users/{user_id}/reviews


/**
 *  페이지네이션(pagination) 정보 DTO
 */
data class PaginationDto(
    @SerializedName("page") val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total") val total: Int,
    @SerializedName("total_pages") val totalPages: Int
)

/**
 *  '받은 후기 목록'의 "reviews" 리스트 아이템 DTO
 */
data class ReceivedReviewDto(
    @SerializedName("review_id") val reviewId: Long,
    @SerializedName("reviewer") val reviewer: ReviewerDto, // 중첩 객체
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String,
    @SerializedName("selected_badges") val selectedBadges: List<String>,
    @SerializedName("created_at") val createdAt: String
)
data class ReviewerDto(
    @SerializedName("user_id") val userId: Long,
    @SerializedName("name") val name: String,
    @SerializedName("profile_image_url") val profileImageUrl: String
)

/**
 *  '받은 후기 목록'의 "data" 객체 DTO
 */
data class ReceivedReviewsDataDto(
    @SerializedName("average_rating") val averageRating: Double,
    @SerializedName("total_reviews") val totalReviews: Int,
    @SerializedName("reviews") val reviews: List<ReceivedReviewDto>
)


