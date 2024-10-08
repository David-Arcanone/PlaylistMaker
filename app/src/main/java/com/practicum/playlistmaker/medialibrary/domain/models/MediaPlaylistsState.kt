package com.practicum.playlistmaker.medialibrary.domain.models

sealed class MediaPlaylistsState {
    object Default:MediaPlaylistsState()
    object NoPlaylists:MediaPlaylistsState()
}