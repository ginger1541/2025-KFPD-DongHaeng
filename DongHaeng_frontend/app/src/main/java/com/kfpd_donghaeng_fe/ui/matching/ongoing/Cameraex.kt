import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

// 이 컴포저블을 다른 파일에서 사용하려면 UserType을 import 해야 합니다.
// import com.kfpd_donghaeng_fe.viewmodel.auth.UserType


// -----------------------------------------------------------------------------
// 1. CameraRoute (권한 처리 및 ViewModel 연동) - 이전 코드와 동일하게 유지
// -----------------------------------------------------------------------------
@Composable
fun CertificateCameraScreen(
    onBackClick: () -> Unit, // 뒤로가기 액션
    onImageCaptured: (Uri) -> Unit // 이미지 촬영 후 Uri를 전달하는 콜백
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 권한 상태 확인
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    // 권한 요청 런처
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
            if (granted) {
                Log.d("Permission", "카메라 권한 성공적으로 획득.")
            } else {
                Log.e("Permission", "카메라 권한 거부됨. 앱 설정 확인 필요.")
                Toast.makeText(context, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                // 권한이 없으면 뒤로가기 등으로 처리할 수 있음
            }
        }
    )

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            launcher.launch(Manifest.permission.CAMERA)
        }
    }

    if (hasCameraPermission) {
        // 권한이 있을 때 카메라 프리뷰와 UI 오버레이를 표시
        CameraWithOverlayContent(
            onBackClick = onBackClick,
            onImageCaptured = onImageCaptured
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
// 2. CameraWithOverlayContent (카메라 미리보기와 오버레이 UI 결합)
// -----------------------------------------------------------------------------
@Composable
fun CameraWithOverlayContent(
    onBackClick: () -> Unit,
    onImageCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // CameraX ImageCapture 유스케이스
    val imageCapture = remember { ImageCapture.Builder().build() }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    Box(modifier = Modifier.fillMaxSize()) {
        // A. 카메라 미리보기 (CameraX PreviewView)
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    // ScaleType을 FIT_CENTER, FILL_CENTER 등으로 조절하여 화면에 맞게 표시
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA // 후면 카메라 사용

                    try {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture // 캡처 기능 바인딩
                        )
                        Log.d("CameraX", "카메라 유스케이스 바인딩 성공!")
                    } catch (e: Exception) {
                        Log.e("CameraX", "바인딩 실패: ${e.message}", e)
                        Toast.makeText(ctx, "카메라 시작 실패: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        // B. 오버레이 UI
        CameraOverlayUI(
            onBackClick = onBackClick,
            // 촬영 버튼은 이 화면에서 보이지 않으므로 일단 제거합니다.
            // onCaptureClick = { capturePhoto(context, imageCapture, cameraExecutor, onImageCaptured) }
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
// 3. CameraOverlayUI (카메라 미리보기 위에 겹쳐지는 UI 요소들)
// -----------------------------------------------------------------------------
@Composable
fun CameraOverlayUI(
    onBackClick: () -> Unit,
    // onCaptureClick: () -> Unit // 자동 촬영이므로 수동 촬영 버튼은 제거
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 상단 바
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(Color.Black.copy(alpha = 0.5f)) // 반투명 배경
                .align(Alignment.TopCenter),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기", tint = Color.White)
            }
            Spacer(Modifier.weight(1f))
        }

        // 전체 화면을 어둡게 하고, 중앙에 사각형 구멍을 뚫는 오버레이
        IdCardOverlay(
            modifier = Modifier.fillMaxSize()
        )

        // 중앙 안내 텍스트
        Text(
            text = "아래 사각형에 신분증이 꽉 차도록 맞추면\n자동으로 촬영됩니다.",
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .offset(y = (-100).dp) // 가이드라인 위로 올리기
                .padding(horizontal = 32.dp)
        )

        // 하단 유의사항 텍스트
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 48.dp)
        ) {
            val bulletPoint = "•" // 불릿 포인트 문자
            val warningColor = Color.White.copy(alpha = 0.8f) // 텍스트 색상
            val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)

            Text(
                text = buildAnnotatedString {
                    append("$bulletPoint 장애인 등록증은 고객의 실명을 확인하기 위한 용도입니다.\n")
                    append("$bulletPoint 원본 또는 입체적 복사본으로 흘려보고 빛반사에 주의하여 촬영해주세요.\n")
                    append("$bulletPoint ")
                    withStyle(style = boldStyle) {
                        append("장애인 등록증 사진에 빛 반사가 심할 경우")
                    }
                    append(" 촬영된 경우 실명확인을 처음부터\n다시 진행해야 함으로 유의해주세요.")
                },
                color = warningColor,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )
        }
    }
}

