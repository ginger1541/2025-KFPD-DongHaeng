package com.kfpd_donghaeng_fe.viewmodel.matching

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kfpd_donghaeng_fe.domain.entity.matching.QREntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScanResultEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScandEntity
import com.kfpd_donghaeng_fe.domain.entity.matching.QRTypes
import com.kfpd_donghaeng_fe.domain.entity.matching.QRScreenUiState
import com.kfpd_donghaeng_fe.domain.usecase.GetOngoingQREndInfoUseCase
import com.kfpd_donghaeng_fe.domain.usecase.GetOngoingQRStartInfoUseCase
//import com.kfpd_donghaeng_fe.domain.usecase.GetOngoingQREndInfoUseCase // 💡 End Info UseCase가 있다고 가정하고 추가
import com.kfpd_donghaeng_fe.domain.usecase.SendQRScanResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class QRViewModel @Inject constructor(
    private val getOngoingQRStartInfoUseCase: GetOngoingQRStartInfoUseCase,
    private val getOngoingQREndInfoUseCase: GetOngoingQREndInfoUseCase, // 💡 End UseCase 인젝션 추가
    private val sendQRScanResultUseCase: SendQRScanResultUseCase
) : ViewModel() {

    private val _qrScanRequestEvent = MutableSharedFlow<Pair<Long, QRTypes>>()
    val qrScanRequestEvent: SharedFlow<Pair<Long, QRTypes>> = _qrScanRequestEvent.asSharedFlow()

    private val _uiState = MutableStateFlow(QRScreenUiState(isLoading = true))
    val uiState: StateFlow<QRScreenUiState> = _uiState.asStateFlow()

    private val _locateUiState = MutableStateFlow(QRScandEntity.Empty)
    val locateUiState: StateFlow<QRScandEntity> = _locateUiState.asStateFlow()
    private val _resultUiState = MutableStateFlow(QRScanResultEntity.EmptyState)
    val resultUiState: StateFlow<QRScanResultEntity> = _resultUiState.asStateFlow()

    fun requestQrScan(matchId: Long, qrType: QRTypes) {
        viewModelScope.launch {
            _qrScanRequestEvent.emit(Pair(matchId, qrType))
        }
    }

    // ----------------------------------------------------
    // 💡 수정된 부분: executeLoadQrInfo 제거 및 함수 분리
    // ----------------------------------------------------

    fun loadStartQRInfo(matchId: Long, _qrType: QRTypes) {
        viewModelScope.launch {
            Log.d("QR_DEBUG", "START 로딩 시작: matchId=$matchId") // 💡 로딩 시작 로그
            _uiState.update { it.copy(isLoading = true, isError = false) }

            getOngoingQRStartInfoUseCase(matchId)
                .onSuccess { qrEntity ->
                    // 💡 성공 로그: 로딩 끝
                    Log.d("QR_DEBUG", "START 로딩 성공, isLoading=false로 설정")
                    _uiState.update { it.copy(qrEntity = qrEntity, isLoading = false) }
                }
                .onFailure { e ->
                    // 💡 실패 로그: 로딩 끝
                    Log.e("QR_DEBUG", "START 로딩 실패", e)
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }
        }
    }

    fun loadEndQRInfo(matchId: Long, _qrType: QRTypes) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isError = false) }
            getOngoingQREndInfoUseCase(matchId) // 💡 End UseCase 사용
                .onSuccess { qrEntity ->
                    _uiState.update { it.copy(qrEntity = qrEntity, isLoading = false) }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }
        }
    }

    // ----------------------------------------------------

    /**
     * QR 스캔 요청을 서버에 보내고 결과를 받아 상태를 업데이트합니다.
     */
    fun scanQR(requestEntity: QRScandEntity, qrType: QRTypes, matchId: Long) {
        _locateUiState.update { requestEntity }
        viewModelScope.launch {
            val result = sendQRScanResultUseCase(requestEntity, qrType, matchId)

            result.onSuccess { resultEntity ->
                _resultUiState.update { resultEntity }

                _uiState.update { current ->
                    current.copy(qrEntity = current.qrEntity.copy(qrScanned = true))
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isError = true) }
            }
        }
    }
}