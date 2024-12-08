package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE, entity = MusicEntity::class)
    suspend fun insertMusicToFavorite(track: MusicEntity)

    @Delete(entity = MusicEntity::class)
    suspend fun deleteFromFavorites(track:MusicEntity)//для удаления трека из таблицы избранных треков;

    @Query("SELECT * FROM favoritesTable ORDER BY whenAdded DESC")
    fun getFavorites():Flow<List<MusicEntity>>//для получения списка со всеми треками, добавленными в избранное;

    @Query("SELECT trackId FROM favoritesTable")
    fun getFavoritesId(): Flow<List<Int>>

}