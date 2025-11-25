package com.kfpd_donghaeng_fe.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.annotation.DrawableRes
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
import androidx.core.content.ContextCompat
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
import com.kfpd_donghaeng_fe.R
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalLifecycleOwner

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
    route: WalkingRoute? = null,
    enabled: Boolean = true, // ← 추가
    markers: List<RouteLocation> = emptyList()
) {
    //  ! 추가 ! ( 45번째 줄 return 까지 )
//    if(!enabled_map_emulate){
//        Box(
//            modifier = modifier
//                .fillMaxSize()
//                .background(Color(0xFF81D4FA)), // 밝은 파란색
//        )
//        return
//    }

    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    Log.d("KakaoMap", "Lifecycle: ON_RESUME")
                    try { mapView.resume() } catch (e: Exception) { e.printStackTrace() }
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d("KakaoMap", "Lifecycle: ON_PAUSE")
                    try { mapView.pause() } catch (e: Exception) { e.printStackTrace() }
                }
                Lifecycle.Event.ON_DESTROY -> {
                    // onMapDestroy 콜백에서 처리되기도 하지만 안전장치
                    // mapView.finish() // 필요시 호출
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // 화면이 완전히 끝날 땐 finish()가 호출되어야 함
        }
    }

    val markerBitmaps = remember {
        mapOf(
            LocationType.START to context.vectorToBitmap(R.drawable.ic_start_dot, 80, 80),
            LocationType.END to context.vectorToBitmap(R.drawable.ic_end_dot, 80, 80),
            LocationType.REQUESTER to context.vectorToBitmap(R.drawable.ic_requester_pin, 100, 120),
            LocationType.COMPANION to context.vectorToBitmap(R.drawable.ic_companion_pin, 100, 120),
            LocationType.PLACE to context.vectorToBitmap(R.drawable.ic_pin_orange, 70, 85),
            LocationType.TARGET to context.vectorToBitmap(R.drawable.ic_target_location,)
        )
    }

    val routeBitmaps = remember {
        mapOf(
            "start" to context.vectorToBitmap(R.drawable.ic_start, 100, 120),
            "end" to context.vectorToBitmap(R.drawable.ic_destination, 100, 120)
        )
    }

    // 지도 요소 관리 상태
    val labelManager = remember { mutableStateOf<LabelManager?>(null) }
    var currentMarkers by remember { mutableStateOf<List<Label>>(emptyList()) }
    var lastMarkersInput by remember { mutableStateOf<List<RouteLocation>>(emptyList()) }

    var currentLabels by remember { mutableStateOf<List<Label>>(emptyList()) }
    var kakaoMapState by remember { mutableStateOf<KakaoMap?>(null) }
    var currentPolyline by remember { mutableStateOf<Polyline?>(null) }
    var lastRoute by remember { mutableStateOf<WalkingRoute?>(null) }

    var isInitialMoveDone by remember { mutableStateOf(false) }
    var lastMovedLocation by remember { mutableStateOf<LatLng?>(null) }
    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = {
            mapView.apply {
                start(
                    object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {
                            Log.d("KakaoMap", "Map destroyed")
                        }
                        override fun onMapError(exception: Exception?) {
                            Log.e("KakaoMap", "Error: ${exception?.message}")
                        }
                    },
                    object : KakaoMapReadyCallback() {
                        override fun onMapReady(map: KakaoMap) {
                            kakaoMapState = map
                            Log.d("KakaoMap", "Map ready")
                            val position = LatLng.from(locationY, locationX)
                            map.moveCamera(CameraUpdateFactory.newCenterPosition(position))
                            map.moveCamera(CameraUpdateFactory.zoomTo(15))

                            if (!isInitialMoveDone) {
                                val position = LatLng.from(locationY, locationX)
                                map.moveCamera(CameraUpdateFactory.newCenterPosition(position))
                                isInitialMoveDone = true
                            }
                        }
                    }
                )
            }
        },
        update = { view ->
            if (enabled) {
                kakaoMapState?.let { map ->
                    // 1. 초기 위치 이동
                    if (route == null) {
                        val currentTarget = LatLng.from(locationY, locationX)
                        if (!isInitialMoveDone || lastMovedLocation != currentTarget) {
                            map.moveCamera(CameraUpdateFactory.newCenterPosition(currentTarget))
                            isInitialMoveDone = true
                            lastMovedLocation = currentTarget
                        }
                    }

                    // 2. 경로 및 라벨 업데이트
                    if (route != lastRoute) {
                        lastRoute = route

                        currentPolyline?.let { map.shapeManager?.layer?.remove(it) }
                        currentPolyline = null

                        val labelLayer = map.labelManager?.layer
                        currentLabels.forEach { labelLayer?.remove(it) }
                        currentLabels = emptyList()

                        if (route != null && route.points.isNotEmpty()) {
                            val latLngs = route.points.map { point ->
                                LatLng.from(point.latitude, point.longitude)
                            }

                            val lineStyle = PolylineStyle.from(20f, Color.parseColor("#FF8216"))
                            val lineOptions = PolylineOptions.from(
                                MapPoints.fromLatLng(latLngs),
                                lineStyle
                            )
                            currentPolyline = map.shapeManager?.layer?.addPolyline(lineOptions)

                            val newLabels = mutableListOf<Label>()

                            // 출발지
                            val startPos = latLngs.first()
                            routeBitmaps["start"]?.let { startBitmap ->
                                val startStyle = LabelStyle.from(startBitmap)
                                    .setAnchorPoint(0.5f, 1.0f)
                                val startOptions = LabelOptions.from("start", startPos)
                                    .setStyles(startStyle)
                                labelLayer?.addLabel(startOptions)?.let {
                                    newLabels.add(it)
                                    Log.d("KakaoMap", "경로 시작점 추가 성공")
                                }
                            }

                            // 도착지
                            val endPos = latLngs.last()
                            routeBitmaps["end"]?.let { endBitmap ->
                                val endStyle = LabelStyle.from(endBitmap)
                                    .setAnchorPoint(0.5f, 1.0f)
                                val endOptions = LabelOptions.from("end", endPos)
                                    .setStyles(endStyle)
                                labelLayer?.addLabel(endOptions)?.let {
                                    newLabels.add(it)
                                    Log.d("KakaoMap", "경로 도착점 추가 성공")
                                }
                            }

                            currentLabels = newLabels
                            map.moveCamera(
                                CameraUpdateFactory.fitMapPoints(latLngs.toTypedArray(), 100)
                            )

                            Log.d("KakaoMap", "경로 업데이트 완료: ${newLabels.size}개 라벨")
                        }
                    }

                    // 3. 동적 마커 업데이트
                    if (markers != lastMarkersInput) {
                        Log.d("KakaoMap", "마커 업데이트 시작 - 마커 개수: ${markers.size}")
                        lastMarkersInput = markers
                        val labelLayer = map.labelManager?.layer

                        if (labelLayer == null) {
                            Log.e("KakaoMap", "labelLayer가 null입니다!")
                        } else {
                            currentMarkers.forEach { labelLayer.remove(it) }

                            val newLabels = mutableListOf<Label>()

                            markers.forEach { loc ->
                                Log.d("KakaoMap", "마커 처리 중: ${loc.id}, lat=${loc.latitude}, lng=${loc.longitude}, type=${loc.type}")

                                loc.latitude?.let { lat ->
                                    loc.longitude?.let { lng ->
                                        val position = LatLng.from(lat, lng)

                                        // ✅ markerBitmaps에서 타입에 맞는 비트맵 가져오기 (PLACE 포함)
                                        val markerBitmap = markerBitmaps[loc.type]

                                        if (markerBitmap != null) {
                                            val style = LabelStyle.from(markerBitmap)
                                                .setAnchorPoint(0.5f, 1.0f)

                                            val options = LabelOptions.from(loc.id, position)
                                                .setStyles(style)

                                            val label = labelLayer.addLabel(options)
                                            if (label != null) {
                                                newLabels.add(label)
                                                Log.d("KakaoMap", "라벨 추가 성공: ${loc.id} (${loc.type})")
                                            } else {
                                                Log.e("KakaoMap", "라벨 추가 실패: ${loc.id}")
                                            }
                                        } else {
                                            Log.e("KakaoMap", "Bitmap이 null입니다: ${loc.type}")
                                        }
                                    }
                                }
                            }

                            currentMarkers = newLabels
                            Log.d("KakaoMap", "최종 추가된 라벨 개수: ${newLabels.size}")

                            if (newLabels.isNotEmpty() && route == null) {
                                val positions = markers.mapNotNull { loc ->
                                    loc.latitude?.let { lat ->
                                        loc.longitude?.let { lng ->
                                            LatLng.from(lat, lng)
                                        }
                                    }
                                }

                                if (positions.isNotEmpty()) {
                                    if (positions.size == 1) {
                                        map.moveCamera(CameraUpdateFactory.newCenterPosition(positions[0]))
                                        map.moveCamera(CameraUpdateFactory.zoomTo(16))
                                        Log.d("KakaoMap", "단일 마커 위치로 카메라 이동")
                                    } else {
                                        map.moveCamera(
                                            CameraUpdateFactory.fitMapPoints(positions.toTypedArray(), 200)
                                        )
                                        Log.d("KakaoMap", "여러 마커를 포함하도록 카메라 조정")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    )
}

//@RequiresApi(Build.VERSION_CODES.N)
//private fun updateMap(
//    map: KakaoMap,
//    data: MapData,
//    currentPolyline: Polyline?,
//    currentMarkers: List<Label>,
//    onPolylineUpdated: (Polyline?) -> Unit,
//    onMarkersUpdated: (List<Label>) -> Unit
//) {
//    // 1. ShapeLayer / LabelManager / LabelLayer 가져오기
//    val shapeLayer = map.getShapeManager()?.getLayer()
//    val labelManager = map.getLabelManager()
//    val labelLayer = labelManager?.layer
//
//    if (shapeLayer == null || labelManager == null || labelLayer == null) {
//        onPolylineUpdated(null)
//        onMarkersUpdated(emptyList())
//        return
//    }
//
//    // 2. 기존 마커 제거
//    currentMarkers.forEach { labelLayer.remove(it) }
//
//    // 3. 새 마커 생성 + TransformMethod 적용
//    val newMarkers = data.markers.mapNotNull { loc ->
//        val lat = loc.latitude
//        val lng = loc.longitude
//        if (lat == null || lng == null) return@mapNotNull null
//
//        val position = LatLng.from(lat, lng)
//
//        // 1) 타입별 아이콘 리소스
//        val markerStyleRes = when (loc.type) {
//            LocationType.START      -> com.kfpd_donghaeng_fe.R.drawable.ic_start
//            LocationType.END        -> com.kfpd_donghaeng_fe.R.drawable.ic_destination
//            LocationType.REQUESTER  -> com.kfpd_donghaeng_fe.R.drawable.ic_needy
//            LocationType.COMPANION  -> com.kfpd_donghaeng_fe.R.drawable.ic_helper
//        }
//
//        // 2) LabelStyle 하나만 생성해서 바로 setStyles에 넣기
//        val style: LabelStyle = LabelStyle.from(markerStyleRes)
//
//        // 3) 타입별 TransformMethod
//        val transform = when (loc.type) {
//            LocationType.START,
//            LocationType.END -> TransformMethod.Default
//            LocationType.REQUESTER,
//            LocationType.COMPANION -> TransformMethod.Decal
//        }
//
//        // 4) LabelOptions 생성 + 스타일 + Transform 설정
//        labelLayer.addLabel(
//            LabelOptions.from(loc.id, position)
//                .setStyles(style)        // ★ LabelStyle 하나만 넘김 (vararg 오버로드)
//                .setTransform(transform)
//        )
//    }
//
//
//    onMarkersUpdated(newMarkers)
//
//    // 4. 기존 폴리라인 제거
//    currentPolyline?.remove()
//
//    // 5. 새 폴리라인 생성
//    val routePoints = data.route?.points.orEmpty()
//    if (routePoints.isNotEmpty()) {
//        val waypoints = routePoints.map { LatLng.from(it.latitude, it.longitude) }
//        val mapPoints = MapPoints.fromLatLng(waypoints)
//
//        // 줌 레벨에 따라 다른 스타일 사용 (원하면 zoomLevel 지정해서 더 세밀하게)
//        val styleZoomDefault = PolylineStyle.from(
//            6f,           // 두께(px)
//            Color.BLUE
//        )
//        val styleZoomClose = PolylineStyle.from(
//            8f,
//            Color.BLUE
//        ).setZoomLevel(17) // 예: 17 이상에서만 좀 더 두껍게
//
//        val options = PolylineOptions.from(
//            mapPoints,
//            styleZoomDefault,
//            styleZoomClose
//        )
//
//        val newPolyline = shapeLayer.addPolyline(options)
//        onPolylineUpdated(newPolyline)
//    } else {
//        onPolylineUpdated(null)
//    }
//
//    // TODO: 필요하면 여기서 waypoints + 마커들을 모두 포함하는 bounds 계산해서
//    //       map.moveCamera(...) / animateCamera(...) 로 카메라 맞춰주면 됨.
//}

fun Context.vectorToBitmap(
    @DrawableRes drawableResId: Int,
    width: Int = 100,  // 원하는 크기(px)
    height: Int = 100
): Bitmap {
    val drawable = ContextCompat.getDrawable(this, drawableResId)
        ?: throw IllegalArgumentException("Drawable not found")

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)

    return bitmap
}