package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.search.domain.models.Track

interface PlayerRepository {
    fun getSavedTrack(): Track?
}