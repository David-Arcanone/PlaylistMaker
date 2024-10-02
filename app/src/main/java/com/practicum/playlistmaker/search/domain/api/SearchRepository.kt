package com.practicum.playlistmaker.search.domain.api

import com.practicum.playlistmaker.search.domain.models.SearchedTracks
import com.practicum.playlistmaker.search.domain.models.Track

interface SearchRepository {
    fun searchTracks(expression:String): SearchedTracks
    fun getSavedTrackHistory(): List<Track>
    fun saveTrackHistory(newValue: List<Track>)
    fun getSavedTrack(): Track?
    fun saveCurrentTrack(newValue: Track)
    fun startPlayerActivity()

}