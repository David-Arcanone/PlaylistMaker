package com.practicum.playlistmaker.newPlaylist.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity
import com.practicum.playlistmaker.newPlaylist.data.db.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = PlaylistEntity::class)
    suspend fun insertPlaylist(playlist:PlaylistEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE, entity = PlaylistEntity::class)
    suspend fun updatePlaylist(playlist:PlaylistEntity)
    @Query("DELETE FROM playlistsTable WHERE playlistName=:name")
    suspend fun deleteFromPlaylists(name:String)

    @Query("SELECT * FROM playlistsTable ORDER BY playlistName DESC")
    fun getAllPlaylists():Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlistsTable WHERE playlistName = :name")
    suspend fun getPlaylist(name:String):List<PlaylistEntity>

    @Query("SELECT playlistName FROM playlistsTable")
    suspend fun getPlaylistsNames(): List<String>

}