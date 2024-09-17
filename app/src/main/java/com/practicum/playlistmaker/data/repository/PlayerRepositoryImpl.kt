package com.practicum.playlistmaker.data.repository

import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.domain.api.PlayerRepository
import com.practicum.playlistmaker.domain.models.Track

class PlayerRepositoryImpl(
    private val sharedPref: SharedPreferences,
    private val mediaPlayer: MediaPlayer
) : PlayerRepository {
    companion object {
        private const val PLAYLIST_CURRENT_TRACK = "playlist_current_track"
    }

    override fun getSavedTrack(): Track? {
        val trackType = object : TypeToken<Track>() {}
        val json = sharedPref.getString(PLAYLIST_CURRENT_TRACK, null) ?: return null
        return Gson().fromJson(json, trackType)
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun preparePlayer(
        urlSong: String,
        onPlayerPreparedFunction: () -> Unit,
        onPlayerCompletedFunction: () -> Unit
    ) {
        mediaPlayer.reset()
        mediaPlayer.setDataSource(urlSong)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { onPlayerPreparedFunction() }
        mediaPlayer.setOnCompletionListener { onPlayerCompletedFunction() }
    }

    override fun finishPlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition ?: 0
}