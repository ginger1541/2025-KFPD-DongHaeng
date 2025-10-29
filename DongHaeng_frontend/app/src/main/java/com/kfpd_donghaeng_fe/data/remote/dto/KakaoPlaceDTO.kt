package com.kfpd_donghaeng_fe.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult

/**
 * Kakao Local API Response DTO
 */
data class KakaoPlaceResponse(
    @SerializedName("documents") val documents: List<KakaoPlaceDocument>,
    @SerializedName("meta") val meta: KakaoPlaceMeta
)

data class KakaoPlaceDocument(
    @SerializedName("address_name") val addressName: String,
    @SerializedName("category_group_code") val categoryGroupCode: String,
    @SerializedName("category_group_name") val categoryGroupName: String,
    @SerializedName("category_name") val categoryName: String,
    @SerializedName("distance") val distance: String,
    @SerializedName("id") val id: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("place_name") val placeName: String,
    @SerializedName("place_url") val placeUrl: String,
    @SerializedName("road_address_name") val roadAddressName: String,
    @SerializedName("x") val x: String, // longitude
    @SerializedName("y") val y: String  // latitude
)

data class KakaoPlaceMeta(
    @SerializedName("is_end") val isEnd: Boolean,
    @SerializedName("pageable_count") val pageableCount: Int,
    @SerializedName("total_count") val totalCount: Int
)

/**
 * DTO -> Domain Entity 변환
 */
fun KakaoPlaceDocument.toDomain() = PlaceSearchResult(
    placeName = placeName,
    addressName = addressName,
    roadAddressName = roadAddressName,
    categoryName = categoryName,
    phone = phone,
    x = x,
    y = y
)