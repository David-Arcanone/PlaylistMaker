package com.practicum.playlistmaker.newPlaylist.domain.models

import android.net.Uri

data class NewPlaylistState(
    val pictureUri: Uri?,
    val isVerified: Boolean,
)
