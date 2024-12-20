package com.practicum.playlistmaker.medialibrary.domain.models

import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist

sealed class MediaPlaylistsState {
    object Default : MediaPlaylistsState()
    object NoPlaylists : MediaPlaylistsState()
    class PlaylistsFound(val listOfPlaylists: List<Playlist>):MediaPlaylistsState()
}