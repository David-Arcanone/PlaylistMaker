package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesHistoryInteractor {
    fun getSavedFavorites(): Flow<List<Track>>
}