package com.practicum.playlistmaker.medialibrary.data.repository

import com.practicum.playlistmaker.medialibrary.data.converters.MusicFavoriteDbConvertor
import com.practicum.playlistmaker.medialibrary.data.db.AppPlaylistMakerDatabase
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FavoritesHistoryRepositoryImpl(
    private val myDatabase: AppPlaylistMakerDatabase,
    private val musicDbConvertor: MusicFavoriteDbConvertor
):FavoritesHistoryRepository {
    override fun getSavedFavorites(): Flow<List<Track>> = flow {
        val tracks = myDatabase.favoritesMusicDao().getFavorites()
        emit(convertFromMusicEntity(tracks))
    }
    private fun convertFromMusicEntity(tracks:List<MusicEntity>):List<Track>{
        return tracks.map{track->musicDbConvertor.map(track)}
    }
}