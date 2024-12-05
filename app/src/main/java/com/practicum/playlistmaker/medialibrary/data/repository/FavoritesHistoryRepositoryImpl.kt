package com.practicum.playlistmaker.medialibrary.data.repository

import com.practicum.playlistmaker.medialibrary.data.converters.MusicFavoriteDbConvertor
import com.practicum.playlistmaker.medialibrary.data.db.AppPlaylistMakerDatabase
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesHistoryRepositoryImpl(
    private val myDatabase: AppPlaylistMakerDatabase,
    private val musicDbConvertor: MusicFavoriteDbConvertor,
    private val myMusicDbConvertor: MusicFavoriteDbConvertor
) : FavoritesHistoryRepository {
    override suspend fun addLike(newFavTrack: Track) {
        val trackConvertedToMusicEntity=myMusicDbConvertor.map(newFavTrack)
        myDatabase.favoritesMusicDao().insertMusicToFavorite(trackConvertedToMusicEntity)
    }
    override suspend fun deleteLike(badMusic: Track) {
        val trackConvertedToMusicEntity=myMusicDbConvertor.map(badMusic)
        myDatabase.favoritesMusicDao().deleteFromFavorites(trackConvertedToMusicEntity)
    }
    override fun getSavedFavorites(): Flow<List<Track>> =
        myDatabase.favoritesMusicDao().getFavorites().map {
            convertFromMusicEntity(it)
        }

    private fun convertFromMusicEntity(tracks: List<MusicEntity>): List<Track> {
        return tracks.map { track -> musicDbConvertor.map(track) }
    }
    override fun getListOfLikedId(): Flow<List<Int>> =
        myDatabase.favoritesMusicDao().getFavoritesId()



}