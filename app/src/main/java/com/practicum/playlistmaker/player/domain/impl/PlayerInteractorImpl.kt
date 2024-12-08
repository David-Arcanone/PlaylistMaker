package com.practicum.playlistmaker.player.domain.impl

import com.practicum.playlistmaker.player.domain.api.PlayerInteractor
import com.practicum.playlistmaker.player.domain.api.PlayerRepository
import com.practicum.playlistmaker.search.domain.consumer.TracksConsumer
import com.practicum.playlistmaker.search.domain.models.Track

class PlayerInteractorImpl(
    private val repository: PlayerRepository
) : PlayerInteractor {
    override fun getSavedTrack(consumer: TracksConsumer<Track>, doIfNoMatch: () -> Unit) {
        val currentTrack = repository.getSavedTrack()
        if (currentTrack != null) consumer.consume(currentTrack) else doIfNoMatch()
    }

    override fun startPlayer() {
        repository.startPlayer()
    }

    override fun pausePlayer() {
        repository.pausePlayer()
    }

    override fun preparePlayer(
        songUrl: String,
        onPlayerPreparedFunction: () -> Unit,
        onPlayerCompletedFunction: () -> Unit
    ) {
        repository.preparePlayer(songUrl, onPlayerPreparedFunction, onPlayerCompletedFunction)
    }

    override fun getCurrentMediaPosition(): Int = repository.getCurrentPosition()
    override fun finishPlayer() {
        repository.finishPlayer()
    }
}