package com.practicum.playlistmaker

import com.google.gson.annotations.SerializedName
import java.text.SimpleDateFormat
import java.util.Locale

data class Track(
    @SerializedName("trackName") val trackName:String,
    @SerializedName("artistName") val artistName:String,
    @SerializedName("trackTimeMillis") val trackTime:Int,
    @SerializedName("artworkUrl100") val artWorkUrl100:String
){
    fun showTrackTime():String{
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(trackTime)
    }
}
