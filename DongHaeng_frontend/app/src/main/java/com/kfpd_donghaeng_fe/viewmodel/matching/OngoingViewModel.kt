package com.kfpd_donghaeng_fe.viewmodel.matching
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.kfpd_donghaeng_fe.ui.matching.MatchingState
import com.kfpd_donghaeng_fe.ui.matching.DHType
import androidx.compose.material3.Text

class OngoingViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MatchingState())
    val uiState = _uiState.asStateFlow()

    fun nextPage() {
        if (_uiState.value.OngoingPage < 2) {
            _uiState.update { it.copy(OngoingPage = it.OngoingPage + 1) }
        }
    }

    fun previousPage() {
        if (_uiState.value.OngoingPage > 0) {
            _uiState.update { it.copy(OngoingPage = it.OngoingPage - 1) }
        }
    }


}



