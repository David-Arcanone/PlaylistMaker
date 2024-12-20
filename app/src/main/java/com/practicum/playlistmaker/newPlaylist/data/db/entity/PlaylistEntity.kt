package com.practicum.playlistmaker.newPlaylist.data.db.entity

import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.search.domain.models.Track

@Entity(tableName = "playlistsTable")
data class PlaylistEntity(
    @PrimaryKey
    val playlistName: String,
    val playlistDescription: String?,
    val playlistPicture: String?,
    val listOfTracks: String,
)
