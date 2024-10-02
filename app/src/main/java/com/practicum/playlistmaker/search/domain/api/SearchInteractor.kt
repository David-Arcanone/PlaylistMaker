package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.SearchedTracks
import com.practicum.playlistmaker.search.domain.models.Track

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer<SearchedTracks>)
    fun getSavedTrackHistory(): List<Track>
    fun saveTrackHistory(newValue: List<Track>)
    fun clearHistory()
    fun getSavedTrack(): Track?
    fun saveCurrentTrack(newValue: Track)
    fun updateHistoryWithNewTrack(newAddedTrack: Track):List<Track>
    fun startPlayerIntent()
}