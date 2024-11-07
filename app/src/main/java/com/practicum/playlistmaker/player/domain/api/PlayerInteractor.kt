package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerInteractor {
    fun getSavedTrack(consumer: TracksConsumer<Track>, doIfNoMatch:()->Unit)
}