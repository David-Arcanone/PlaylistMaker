package com.practicum.playlistmaker.newPlaylist.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.newPlaylist.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PlaylistEntity::class)
    suspend fun insertPlaylist(playlist:PlaylistEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist:PlaylistEntity)
    @Query("DELETE FROM playlistsTable WHERE id=:idToDelete")
    suspend fun deleteFromPlaylists(idToDelete:Int)

    @Query("SELECT * FROM playlistsTable ORDER BY playlistName DESC")
    fun getAllPlaylists():Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlistsTable WHERE id = :idToFind")
    suspend fun getPlaylist(idToFind:Int):List<PlaylistEntity>

    @Query("SELECT id FROM playlistsTable")
    suspend fun getPlaylistsIds(): List<Int>

}