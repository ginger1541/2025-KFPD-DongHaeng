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
import javax.inject.Inject // jakarta.inject 대신 표준 javax.inject 사용
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update // update 함수 import
import kotlinx.coroutines.launch

@HiltViewModel
class QRViewModel @Inject constructor(
    private val getOngoingQRStartInfoUseCase: GetOngoingQRStartInfoUseCase,
    private val sendQRScanResultUseCase: SendQRScanResultUseCase
) : ViewModel() {


    private val _uiState3 = MutableStateFlow(QREntity.Empty)
    val uiState3: StateFlow<QREntity> = _uiState3.asStateFlow()

    private val _locateUiState = MutableStateFlow(QRScandEntity.Empty)
    val locateUiState: StateFlow<QRScandEntity> = _locateUiState.asStateFlow()
    private val _resultUiState = MutableStateFlow(QRScanResultEntity.EmptyState)
    val resultUiState: StateFlow<QRScanResultEntity> = _resultUiState.asStateFlow()


    fun loadQrInfo(matchId: Long) {
        viewModelScope.launch {
            // UseCase 호출 및 성공 시
            /* val result = getOngoingQRStartInfoUseCase(matchId)
            result.onSuccess { entity ->
                _uiState3.update { entity } // update 함수 사용
            }
            */
        }
    }

    fun loadStartQR(matchId: Long) {
        viewModelScope.launch {
            val result = getOngoingQRStartInfoUseCase(matchId)

            result.onSuccess { qrEntity ->
                val qrUrl = qrEntity.qrImageUrl
                 _uiState3.update { it.copy(qrImageUrl = qrUrl) } // 예시
            }.onFailure { e ->
                // 실패: 오류 메시지 처리
            }
        }
    }

    /**
     * QR 스캔 요청을 서버에 보내고 결과를 받아 상태를 업데이트합니다.
     */
    fun scanQR(requestEntity: QRScandEntity, qrType: QRTypes, matchId: Long) {
        // 스캔 시작 장소 상태 업데이트 (로컬에서 즉시 업데이트)
        _locateUiState.update { requestEntity }
        viewModelScope.launch {
            // 1. UseCase를 통해 서버에 스캔 요청
            val result = sendQRScanResultUseCase(requestEntity, qrType, matchId)

            result.onSuccess { resultEntity ->
                // 2. 성공: 스캔 결과 Entity(_resultUiState) 업데이트
                _resultUiState.update { resultEntity }

                // 3. 페이지 전환 트리거: QR 스캔 성공 플래그를 true로 업데이트
                _uiState3.update { it.copy(qrScanned = true) }
            }.onFailure { e ->
                // 4. 실패: 오류 상태 처리 (예: 스캔 실패 메시지 표시)
                // _resultUiState.update { QRScanResultEntity.Failure(0, "스캔 실패: ${e.message}") }
            }
        }
    }
}