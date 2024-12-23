package com.practicum.playlistmaker.root.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.medialibrary.data.db.dao.FavoriteMusicDao
import com.practicum.playlistmaker.medialibrary.data.db.entity.MusicEntity
import com.practicum.playlistmaker.newPlaylist.data.db.dao.PlaylistsDao
import com.practicum.playlistmaker.newPlaylist.data.db.entity.PlaylistEntity
import com.practicum.playlistmaker.playlistOverview.data.db.dao.TrackAddedToPlaylistsDao
import com.practicum.playlistmaker.playlistOverview.data.db.entity.TrackAddedToPlaylistEntity

@Database(version = 9, entities = [MusicEntity::class, PlaylistEntity::class, TrackAddedToPlaylistEntity::class])
abstract class AppPlaylistMakerDatabase : RoomDatabase() {
    abstract fun favoritesMusicDao(): FavoriteMusicDao
    abstract fun playlistsDao(): PlaylistsDao
    abstract fun tracksAddedToPlaylistDao(): TrackAddedToPlaylistsDao
}