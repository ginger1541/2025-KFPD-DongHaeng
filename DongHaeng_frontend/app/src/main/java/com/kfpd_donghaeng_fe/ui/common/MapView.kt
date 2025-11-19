package com.kfpd_donghaeng_fe.ui.common

import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
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

@Composable
fun KakaoMapView(
    modifier: Modifier = Modifier,
    locationX: Double,
    locationY: Double,
    enabled: Boolean = true, // ← 추가
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    var kakaoMap: KakaoMap? by remember { mutableStateOf(null) }

    // 지도 요소 관리 상태
    val labelManager = remember { mutableStateOf<LabelManager?>(null) }
    var currentPolyline by remember { mutableStateOf<Polyline?>(null) }
    var currentMarkers by remember { mutableStateOf<List<Label>>(emptyList()) }

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            if (enabled) {
                mapView.start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {}
                        override fun onMapError(exception: Exception?) {
                            Log.e("KakaoMapDebug", "Map error: ${exception?.message}")
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(map: KakaoMap) {
                            // 1) 좌표들
                            val waypoints = listOf(
                                LatLng.from(37.56369, 126.97558), // 시청역
                                LatLng.from(37.56580, 126.97472), // 덕수궁
                                LatLng.from(37.56531, 126.97695), // 서울광장
                                LatLng.from(37.56629, 126.98223), // 을지로입구
                                LatLng.from(37.56336, 126.98779)  // 명동성당
                            )

                            val mapPoints = MapPoints.fromLatLng(waypoints)

                            val styleZoomClose   = PolylineStyle.from(8F, Color.RED)   // 줌인 시 굵게
                            val styleZoomDefault = PolylineStyle.from(6F, Color.RED)

                            val options = PolylineOptions.from(mapPoints, styleZoomClose, styleZoomDefault)

                            val layer = map.getShapeManager()?.getLayer()
//                            val polyline = layer?.addPolyline(options)

                            val center = LatLng.from(
                                waypoints.map { it.latitude }.average(),
                                waypoints.map { it.longitude }.average()
                            )
                            map.moveCamera(CameraUpdateFactory.newCenterPosition(center))
                            map.moveCamera(CameraUpdateFactory.zoomTo(15)) // zoomTo는 Float 파라미터
                        }



                        override fun getPosition(): LatLng = LatLng.from(locationY, locationX)
                    }
                )
            }
            mapView
        },
        update = {
            if (enabled) {
                val target = LatLng.from(locationY, locationX)
                kakaoMap?.moveCamera(CameraUpdateFactory.newCenterPosition(target))
            }
        },
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

