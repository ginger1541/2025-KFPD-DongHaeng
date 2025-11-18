package com.kfpd_donghaeng_fe.ui.common

import android.util.Log
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
import androidx.compose.ui.graphics.Color

import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyles
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.shape.MapPoints
import com.kakao.vectormap.shape.PolylineOptions
import com.kakao.vectormap.shape.PolylineStyle
import com.kfpd_donghaeng_fe.GlobalApplication

@Composable
fun KakaoMapView(
    modifier: Modifier = Modifier,
    locationX: Double,
    locationY: Double,
    enabled: Boolean = true, // ← 추가
    enabled_map_emulate:Boolean = GlobalApplication.isMapLoaded
) {
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
    var kakaoMap: KakaoMap? by remember { mutableStateOf(null) }

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

                            val styleZoomClose   = PolylineStyle.from(8F, android.graphics.Color.RED)   // 줌인 시 굵게
                            val styleZoomDefault = PolylineStyle.from(6F, android.graphics.Color.RED)

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
