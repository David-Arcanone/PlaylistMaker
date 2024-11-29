package com.practicum.playlistmaker.medialibrary.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity

@Dao
interface FavoriteMusicDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMusicToFavorite(music: MusicEntity)//для добавления трека в таблицу с избранными треками;

    @Delete(entity = MusicEntity::class)
    suspend fun deleteFromFavorites(musicEntity: MusicEntity)//для удаления трека из таблицы избранных треков;

    @Query("SELECT * FROM favorites_table order by when_added DESC")//чтоб сперва свежие фэйворит были
    suspend fun getFavorites():List<MusicEntity>//для получения списка со всеми треками, добавленными в избранное;

    @Query("SELECT trackId FROM favorites_table")
    suspend fun getFavoritesId():List<Int>


}