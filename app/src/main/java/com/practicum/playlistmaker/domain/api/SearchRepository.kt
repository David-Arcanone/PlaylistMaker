package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.SearchedTracks
import com.practicum.playlistmaker.domain.models.Track

interface SearchRepository {
    fun searchTracks(expression:String):SearchedTracks
    fun getSavedTrackHistory(): ArrayList<Track>
    fun saveTrackHistory(newValue: ArrayList<Track>)
    fun getSavedTrack(): Track?
    fun saveCurrentTrack(newValue: Track)
}