package com.kfpd_donghaeng_fe.domain.usecase

import android.util.Log
import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.repository.OngoingQRRepository
import javax.inject.Inject



//qr 정보 받기
// GetOngoingQRStartInfoUseCase.kt (수정)
class GetOngoingQRStartInfoUseCase @Inject constructor(
    private val repository: OngoingQRRepository // 타입 확인
) {
    suspend operator fun invoke(matchId: Long): Result<QREntity> {
        Log.d("QR_DEBUG", "Use Case 진입: Repository 호출 시도")

        return try {
            val result = repository.getOngoingQRStartInfo(matchId)

            // 💡 이 라인이 찍히지 않더라도...
            Log.d("QR_DEBUG", "Use Case: Repository 반환 완료. ViewModel로 전달.")

            result
        } catch (e: Exception) {
            // 🔴 여기가 핵심! 어떤 예외든 잡아서 출력합니다.
            Log.e("QR_FATAL", "🚨 Use Case에서 반환 값 처리 중 알 수 없는 치명적인 예외 발생:", e)
            Result.failure(e)
        }
    }
}



class GetOngoingQREndInfoUseCase @Inject constructor(
    private val repository: OngoingQRRepository // 타입 확인
) {
    suspend operator fun invoke(matchId: Long): Result<QREntity> {
        Log.d("QR_DEBUG", "Use Case 진입: Repository 호출 시도")

        return try {
            val result = repository.getOngoingQREndInfo(matchId)

            // 💡 이 라인이 찍히지 않더라도...
            Log.d("QR_DEBUG", "Use Case: Repository 반환 완료. ViewModel로 전달.")

            result
        } catch (e: Exception) {
            // 🔴 여기가 핵심! 어떤 예외든 잡아서 출력합니다.
            Log.e("QR_FATAL", "🚨 Use Case에서 반환 값 처리 중 알 수 없는 치명적인 예외 발생:", e)
            Result.failure(e)
        }
    }
}

// qr 스캔 결과 서버 , 응답 받기
class SendQRScanResultUseCase @Inject constructor(
    private val repository: OngoingQRRepository
) {
    // QRScandEntity, QRTypes, matchId를 받아 QRScanResultEntity를 Result로 반환
    suspend operator fun invoke(
        requestEntity: QRScandEntity,
        qrType: QRTypes,
        matchId: Long
    ): Result<QRScanResultEntity> {
        return repository.sendQRScanResult(requestEntity, qrType, matchId)
    }
}