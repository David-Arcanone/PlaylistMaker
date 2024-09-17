package com.practicum.playlistmaker.domain.api

import android.os.Bundle
import com.practicum.playlistmaker.domain.models.SearchedTracks
import com.practicum.playlistmaker.domain.models.Track

interface SearchRepository {
    fun searchTracks(expression:String):SearchedTracks
    fun getSavedTrackHistory(): ArrayList<Track>
    fun saveTrackHistory(newValue: ArrayList<Track>)
    fun getSavedTrack(): Track?
    fun saveCurrentTrack(newValue: Track)
    fun getSavedInstanceEditTextValue(savedInstanceState: Bundle?):String
    fun saveInstanceEditTextValue(outState: Bundle, myText: String)
}