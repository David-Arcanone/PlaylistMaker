package com.practicum.playlistmaker.player.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerRepositoryImpl(
    private val sharedPref: SharedPreferences,
    private val myGson: Gson
) : PlayerRepository {
    override fun getSavedTrack(): Track? {
        val trackType = object : TypeToken<Track>() {}
        val json = sharedPref.getString(PLAYLIST_CURRENT_TRACK, null) ?: return null
        return myGson.fromJson(json, trackType)
    }
    companion object {
        private const val PLAYLIST_CURRENT_TRACK = "playlist_current_track"
    }
}