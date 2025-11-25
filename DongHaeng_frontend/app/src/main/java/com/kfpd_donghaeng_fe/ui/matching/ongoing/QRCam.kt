package com.kfpd_donghaeng_fe.ui.matching.ongoing

import android.content.Context
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// 이 함수는 실제 QR 코드 분석 로직을 담당해야 합니다.
// 여기서는 CameraX 설정의 틀만 제공하며, 실제 분석기는 별도로 구현해야 합니다.
// (예: ML Kit Barcode Scanner를 사용하는 QrCodeAnalyzer 클래스)

/**
 * QR 코드 스캔을 위한 전체 화면 컴포저블입니다.
 *
 * @param onQrCodeScanned QR 코드가 성공적으로 스캔되었을 때 호출될 콜백.
 * @param onStopScanning 스캔 화면을 닫거나 종료할 때 호출될 콜백.
 */
@Composable
fun QrScannerScreen(
    onQrCodeScanned: (String) -> Unit,
    onStopScanning: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // CameraProvider를 비동기로 가져옵니다.
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }

    // 분석을 위한 Executor
    val cameraExecutor = remember {
        Executors.newSingleThreadExecutor()
    }

    // Android View를 Compose에 통합
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            PreviewView(ctx).apply {
                // 카메라 초기화 로직
                setupCamera(
                    ctx,
                    this,
                    cameraProviderFuture,
                    cameraExecutor,
                    onQrCodeScanned,
                    onStopScanning
                )
            }
        }
    )

    // 화면이 Compose에서 제거될 때 Executor를 종료합니다.
    // DisposableEffect(Unit) {
    //     onDispose {
    //         cameraExecutor.shutdown()
    //     }
    // }
}

/**
 * CameraX를 초기화하고 Preview 및 ImageAnalysis UseCase를 바인딩하는 함수.
 */
private fun setupCamera(
    context: Context,
    previewView: PreviewView,
    cameraProviderFuture: ListenableFuture<ProcessCameraProvider>,
    cameraExecutor: ExecutorService,
    onQrCodeScanned: (String) -> Unit,
    onStopScanning: () -> Unit
) {
    cameraProviderFuture.addListener({
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        val preview = Preview.Builder().build().also {
            it.setSurfaceProvider(previewView.surfaceProvider)
        }

        // 이미지 분석 UseCase 설정
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also {
                // 💡 QrCodeAnalyzer를 연결합니다.
                it.setAnalyzer(
                    cameraExecutor,
                    QrCodeAnalyzer { result ->
                        // QR 코드가 인식되면 콜백을 호출합니다.
                        onQrCodeScanned(result)

                        // ⚠️ QR 코드를 인식했으므로, 더 이상 분석할 필요가 없습니다.
                        // 카메라를 즉시 닫아 자원을 해제합니다.
                        cameraProviderFuture.get().unbindAll()
                        onStopScanning()
                    }
                )
            }

        // 후면 카메라 선택
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // 기존 바인딩 해제 후 UseCase 바인딩
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as androidx.lifecycle.LifecycleOwner, // Context가 LifecycleOwner여야 함
                cameraSelector,
                preview,
                imageAnalyzer
            )
        } catch (exc: Exception) {
            // 바인딩 실패 처리 (예: 권한 없음)
            onStopScanning()
        }

    }, ContextCompat.getMainExecutor(context))
}