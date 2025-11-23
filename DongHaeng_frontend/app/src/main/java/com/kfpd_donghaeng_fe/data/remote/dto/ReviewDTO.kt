package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.domain.entity.BadgeType
import com.kfpd_donghaeng_fe.domain.entity.ReviewResponseEntity
import org.w3c.dom.Text


//후기 작성
//POST /api/reviews
//1. 후기 작성 요청

data class ReviewRequestDto(
    @SerializedName("match_id") val matchId : Long,
    @SerializedName("reviewee_id") val revieweeId : Long,
    @SerializedName ("rating") val rating : Int, //1~5 조건 있음
    @SerializedName("comment") val comment : Text,
    @SerializedName("selected_badges") val selectedBadges : List<BadgeType>,
    //----------- 응답에만 있는 것 -----------
    //@SerializedName("created_at") val createdAt: String?=null

)

//2. 후기 작성 응답(완료)
data class ReviewResponseDto(
    @SerializedName("match_id") val matchId : Long,
    @SerializedName("reviewee_id") val revieweeId : Long,
    @SerializedName ("rating") val rating : Int, //1~5 조건 있음
    @SerializedName("comment") val comment : Text,
    @SerializedName("selected_badges") val selectedBadges : List<BadgeType>,
    //----------- 응답에만 있는 것 -----------
    @SerializedName("created_at") val createdAt: String

)

fun ReviewResponseDto.toDomain() = ReviewResponseEntity(
    matchId = matchId,
    revieweeId = revieweeId,
    rating = rating, //1~5 조건 있음!
    comment = comment,
    selectedBadges = selectedBadges,
    //----------- 응답에만 있는 것 -----------
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


