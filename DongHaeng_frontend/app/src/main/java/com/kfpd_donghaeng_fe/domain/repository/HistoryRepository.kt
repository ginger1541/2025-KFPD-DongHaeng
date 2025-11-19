package com.kfpd_donghaeng_fe.domain.repository

import com.kfpd_donghaeng_fe.domain.entity.PlaceSearchResult
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    val searchHistoriesFlow: Flow<List<PlaceSearchResult>>
    suspend fun saveHistories(histories: List<PlaceSearchResult>)
}