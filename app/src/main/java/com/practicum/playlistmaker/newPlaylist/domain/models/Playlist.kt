package com.practicum.playlistmaker.newPlaylist.domain.models

import android.net.Uri


data class Playlist(
    val id: Int,
    val playlistName:String,
    val playlistDescription:String?,
    val imgSrc: Uri?,
    val listOfTracks:List<Int>,
    val totalSeconds:Int
    )
