package com.practicum.playlistmaker.domain.api

import android.os.Bundle
import com.practicum.playlistmaker.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.domain.models.SearchedTracks
import com.practicum.playlistmaker.domain.models.Track

interface SearchInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer<SearchedTracks>)
    fun getSavedTrackHistory(): ArrayList<Track>
    fun saveTrackHistory(newValue: ArrayList<Track>)
    fun clearHistory()
    fun getSavedTrack(): Track?
    fun saveCurrentTrack(newValue: Track)
    fun getSavedInstanceEditTextValue(savedInstanceState: Bundle?): String
}