package com.practicum.playlistmaker.playlistOverview.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.playlistOverview.data.db.entity.TrackAddedToPlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackAddedToPlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = TrackAddedToPlaylistEntity::class)
    suspend fun insertTrackAddedToPlaylistToStorage(trackData: TrackAddedToPlaylistEntity)
    @Query("SELECT * FROM tracksFromPlaylists ORDER BY trackName DESC")
    fun getAllTracksDataFromAllPlaylists():Flow<List<TrackAddedToPlaylistEntity>>//для получения всего списка дата данных;
    @Query("SELECT * FROM tracksFromPlaylists WHERE trackId=:id ORDER BY trackName DESC")
    suspend fun getSelectedTrackData(id:Int): List<TrackAddedToPlaylistEntity>//для поиска дата данных по конкретному треку
    @Query("SELECT track FROM tracksFromPlaylists WHERE trackId in (:listOfIds)")//получение списка треков по списку Id
    suspend fun getSelectedTracksDataFromList(listOfIds:List<Int>):List<String>
    @Query("DELETE FROM tracksFromPlaylists WHERE trackId=:id")//удаление дата данных
    suspend fun deleteTrackAddedToPlaylistFromStorage(id:Int)
    @Update(onConflict = OnConflictStrategy.REPLACE, entity = TrackAddedToPlaylistEntity::class)//обновляю при необходимости
    suspend fun updateTrackAddedToPlaylist(trackData: TrackAddedToPlaylistEntity)

}