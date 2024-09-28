package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerRepository {
    fun getSavedTrack(): Track?
    fun preparePlayer(urlSong: String,onPlayerPreparedFunction:()->Unit,onPlayerCompletedFunction:()->Unit)
    fun startPlayer()
    fun pausePlayer()
    fun finishPlayer()
    fun getCurrentPosition():Int

}