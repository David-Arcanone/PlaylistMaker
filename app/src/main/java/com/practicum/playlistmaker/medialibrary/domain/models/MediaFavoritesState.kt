package com.practicum.playlistmaker.medialibrary.domain.models


sealed class MediaFavoritesState {
    object Default: MediaFavoritesState()
    object NoFavorites: MediaFavoritesState()
}