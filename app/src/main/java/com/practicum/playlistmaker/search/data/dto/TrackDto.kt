package com.practicum.playlistmaker.search.data.dto

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class TrackDto(
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Int?,
    @SerializedName("artworkUrl100") val artWorkUrl100: String?,
    @SerializedName("trackId") val trackId: Int,
    @SerializedName("country") val country: String,
    @SerializedName("primaryGenreName") val primaryGenreName: String,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("releaseDate") val releaseDate: String?,
    @SerializedName("previewUrl") val previewUrl: String?
) {
    fun showTrackTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    }

    fun getCoverImg():String = artWorkUrl100?.replaceAfterLast('/', "512x512bb.jpg") ?:""

    fun getReleaseYear():String = releaseDate?.take(4) ?:""//год это первые 4 цифры
}