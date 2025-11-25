package com.kfpd_donghaeng_fe.ui.matching.ongoing

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import androidx.camera.core.ImageAnalysis

/*
// -----------------------------------------------------------------------------
// 1. QrCodeScannerScreen (권한 처리 및 스캐너 시작)
// -----------------------------------------------------------------------------
@Composable
fun QrCodeScannerScreen(
    onBackClick: () -> Unit,
    // QR 코드 인식 결과 문자열을 반환하는 콜백 함수
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current

    // 권한 상태 관리
    var hasCameraPermission by remember {
        mutableStateOf(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
    }

    // 권한 요청 런처
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (!granted) {
                Toast.makeText(context, "QR 스캔을 위해 카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        // 권한이 있을 때 스캐너 콘텐츠 표시
        QrScannerContent(
            onBackClick = onBackClick,
            onQrCodeScanned = onQrCodeScanned
        )
    } else {
        // 권한이 없을 때 메시지 표시
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "카메라 권한을 허용해주세요.", color = Color.White)
        }
    }
}


// -----------------------------------------------------------------------------
// 2. QrScannerContent (카메라 바인딩 및 ImageAnalysis 설정)
// -----------------------------------------------------------------------------
@Composable
fun QrScannerContent(
    onBackClick: () -> Unit,
    onQrCodeScanned: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // QR 코드가 인식되었는지 확인하는 상태 (중복 스캔 방지)
    var isScanning by remember { mutableStateOf(true) }

    // ExecutorService와 ImageAnalysis 유스케이스 정의
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    // 💡 ImageAnalysis 유스케이스 정의
    val imageAnalysis = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(androidx.camera.core.ImageAnalysis.STRATEGY_KEEP_LATEST) // 최신 프레임만 사용
            .build()
            .also {
                // 💡 여기에 실제 QR 코드 분석기(Analyzer)를 연결
                it.setAnalyzer(cameraExecutor, YourQrCodeAnalyzer { rawValue ->
                    // QR 코드가 인식되었을 때 콜백
                    if (isScanning) {
                        isScanning = false // 중복 스캔 방지
                        Log.d("QR_SCANNER", "QR Code Scanned: $rawValue")

                        // 분석기를 비활성화하여 추가 스캔 방지
                        it.clearAnalyzer()

                        // 결과 콜백 호출
                        onQrCodeScanned(rawValue)
                    }
                })
            }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // A. 카메라 미리보기 (CameraX PreviewView)
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        cameraProvider.unbindAll()
                        // 💡 ImageAnalysis 유스케이스를 바인딩하여 실시간 분석 시작
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis
                        )
                    } catch (e: Exception) {
                        Log.e("CameraX", "바인딩 실패: ${e.message}", e)
                        Toast.makeText(ctx, "카메라 시작 실패", Toast.LENGTH_LONG).show()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // B. QR 스캔 오버레이 UI
        QrCodeOverlayUI(
            onBackClick = onBackClick,
            modifier = Modifier.fillMaxSize()
        )
    }

    // 컴포넌트가 사라질 때 ExecutorService 종료
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}


// -----------------------------------------------------------------------------
// 3. QrCodeOverlayUI (오버레이 UI: 뒤로가기 버튼, 스캔 영역)
// -----------------------------------------------------------------------------
@Composable
fun QrCodeOverlayUI(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.Black.copy(alpha = 0.5f))
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기", tint = Color.White)
            }
            Text(
                text = "QR 코드 스캔",
                color = Color.White,
                fontSize = 20.sp,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // 💡 중앙 사각형 구멍을 뚫는 오버레이 (QR 스캔 영역 가이드라인)
        QrScanAreaOverlay(
            modifier = Modifier.fillMaxSize()
        )

        // 중앙 안내 텍스트
        Text(
            text = "QR 코드를 중앙 사각형 안에 맞춰주세요.",
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = 150.dp) // 가이드라인 아래로 이동
                .padding(horizontal = 32.dp)
        )
    }
}

// -----------------------------------------------------------------------------
// 4. QrScanAreaOverlay (중앙에 구멍을 뚫는 캔버스)
// -----------------------------------------------------------------------------
@Composable
fun QrScanAreaOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // QR 스캔 영역 (정사각형)
        val scanSize = width * 0.6f // 화면 너비의 60%
        val scanTop = (height - scanSize) / 2
        val scanLeft = (width - scanSize) / 2

        // 1. 전체 화면을 검은색 반투명으로 칠합니다.
        drawRect(color = Color.Black.copy(alpha = 0.6f))

        // 2. 중앙 스캔 영역을 투명하게 만듭니다.
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(scanLeft, scanTop),
            size = Size(scanSize, scanSize),
            blendMode = BlendMode.DstOut
        )

        // 3. 스캔 영역 가이드라인 (테두리)
        drawRect(
            color = Color.Green, // 스캔 영역 테두리 색상
            topLeft = Offset(scanLeft, scanTop),
            size = Size(scanSize, scanSize),
            style = Stroke(width = 3.dp.toPx())
        )
    }
}


// -----------------------------------------------------------------------------
// 5. ⚠️ QR 코드 디코더 인터페이스 (별도 구현 필요)
// -----------------------------------------------------------------------------

/**
 * ImageAnalysis 유스케이스에 전달될 실제 QR 코드 분석기입니다.
 * 이 클래스 내부에서 Google ML Kit Barcode Scanner 등을 사용하여
 * ImageProxy 객체로부터 QR 코드 데이터를 추출해야 합니다.
 */
class YourQrCodeAnalyzer(private val onQrCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val scope = Executors.newSingleThreadExecutor()

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        // 💡 1. ImageProxy에서 Bitmap 또는 Image로 변환
        // val mediaImage = imageProxy.image

        // 💡 2. ML Kit Barcode Scanner에 전달하여 QR 코드 인식
        // val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        // scanner.process(inputImage)
        //    .addOnSuccessListener { barcodes ->
        //        for (barcode in barcodes) {
        //            val rawValue = barcode.rawValue // QR 코드 데이터
        //            if (rawValue != null) {
        //                onQrCodeScanned(rawValue)
        //                break
        //            }
        //        }
        //    }
        // ... (생략)

        // 중요: 분석이 완료되면 반드시 close() 호출
        imageProxy.close()
    }
}*/