package com.kfpd_donghaeng_fe.viewmodel.matching

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.domain.usecase.GetOngoingQRStartInfoUseCase
import com.kfpd_donghaeng_fe.domain.usecase.SendQRScanResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class QRViewModel @Inject constructor(
    private val getOngoingQRStartInfoUseCase: GetOngoingQRStartInfoUseCase,
    private val sendQRScanResultUseCase: SendQRScanResultUseCase
) : ViewModel() {


    private val _uiState3 = MutableStateFlow(QREntity.Empty)
    val uiState3: StateFlow<QREntity> = _uiState3.asStateFlow()

    // 💡 2. 스캔 시작 장소 상태 추가 (OngoingRoute에서 locateUiState로 사용됨)
    private val _locateUiState = MutableStateFlow(QRScandEntity.Empty) // 👈 QRScandEntity.Empty 정의 필요
    val locateUiState: StateFlow<QRScandEntity> = _locateUiState.asStateFlow()

    // 💡 3. 스캔 결과 상태 추가 (OngoingRoute에서 resultUiState로 사용됨)
    private val _resultUiState = MutableStateFlow(QRScanResultEntity.EmptyState) // 👈 QRScanResultEntity.Empty 정의 필요
    val resultUiState: StateFlow<QRScanResultEntity> = _resultUiState.asStateFlow()

    // ... (데이터를 로드하는 로직)

    fun loadQrInfo(matchId: Long) {
        viewModelScope.launch {
            // UseCase 호출 및 성공 시
            /* val result = getOngoingQRStartInfoUseCase(matchId)
            result.onSuccess { entity ->
                _uiState3.value = entity // 성공한 실제 데이터로 업데이트
            }
            */
        }
    }

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