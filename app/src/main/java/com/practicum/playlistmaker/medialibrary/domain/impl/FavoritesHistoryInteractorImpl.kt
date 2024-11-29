package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesHistoryInteractorImpl(private val myRepository: FavoritesHistoryRepository) :
    FavoritesHistoryInteractor {
    override fun getSavedFavorites(): Flow<List<Track>> {
        return myRepository.getSavedFavorites()
    }
}