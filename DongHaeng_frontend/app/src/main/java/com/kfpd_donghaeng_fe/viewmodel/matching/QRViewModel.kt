package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.domain.usecase.GetOngoingQRStartInfoUseCase
import com.kfpd_donghaeng_fe.domain.usecase.SendQRScanResultUseCase
import jakarta.inject.Inject
import kotlinx.coroutines.launch


// 예시: ViewModel 코드
class QRViewModel @Inject constructor(
    private val getOngoingQRStartInfoUseCase: GetOngoingQRStartInfoUseCase,
    private val sendQRScanResultUseCase: SendQRScanResultUseCase
) : ViewModel() {

    fun loadStartQR(matchId: Long) {
        viewModelScope.launch {
            val result = getOngoingQRStartInfoUseCase(matchId)

            result.onSuccess { qrEntity ->
                // 성공: QR URL을 추출하여 UI에 표시
                val qrUrl = qrEntity.qrImageUrl
                // ... LiveData 업데이트
            }.onFailure { e ->
                // 실패: 오류 메시지 처리
                // ... LiveData 업데이트
            }
        }
    }

    fun scanQR(requestEntity: QRScandEntity, qrType: QRTypes, matchId: Long) {
        viewModelScope.launch {
            val result = sendQRScanResultUseCase(requestEntity, qrType, matchId)

            result.onSuccess { resultEntity ->
                // 성공: 스캔 결과(성공 메시지, 상태 등)를 처리
                // ...
            }.onFailure { e ->
                // 실패: 오류 처리
                // ...
            }
        }
    }
}