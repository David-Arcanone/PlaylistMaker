package com.practicum.playlistmaker.playlistOverview.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "tracksFromPlaylists")
data class TrackAddedToPlaylistEntity(
    @PrimaryKey
    val trackId: Int,
    val track: String,
    val listOfPlaylistsIds: String,
    @ColumnInfo(name="trackName")
    val trackName: String,//для фильтрации
)
