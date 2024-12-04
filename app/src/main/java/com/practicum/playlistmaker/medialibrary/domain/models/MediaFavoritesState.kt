package com.practicum.playlistmaker.medialibrary.domain.models

import com.practicum.playlistmaker.search.domain.models.Track


sealed class MediaFavoritesState {
    object Default: MediaFavoritesState()
    object NoFavorites: MediaFavoritesState()
    class Favorites(val favTracks:List<Track>):MediaFavoritesState()
}