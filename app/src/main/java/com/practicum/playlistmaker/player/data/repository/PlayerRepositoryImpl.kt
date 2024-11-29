package com.practicum.playlistmaker.player.data.repository

import android.content.SharedPreferences
import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.medialibrary.data.converters.MusicFavoriteDbConvertor
import com.practicum.playlistmaker.medialibrary.data.db.AppPlaylistMakerDatabase
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerRepositoryImpl(
    private val sharedPref: SharedPreferences,
    private val myGson: Gson,
    private val mediaPlayer: MediaPlayer,
    private val myDatabase: AppPlaylistMakerDatabase,
    private val myMusicDbConvertor: MusicFavoriteDbConvertor
) : PlayerRepository {
    override fun getSavedTrack(): Track? {
        val trackType = object : TypeToken<Track>() {}
        val json = sharedPref.getString(PLAYLIST_CURRENT_TRACK, null) ?: return null
        return myGson.fromJson(json, trackType)
    }

    override fun preparePlayer(
        urlSong: String,
        onPlayerPreparedFunction: () -> Unit,
        onPlayerCompletedFunction: () -> Unit
    ) {
        mediaPlayer.setDataSource(urlSong)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener { onPlayerPreparedFunction() }
        mediaPlayer.setOnCompletionListener { onPlayerCompletedFunction() }
    }

    override fun finishPlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    override fun startPlayer() {
        mediaPlayer.start()
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
    }

    override fun getCurrentPosition(): Int = mediaPlayer.currentPosition ?: 0

    companion object {
        private const val PLAYLIST_CURRENT_TRACK = "playlist_current_track"
    }
}