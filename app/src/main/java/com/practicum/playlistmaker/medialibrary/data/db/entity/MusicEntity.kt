package com.practicum.playlistmaker.medialibrary.data.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.Calendar

@Entity(tableName="favorites_table")
data class MusicEntity(
    @PrimaryKey
    val trackId: Int,
    val trackName: String,
    val artistName: String,
    val trackTime: Int?,
    val artWorkUrl100: String?,
    val country: String,
    val primaryGenreName: String,
    val collectionName: String?,
    val previewUrl: String?,
    val coverImg: String,
    val releaseYear: String?,
    val trackLengthText: String?,
    @ColumnInfo (name="when_added")
    val when_added:Long
)
