package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import com.kfpd_donghaeng_fe.ui.matching.componentes.LocationInput
import com.kfpd_donghaeng_fe.ui.matching.componentes.LocationType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor() : ViewModel() {

    private val _routeInputs = MutableStateFlow<List<LocationInput>>(
        listOf(
            LocationInput(
                id = "start",
                type = LocationType.START,
                address = "내 위치: 서울 마포구 신촌로 24길 38-4",
                isEditable = false
            ),
            LocationInput(
                id = "end",
                type = LocationType.END,
                address = "도착지 입력",
                isEditable = true
            )
        )
    )
    val routeInputs: StateFlow<List<LocationInput>> = _routeInputs.asStateFlow()

    /**
     * 도착지 위치 업데이트 (Domain Entity 사용)
     */
    fun updateEndLocation(placeInfo: PlaceSearchResult) {
        _routeInputs.value = _routeInputs.value.map { input ->
            if (input.type == LocationType.END) {
                input.copy(
                    address = placeInfo.placeName,
                    placeInfo = placeInfo
                )
            } else {
                input
            }
        }
    }

    /**
     * 경로 초기화
     */
    fun resetRoute() {
        _routeInputs.value = listOf(
            LocationInput(
                id = "start",
                type = LocationType.START,
                address = "내 위치: 서울 마포구 신촌로 24길 38-4",
                isEditable = false
            ),
            LocationInput(
                id = "end",
                type = LocationType.END,
                address = "도착지 입력",
                isEditable = true
            )
        )
    }

    /**
     * 현재 경로 정보 가져오기
     */
    fun getRouteInfo(): RouteInfo {
        val inputs = _routeInputs.value
        return RouteInfo(
            start = inputs.find { it.type == LocationType.START },
            waypoint = inputs.find { it.type == LocationType.WAYPOINT },
            end = inputs.find { it.type == LocationType.END }
        )
    }


    fun setMockEndLocationForTest() {
        val mockPlace = PlaceSearchResult(
            placeName = "Google KOREA",
            addressName = "서울 강남구 역삼동 737",
            roadAddressName = "서울 강남구 역삼로 188",
            categoryName = "회사",
            phone = "02-531-9000",
            x = "127.034785", // 경도 (Longitude)
            y = "37.502842"  // 위도 (Latitude)
        )
        _routeInputs.update { currentList ->
            currentList.map { input ->
                if (input.type == LocationType.END) {
                    input.copy(
                        placeInfo = mockPlace, // placeInfo 채우기
                        address = mockPlace.placeName // 표시되는 텍스트도 업데이트
                    )
                } else {
                    input
                }
            }
        }
    }
}

/**
 * 경로 정보 데이터 클래스
 */
data class RouteInfo(
    val start: LocationInput?,
    val waypoint: LocationInput?,
    val end: LocationInput?
)