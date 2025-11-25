package com.kfpd_donghaeng_fe.ui.matching.ongoing


import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import android.util.Log

/**
 * CameraX ImageAnalysis를 위한 ML Kit 기반의 QR 코드 분석기입니다.
 * QR 코드를 발견하면 콜백을 통해 데이터를 전달합니다.
 *
 * @param onQrCodeScanned QR 코드가 인식되었을 때 호출될 함수 (QR 코드 문자열을 반환)
 */
class QrCodeAnalyzer(
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // QR 코드만 스캔하도록 옵션을 설정합니다.
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
        .build()

    // ML Kit Barcode Scanner 인스턴스를 생성합니다.
    private val scanner = BarcodeScanning.getClient(options)

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        // 이미 프레임이 처리 중이면 다음 프레임은 무시합니다. (동시성 제어)
        val mediaImage = imageProxy.image
        if (mediaImage == null) {
            imageProxy.close()
            return
        }

        // CameraX ImageProxy를 ML Kit의 InputImage로 변환합니다.
        val inputImage = InputImage.fromMediaImage(
            mediaImage,
            imageProxy.imageInfo.rotationDegrees
        )

        // 스캐너를 실행합니다.
        scanner.process(inputImage)
            .addOnSuccessListener { barcodes ->
                barcodes.forEach { barcode ->
                    // QR 코드가 인식되었고, 값이 null이 아닌 경우
                    if (barcode.valueType == Barcode.TYPE_TEXT && barcode.rawValue != null) {
                        Log.d("QR_Analyzer", "QR 코드 인식 성공: ${barcode.rawValue}")

                        // 1. 콜백 함수 호출: QR 문자열을 View/ViewModel로 전달합니다.
                        onQrCodeScanned(barcode.rawValue!!)

                        // 2. 중요: 인식 성공 후 더 이상 분석할 필요가 없으므로 스캐너를 닫거나 플래그를 설정하여 중복 실행을 막아야 합니다.
                        // (이 로직은 QrScannerScreen에서 onQrCodeScanned 호출 후 CameraProvider.unbindAll()로 처리하는 것이 안전합니다.)

                        // 하나의 코드를 인식하면 바로 리턴하여 중복 콜백을 방지합니다.
                        return@addOnSuccessListener
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e("QR_Analyzer", "바코드 스캔 실패", e)
            }
            .addOnCompleteListener {
                // 프레임 처리가 완료되면 반드시 ImageProxy를 닫아 다음 프레임 처리를 허용합니다.
                imageProxy.close()
            }
    }
}