package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface SearchInteractor {
    fun searchTracks(expression: String):Flow<Pair<List<Track>,Boolean>>
    fun getSavedTrackHistory(): List<Track>
    fun saveTrackHistory(newValue: List<Track>)
    fun clearHistory()
    fun getSavedTrack(): Track?
    fun saveCurrentTrack(newValue: Track)
    fun updateHistoryWithNewTrack(newAddedTrack: Track):List<Track>
}