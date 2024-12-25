package com.practicum.playlistmaker.editPlaylist.domain.models

import android.net.Uri
import com.practicum.playlistmaker.newPlaylist.domain.models.Playlist

sealed class EditPlaylistState {
    object NotInitState : EditPlaylistState()
    data class DoneInitState(val currentPlaylist: Playlist) : EditPlaylistState()
    data class DoneInitStateEditing(
        val pictureUri: Uri?,
        val isVerified: Boolean
    ) : EditPlaylistState()

    object NoSaveFound : EditPlaylistState()
}