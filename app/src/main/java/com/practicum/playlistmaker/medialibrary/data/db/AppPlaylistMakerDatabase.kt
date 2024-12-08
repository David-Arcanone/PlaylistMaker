package com.practicum.playlistmaker.medialibrary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.medialibrary.data.db.dao.FavoriteMusicDao
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity

@Database(version = 2, entities = [MusicEntity::class])
abstract class AppPlaylistMakerDatabase : RoomDatabase() {
    abstract fun favoritesMusicDao(): FavoriteMusicDao
}