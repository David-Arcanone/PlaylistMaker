package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.Track

interface PlayerRepository {
    fun getSavedTrack(): Track?
    fun preparePlayer(urlSong: String,onPlayerPreparedFunction:()->Unit,onPlayerCompletedFunction:()->Unit)
    fun startPlayer()
    fun pausePlayer()
    fun finishPlayer()
    fun getCurrentPosition():Int

}