package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun getSavedTrack(consumer: TracksConsumer<Track>, doIfNoMatch: () -> Unit) {
        val currentTrack = repository.getSavedTrack()
        if (currentTrack != null) consumer.consume(currentTrack) else doIfNoMatch()
    }
}