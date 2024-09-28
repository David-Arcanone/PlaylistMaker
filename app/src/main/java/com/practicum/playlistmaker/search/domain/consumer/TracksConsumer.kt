package com.practicum.playlistmaker.search.domain.consumer

interface TracksConsumer<T> {
    fun consume(foundTracks: T)
}