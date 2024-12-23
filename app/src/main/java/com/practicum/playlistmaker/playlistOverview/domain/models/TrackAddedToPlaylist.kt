package com.practicum.playlistmaker.playlistOverview.domain.models

import com.practicum.playlistmaker.search.domain.models.Track

data class TrackAddedToPlaylist(
    val trackId: Int,
    val track: Track,
    val listOfPlaylistsIds: List<Int>,
)
