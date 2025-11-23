package com.kfpd_donghaeng_fe.ui.common

import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.getValue
import android.R
import androidx.compose.ui.graphics.Color

import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.TransformMethod
import com.kakao.vectormap.shape.MapPoints
import com.kakao.vectormap.shape.Polyline
import com.kakao.vectormap.shape.PolylineOptions
import com.kakao.vectormap.shape.PolylineStyle
import com.kfpd_donghaeng_fe.domain.entity.LocationType
import com.kfpd_donghaeng_fe.domain.entity.RouteLocation
import com.kfpd_donghaeng_fe.domain.entity.WalkingRoute


data class MapData(
    val center: LatLng,
    val markers: List<RouteLocation>,
    val route: WalkingRoute? = null
)
import com.kfpd_donghaeng_fe.GlobalApplication

@Composable
fun KakaoMapView(
    modifier: Modifier = Modifier,
    locationX: Double,
    locationY: Double,
    route: WalkingRoute? = null,
    enabled: Boolean = true, // ← 추가
) {
    //  ! 추가 ! ( 45번째 줄 return 까지 )
    if(!enabled_map_emulate){
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF81D4FA)), // 밝은 파란색
        )
        return
    }
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    // 지도 요소 관리 상태
    val labelManager = remember { mutableStateOf<LabelManager?>(null) }
    var currentMarkers by remember { mutableStateOf<List<Label>>(emptyList()) }
    var kakaoMapState by remember { mutableStateOf<KakaoMap?>(null) }
    var currentPolyline by remember { mutableStateOf<Polyline?>(null) }
    var lastRoute by remember { mutableStateOf<WalkingRoute?>(null) }
    var isInitialMoveDone by remember { mutableStateOf(false) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            mapView.apply {
                start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {}
                        override fun onMapError(exception: Exception?) {
                            Log.e("KakaoMap", "Error: ${exception?.message}")
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(map: KakaoMap) {
                            kakaoMapState = map
                            // 지도 로드 직후에는 기본 위치로 한 번만 이동
                            val position = LatLng.from(locationY, locationX)
                            map.moveCamera(CameraUpdateFactory.newCenterPosition(position))
                            map.moveCamera(CameraUpdateFactory.zoomTo(15))
                        }
                    }
                )
            }
        },
        update = { view ->
            if (enabled) {
                kakaoMapState?.let { map ->
                    // 1. 경로가 없을 때 초기 위치 이동 (딱 한 번만 수행하여 사용자 조작 허용)
                    if (route == null && !isInitialMoveDone) {
                        val target = LatLng.from(locationY, locationX)
                        map.moveCamera(CameraUpdateFactory.newCenterPosition(target))
                        isInitialMoveDone = true
                    }

                    // 2. 경로 그리기 (데이터가 변경되었을 때만 실행)
                    if (route != lastRoute) {
                        lastRoute = route // 변경 사항 반영

                        // 기존 경로 삭제
                        currentPolyline?.let {
                            map.shapeManager?.layer?.remove(it)
                            currentPolyline = null
                        }

                        // 새 경로 그리기
                        if (route != null && route.points.isNotEmpty()) {
                            val latLngs = route.points.map { point ->
                                LatLng.from(point.latitude, point.longitude)
                            }

                            val style = PolylineStyle.from(20f, Color.parseColor("#FF8216")) // 두께와 색상
                            val options = PolylineOptions.from(
                                MapPoints.fromLatLng(latLngs),
                                style
                            )

                            // 레이어에 추가
                            currentPolyline = map.shapeManager?.layer?.addPolyline(options)

                            // 🚀 [수정] 카메라를 경로 전체가 보이도록 이동 (List -> Array 변환)
                            // 패딩(100)을 주어 경로가 화면에 꽉 차게 보이도록 설정
                            map.moveCamera(
                                CameraUpdateFactory.fitMapPoints(latLngs.toTypedArray(), 100)
                            )
                        }
                    }
                }
            }
        }
    )
}

@RequiresApi(Build.VERSION_CODES.N)
private fun updateMap(
    map: KakaoMap,
    data: MapData,
    currentPolyline: Polyline?,
    currentMarkers: List<Label>,
    onPolylineUpdated: (Polyline?) -> Unit,
    onMarkersUpdated: (List<Label>) -> Unit
) {
    // 1. ShapeLayer / LabelManager / LabelLayer 가져오기
    val shapeLayer = map.getShapeManager()?.getLayer()
    val labelManager = map.getLabelManager()
    val labelLayer = labelManager?.layer

    if (shapeLayer == null || labelManager == null || labelLayer == null) {
        onPolylineUpdated(null)
        onMarkersUpdated(emptyList())
        return
    }

    // 2. 기존 마커 제거
    currentMarkers.forEach { labelLayer.remove(it) }

    // 3. 새 마커 생성 + TransformMethod 적용
// 3. 새 마커 생성 + TransformMethod 적용
    val newMarkers = data.markers.mapNotNull { loc ->
        val lat = loc.latitude
        val lng = loc.longitude
        if (lat == null || lng == null) return@mapNotNull null

        val position = LatLng.from(lat, lng)

        // 1) 타입별 아이콘 리소스
        val markerStyleRes = when (loc.type) {
            LocationType.START      -> com.kfpd_donghaeng_fe.R.drawable.ic_start
            LocationType.END        -> com.kfpd_donghaeng_fe.R.drawable.ic_destination
            LocationType.REQUESTER  -> com.kfpd_donghaeng_fe.R.drawable.ic_needy
            LocationType.COMPANION  -> com.kfpd_donghaeng_fe.R.drawable.ic_helper
        }

        // 2) LabelStyle 하나만 생성해서 바로 setStyles에 넣기
        val style: LabelStyle = LabelStyle.from(markerStyleRes)

        // 3) 타입별 TransformMethod
        val transform = when (loc.type) {
            LocationType.START,
            LocationType.END -> TransformMethod.Default
            LocationType.REQUESTER,
            LocationType.COMPANION -> TransformMethod.Decal
        }

        // 4) LabelOptions 생성 + 스타일 + Transform 설정
        labelLayer.addLabel(
            LabelOptions.from(loc.id, position)
                .setStyles(style)        // ★ LabelStyle 하나만 넘김 (vararg 오버로드)
                .setTransform(transform)
        )
    }


    onMarkersUpdated(newMarkers)

    // 4. 기존 폴리라인 제거
    currentPolyline?.remove()

    // 5. 새 폴리라인 생성
    val routePoints = data.route?.points.orEmpty()
    if (routePoints.isNotEmpty()) {
        val waypoints = routePoints.map { LatLng.from(it.latitude, it.longitude) }
        val mapPoints = MapPoints.fromLatLng(waypoints)

        // 줌 레벨에 따라 다른 스타일 사용 (원하면 zoomLevel 지정해서 더 세밀하게)
        val styleZoomDefault = PolylineStyle.from(
            6f,           // 두께(px)
            Color.BLUE
        )
        val styleZoomClose = PolylineStyle.from(
            8f,
            Color.BLUE
        ).setZoomLevel(17) // 예: 17 이상에서만 좀 더 두껍게

        val options = PolylineOptions.from(
            mapPoints,
            styleZoomDefault,
            styleZoomClose
        )

        val newPolyline = shapeLayer.addPolyline(options)
        onPolylineUpdated(newPolyline)
    } else {
        onPolylineUpdated(null)
    }

    // TODO: 필요하면 여기서 waypoints + 마커들을 모두 포함하는 bounds 계산해서
    //       map.moveCamera(...) / animateCamera(...) 로 카메라 맞춰주면 됨.
}

