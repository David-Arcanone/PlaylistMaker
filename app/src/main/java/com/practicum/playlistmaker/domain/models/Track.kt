package com.practicum.playlistmaker.domain.models

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackName") val trackName: String,
    @SerializedName("artistName") val artistName: String,
    @SerializedName("trackTimeMillis") val trackTime: Int?, //нужно для индикатора медиа плеера
    @SerializedName("artworkUrl100") val artWorkUrl100: String?,
    @SerializedName("trackId") val trackId: Int,
    @SerializedName("country") val country: String,
    @SerializedName("primaryGenreName") val primaryGenreName: String,
    @SerializedName("collectionName") val collectionName: String?,
    @SerializedName("previewUrl") val previewUrl: String?,
    @SerializedName("coverImg") val coverImg: String,
    @SerializedName("releaseYear") val releaseYear: String?,
    @SerializedName("trackLengthText") val trackLengthText: String?
)

