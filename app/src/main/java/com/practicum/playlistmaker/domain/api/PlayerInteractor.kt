package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.domain.models.Track

interface PlayerInteractor {
    fun getSavedTrack(consumer:TracksConsumer<Track>,doIfNoMatch:()->Unit)
    fun startPlayer()
    fun pausePlayer()
    fun preparePlayer(songUrl:String,onPlayerPreparedFunction:()->Unit,onPlayerCompletedFunction:()->Unit)
    fun finishPlayer()
    fun getCurrentMediaPosition():Int
}