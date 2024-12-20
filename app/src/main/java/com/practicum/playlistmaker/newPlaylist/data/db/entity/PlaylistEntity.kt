package com.practicum.playlistmaker.newPlaylist.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "playlistsTable")
data class PlaylistEntity(
    @PrimaryKey
    val playlistName: String,
    val playlistDescription: String?,
    val playlistPicture: String?,
    val listOfTracks: String,
)
