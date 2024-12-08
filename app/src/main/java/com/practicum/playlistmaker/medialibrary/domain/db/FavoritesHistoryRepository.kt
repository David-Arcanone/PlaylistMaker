package com.practicum.playlistmaker.medialibrary.domain.db

import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface FavoritesHistoryRepository {
    fun getSavedFavorites(): Flow<List<Track>>
    fun getListOfLikedId(): Flow<List<Int>>
    suspend fun addLike(newFavTrack:Track)
    suspend fun deleteLike(badMusic:Track)
}