// -----------------------------------------------------------------------------
// 4. IdCardOverlay (중앙에 신분증 모양의 투명한 구멍을 뚫는 커스텀 UI)
// -----------------------------------------------------------------------------
@Composable
fun IdCardOverlay(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // 신분증 영역 (전체 화면 대비 비율로 조정)
        val cardWidth = width * 0.7f
        val cardHeight = cardWidth * (3.0f / 4.0f) // 신분증 비율 (대략 4:3)
        val cardTop = (height - cardHeight) / 2
        val cardLeft = (width - cardWidth) / 2

        // 전체 화면을 검은색 반투명으로 칠합니다.
        drawRect(color = Color.Black.copy(alpha = 0.6f))

        // 중앙 신분증 영역을 투명하게 만듭니다 (BlendMode.DstOut 사용)
        drawRect(
            color = Color.Transparent,
            topLeft = Offset(cardLeft, cardTop),
            size = Size(cardWidth, cardHeight),
            blendMode = BlendMode.DstOut // 겹쳐진 부분만 투명하게 만듭니다.
        )

        // 신분증 가이드라인 (흰색 테두리)
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(cardLeft, cardTop),
            size = Size(cardWidth, cardHeight),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(8.dp.toPx()),
            style = Stroke(width = 2.dp.toPx()) // 테두리 두께
        )

        // 코너 부분 가이드라인 (흰색 꺾쇠 모양)
        val cornerLineLength = 20.dp.toPx()
        val cornerLineWidth = 3.dp.toPx()
        val cornerRadiusPx = 8.dp.toPx()

        // 왼쪽 위
        Path().apply {
            moveTo(cardLeft + cornerRadiusPx, cardTop)
            lineTo(cardLeft + cornerLineLength, cardTop)
            moveTo(cardLeft, cardTop + cornerRadiusPx)
            lineTo(cardLeft, cardTop + cornerLineLength)
        }.let { path -> drawPath(path, color = Color.White, style = Stroke(width = cornerLineWidth)) }

        // 오른쪽 위
        Path().apply {
            moveTo(cardLeft + cardWidth - cornerRadiusPx, cardTop)
            lineTo(cardLeft + cardWidth - cornerLineLength, cardTop)
            moveTo(cardLeft + cardWidth, cardTop + cornerRadiusPx)
            lineTo(cardLeft + cardWidth, cardTop + cornerLineLength)
        }.let { path -> drawPath(path, color = Color.White, style = Stroke(width = cornerLineWidth)) }

        // 왼쪽 아래
        Path().apply {
            moveTo(cardLeft + cornerRadiusPx, cardTop + cardHeight)
            lineTo(cardLeft + cornerLineLength, cardTop + cardHeight)
            moveTo(cardLeft, cardTop + cardHeight - cornerRadiusPx)
            lineTo(cardLeft, cardTop + cardHeight - cornerLineLength)
        }.let { path -> drawPath(path, color = Color.White, style = Stroke(width = cornerLineWidth)) }

        // 오른쪽 아래
        Path().apply {
            moveTo(cardLeft + cardWidth - cornerRadiusPx, cardTop + cardHeight)
            lineTo(cardLeft + cardWidth - cornerLineLength, cardTop + cardHeight)
            moveTo(cardLeft + cardWidth, cardTop + cardHeight - cornerRadiusPx)
            lineTo(cardLeft + cardWidth, cardTop + cardHeight - cornerLineLength)
        }.let { path -> drawPath(path, color = Color.White, style = Stroke(width = cornerLineWidth)) }
    }
}


// -----------------------------------------------------------------------------
// 5. 사진 저장 로직 (이전 코드와 동일)
// -----------------------------------------------------------------------------
private fun capturePhoto(
    context: Context,
    imageCapture: ImageCapture,
    executor: ExecutorService,
    onPhotoCaptured: (Uri) -> Unit
) {
    val name = SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US)
        .format(System.currentTimeMillis())
    // 캐시 디렉토리에 저장하여 임시 파일로 사용
    val file = File(context.cacheDir, "IMG_$name.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        outputOptions,
        executor, // 메인 Executor 대신 전달받은 Executor 사용
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                output.savedUri?.let { uri ->
                    Toast.makeText(context, "사진 촬영 완료!", Toast.LENGTH_SHORT).show()
                    Log.d("CameraX", "Photo saved: $uri")
                    onPhotoCaptured(uri) // 촬영된 이미지 Uri를 콜백으로 전달
                } ?: run {
                    Log.e("CameraX", "Photo saved with null URI.")
                    Toast.makeText(context, "사진 저장 실패: URI 없음", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onError(exc: ImageCaptureException) {
                Log.e("CameraX", "사진 촬영 실패: ${exc.message}", exc)
                Toast.makeText(context, "사진 촬영 실패: ${exc.message}", Toast.LENGTH_LONG).show()
            }
        }
    )
}


// -----------------------------------------------------------------------------
// 6. 안드로이드 스튜디오 미리보기용 함수
// -----------------------------------------------------------------------------
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun CertificateCameraScreenPreview() {
    // 미리보기에서는 CameraWithOverlayContent 직접 호출하여 UI만 확인
    // 실제 카메라 바인딩은 미리보기에서 작동하지 않으므로, 이 컴포저블만 확인
    MaterialTheme { // MaterialTheme으로 감싸서 Text 등의 스타일이 적용되도록
        CameraOverlayUI(
            onBackClick = { /* Preview: Do nothing */ }
            // onCaptureClick = { /* Preview: Do nothing */ }
        )
    }
}