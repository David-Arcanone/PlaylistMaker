package com.practicum.playlistmaker.domain.consumer

interface TracksConsumer<T> {
    fun consume(foundTracks: T)
}