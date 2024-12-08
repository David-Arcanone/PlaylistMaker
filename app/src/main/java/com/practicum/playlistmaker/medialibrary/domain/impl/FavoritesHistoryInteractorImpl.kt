package com.practicum.playlistmaker.medialibrary.domain.impl

import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryInteractor
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

class FavoritesHistoryInteractorImpl(private val myRepository: FavoritesHistoryRepository) :
    FavoritesHistoryInteractor {
    override suspend fun addLike(newFavTrack: Track){
        myRepository.addLike(newFavTrack)
    }
    override suspend fun deleteLike(badMusic:Track) {
        myRepository.deleteLike(badMusic)
    }
    override fun getSavedFavorites(): Flow<List<Track>> {
        return myRepository.getSavedFavorites()
    }
    override suspend fun getListOfLikedId(): Flow<List<Int>> {
        return myRepository.getListOfLikedId()
    }


}