package com.practicum.playlistmaker.root.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.medialibrary.data.db.dao.FavoriteMusicDao
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity
import com.practicum.playlistmaker.newPlaylist.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.newPlaylist.data.db.entity.PlaylistEntity

@Database(version = 6, entities = [MusicEntity::class, PlaylistEntity::class])
abstract class AppPlaylistMakerDatabase : RoomDatabase() {
    abstract fun favoritesMusicDao(): FavoriteMusicDao
    abstract fun playlistsDao(): PlaylistsDao
}