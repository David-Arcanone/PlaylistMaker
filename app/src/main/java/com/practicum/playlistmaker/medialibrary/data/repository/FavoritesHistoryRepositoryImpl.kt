package com.practicum.playlistmaker.medialibrary.data.repository

import com.practicum.playlistmaker.medialibrary.data.converters.MusicFavoriteDbConvertor
import com.practicum.playlistmaker.medialibrary.data.db.AppPlaylistMakerDatabase
import com.practicum.playlistmaker.medialibrary.data.db.dao.FavoriteMusicDao
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity
import com.practicum.playlistmaker.medialibrary.domain.db.FavoritesHistoryRepository
import com.practicum.playlistmaker.search.domain.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesHistoryRepositoryImpl(
    private val myDatabaseDao: FavoriteMusicDao,
    private val myMusicDbConvertor: MusicFavoriteDbConvertor
) : FavoritesHistoryRepository {
    override suspend fun addLike(newFavTrack: Track) {
        val trackConvertedToMusicEntity=myMusicDbConvertor.map(newFavTrack)
        myDatabaseDao.insertMusicToFavorite(trackConvertedToMusicEntity)
    }
    override suspend fun deleteLike(badMusic: Track) {
        val trackConvertedToMusicEntity=myMusicDbConvertor.map(badMusic)
        myDatabaseDao.deleteFromFavorites(trackConvertedToMusicEntity)
    }
    override fun getSavedFavorites(): Flow<List<Track>> =
        myDatabaseDao.getFavorites().map {
            convertFromMusicEntity(it)
        }

    private fun convertFromMusicEntity(tracks: List<MusicEntity>): List<Track> {
        return tracks.map { track -> myMusicDbConvertor.map(track) }
    }
    override fun getListOfLikedId(): Flow<List<Int>> =
        myDatabaseDao.getFavoritesId()



}