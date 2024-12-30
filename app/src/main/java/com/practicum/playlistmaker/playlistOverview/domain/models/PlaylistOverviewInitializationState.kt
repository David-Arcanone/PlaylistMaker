package com.practicum.playlistmaker.playlistOverview.domain.models

import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist
import com.practicum.playlistmaker.search.domain.models.Track

sealed class PlaylistOverviewInitializationState {
    object NotInitState:PlaylistOverviewInitializationState()
    data class DoneInitStatePropertiesSheet(val currentPlaylist: Playlist):PlaylistOverviewInitializationState()
    data class DoneInitStateTracksSheet(val currentPlaylist: Playlist,val listOfTracks:List<Track>):PlaylistOverviewInitializationState()
    object NoSaveFound:PlaylistOverviewInitializationState()
}